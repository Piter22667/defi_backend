package com.example.defi.defi.service.impl;

import com.example.defi.defi.dto.*;
import com.example.defi.defi.model.*;
import com.example.defi.defi.repository.*;
import com.example.defi.defi.service.DuneService;
import com.example.defi.defi.service.config.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DuneServiceImpl implements DuneService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UniMonthVolumeRepository uniMonthVolumeRepository;
    private final UniswapTotalFeePairsService uniswapTotalFeePairsService;
    private final UniFeesByChainRepository uniFeesByChainRepository;

    private final PancakeMonthVolumeRepository pancakeMonthVolumeRepository;
    private final PancakeMonthVolumeByChainRepository pancakeMonthVolumeByChainRepository;
    private final PancakeVolumeAndTransactionsOnBnbRepository pancakeVolumeAndTransactionsOnBnbRepository;

    @Value("${dune.api.key}") //changed from application properties //todo changed from application propertie
    String apiKey;


    public DuneServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper, UniMonthVolumeRepository uniMonthVolumeRepository, UniswapTotalFeePairsService uniswapTotalFeePairsService, UniFeesByChainRepository uniFeesByChainRepository, PancakeMonthVolumeRepository pancakeMonthVolumeRepository, PancakeMonthVolumeByChainRepository pancakeMonthVolumeByChainRepository, PancakeVolumeAndTransactionsOnBnbRepository pancakeVolumeAndTransactionsOnBnbRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.uniMonthVolumeRepository = uniMonthVolumeRepository;
        this.uniswapTotalFeePairsService = uniswapTotalFeePairsService;
        this.uniFeesByChainRepository = uniFeesByChainRepository;
        this.pancakeMonthVolumeRepository = pancakeMonthVolumeRepository;
        this.pancakeMonthVolumeByChainRepository = pancakeMonthVolumeByChainRepository;
        this.pancakeVolumeAndTransactionsOnBnbRepository = pancakeVolumeAndTransactionsOnBnbRepository;
        this.apiKey = apiKey;
    }

    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS z");
    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");


    @Override
    public List<UniswapMonthVolumeDto> getUniMonthVolume() {
        //Перевіряємо чи є записи в бд, якщо ендпоїнт вже тригерився, то повертаємо з бд

        List<UniswapMonthVolume> savedVolumes = uniMonthVolumeRepository.findAll();
        if(!savedVolumes.isEmpty()) {
            return savedVolumes.stream()
                    .map(savedVolume -> {
                        UniswapMonthVolumeDto dto = new UniswapMonthVolumeDto();
                        dto.setVolume(savedVolume.getVolume());
                        dto.setMonth(savedVolume.getMonth());
                        return dto;
                    })
                    .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                    .toList();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Dune-Api-Key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "https://api.dune.com/api/v1/query/5190990/results?limit=1000";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Response: " + response.getBody());

        try {
                String responseBody = response.getBody();
                DuneApiResponse duneApiResponse = objectMapper.readValue(responseBody, DuneApiResponse.class);
                Map<String, Double> volumeByMonth = duneApiResponse.getResult().getRows().stream()
                        .collect(Collectors.groupingBy(
                                dto -> ZonedDateTime.parse(dto.getMonth(), inputFormatter).format(outputFormatter),
                                Collectors.summingDouble(dto -> Math.round(dto.getVolume() / 1_000_000.0 * 100.0) / 100.0)
                        ));

                List<UniswapMonthVolumeDto> result = volumeByMonth.entrySet().stream()
                        .map(entry -> new UniswapMonthVolumeDto(entry.getKey(), entry.getValue()))
                        .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                        .toList();


                //якщо обробка ендпоінту успішна, то зберігаємо в бд
                List<UniswapMonthVolume> uniswapMonthVolumes = result.stream()
                        .map(dto -> {
                            UniswapMonthVolume uniswapMonthVolume = new UniswapMonthVolume ();
                            uniswapMonthVolume.setVolume(dto.getVolume());
                            uniswapMonthVolume.setMonth(dto.getMonth());
                            return uniswapMonthVolume;
                        }).toList();

                uniMonthVolumeRepository.saveAll(uniswapMonthVolumes);
                log.info("Saved uniswap month volume: {}", uniswapMonthVolumes);


            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UniswapTotalFeePairsDto> getUniTotalFeePairs() {
        //Перевіряємо чи є записи в бд, якщо ендпоїнт вже тригерився, то повертаємо з бд
        List<UniswapTotalFeePairsModel> savedTotalFeePairs = uniswapTotalFeePairsService.findAll();
        if(!savedTotalFeePairs.isEmpty()) {
            return savedTotalFeePairs.stream()
                    .map(savedPair -> {
                        UniswapTotalFeePairsDto dto = new UniswapTotalFeePairsDto();
                        dto.setTrading_pair(savedPair.getTrading_pair());
                        dto.setTotal_fees_usd(savedPair.getTotal_fees_usd());
                        return dto;
                    })
                    .sorted((a, b) -> Double.compare(
                            b.getTotal_fees_usd(), a.getTotal_fees_usd()))
                    .toList();
        }


        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Dune-Api-Key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = "https://api.dune.com/api/v1/query/44612/results?limit=15";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Response: " + response.getBody());

        try {
            {
                String responseBody = response.getBody();
                UniApiResponseForFees uniApiResponseForFees = objectMapper.readValue(responseBody, UniApiResponseForFees.class);

                List<UniswapTotalFeePairsDto> uniFeeTobeSaved=  uniApiResponseForFees.getResult().getRows().stream()
                        .map(
                                dto -> {
                                    UniswapTotalFeePairsDto totalFeePairsDto = new UniswapTotalFeePairsDto();
                                    totalFeePairsDto.setTrading_pair(dto.getTrading_pair());
                                    totalFeePairsDto.setTotal_fees_usd(Math.round(dto.getTotal_fees_usd() / 1_000_000.0 * 100.0) / 100.0);
                                    return totalFeePairsDto;
                                }
                        )
                        .sorted((a, b) -> Double.compare(
                                b.getTotal_fees_usd(), a.getTotal_fees_usd()))
                        .toList();

                //якщо обробка ендпоінту успішна, то зберігаємо в бд
                List<UniswapTotalFeePairsModel> uniswapTotalFeePairsModels = uniFeeTobeSaved.stream()
                        .map(dto -> {
                            UniswapTotalFeePairsModel uniswapTotalFeePairsModel = new UniswapTotalFeePairsModel();
                            uniswapTotalFeePairsModel.setTrading_pair(dto.getTrading_pair());
                            uniswapTotalFeePairsModel.setTotal_fees_usd(dto.getTotal_fees_usd());
                            return uniswapTotalFeePairsModel;
                        }).toList();
                uniswapTotalFeePairsService.saveAll(uniswapTotalFeePairsModels);
                log.info("Saved uniswap total fee pairs: {}", uniswapTotalFeePairsModels);



                return uniFeeTobeSaved;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UniFeesByChainDto> getUniFeesByChain() {
        List<UniFeesByChainModel> savedUniFeesByChain = uniFeesByChainRepository.findAll();
        if(!savedUniFeesByChain.isEmpty()) {
            return savedUniFeesByChain.stream()
                    .map(savedFee -> {
                        UniFeesByChainDto dto = new UniFeesByChainDto();
                        dto.setChain(savedFee.getChain());
                        dto.setTotal_swap_fee_usd(savedFee.getTotal_swap_fee_usd());
                        return dto;
                    })
                    .sorted((a, b) -> Double.compare(
                            b.getTotal_swap_fee_usd(), a.getTotal_swap_fee_usd()))
                    .toList();
        }


        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Dune-Api-Key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = "https://api.dune.com/api/v1/query/4635573/results?limit=12";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Response: " + response.getBody());

        try {
            {
                String responseBody = response.getBody();
                UniFeeByChainResponse uniFeeByChainResponse = objectMapper.readValue(responseBody, UniFeeByChainResponse.class);

                List<UniFeesByChainDto> uniFeeToBeSaved = uniFeeByChainResponse.getResult().getRows().stream()
                        .map(dto -> {
                            dto.setChain(dto.getChain());
                            dto.setTotal_swap_fee_usd(
                                    Math.round(dto.getTotal_swap_fee_usd() / 1_000_000.0 * 100.0) / 100.0);
                            return dto;
                        })
                        .sorted((a, b) -> Double.compare(
                                b.getTotal_swap_fee_usd(), a.getTotal_swap_fee_usd()))
                        .toList();


                List<UniFeesByChainModel> uniFeesByChainModels = uniFeeToBeSaved.stream()
                        .map(dto -> {
                            UniFeesByChainModel uniFeesByChainModel = new UniFeesByChainModel();
                            uniFeesByChainModel.setChain(dto.getChain());
                            uniFeesByChainModel.setTotal_swap_fee_usd(dto.getTotal_swap_fee_usd());
                            return uniFeesByChainModel;
                        }).toList();

                uniFeesByChainRepository.saveAll(uniFeesByChainModels);
                log.info("Saved uniswap fee by chain : {}", uniFeesByChainModels);


                return uniFeeToBeSaved;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }}



    //Pancake methods

    @Override
    public List<PancakeMonthVolumeDto> getPancakeMonthVolume() {
        List<PancakeMonthVolumeModel> savedVolumes = pancakeMonthVolumeRepository.findAll();
        if(!savedVolumes.isEmpty()) {
            return savedVolumes.stream()
                    .map(savedVolume -> {
                        PancakeMonthVolumeDto dto = new PancakeMonthVolumeDto();
                        dto.setVolume(savedVolume.getVolume());
                        dto.setMonth(savedVolume.getMonth());
                        dto.setUser(savedVolume.getUser());
                        return dto;
                    })
                    .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                    .toList();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Dune-Api-Key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "https://api.dune.com/api/v1/query/2347637/results?limit=100";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Response: " + response.getBody());

        try {
            {
                String responseBody = response.getBody();
                PancakeMonthVolumeApiResponse pancake = objectMapper.readValue(responseBody, PancakeMonthVolumeApiResponse.class);

                List<PancakeMonthVolumeDto> pancakeMonth = pancake.getResult().getRows().stream()
                        .map(dto -> {
                            dto.setMonth(dto.getMonth());
                            dto.setVolume(dto.getVolume());
                            dto.setUser(dto.getUser());
                            return dto;
                        })
                        .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                        .toList();


                //якщо обробка ендпоінту успішна, то зберігаємо в бд
                List<PancakeMonthVolumeModel> pancakeMonthVolumeModels = pancakeMonth.stream()
                        .map(dto -> {
                            PancakeMonthVolumeModel pancakeMonthVolume = new PancakeMonthVolumeModel();
                            pancakeMonthVolume.setMonth(dto.getMonth());
                            pancakeMonthVolume.setVolume(dto.getVolume());
                            pancakeMonthVolume.setUser(dto.getUser());
                            return pancakeMonthVolume;
                        }).toList();

                pancakeMonthVolumeRepository.saveAll(pancakeMonthVolumeModels);
                log.info("Saved pancake month volume: {}", pancakeMonthVolumeModels);

                return pancakeMonth;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PancakeMonthVolumeByChainDto> getPancakeMonthVolumeByChain() {
        //Перевіряємо чи є записи в бд, якщо ендпоїнт вже тригерився, то повертаємо з бд
       List<PancakeMonthVolumeByChainModel> savedVolumes = pancakeMonthVolumeByChainRepository.findAll();
       if(!savedVolumes.isEmpty()) {
            return savedVolumes.stream()
                    .map(savedVolume -> {
                        PancakeMonthVolumeByChainDto dto = new PancakeMonthVolumeByChainDto();
                        dto.setBlockchain(savedVolume.getBlockchain());
                        dto.setVolume(savedVolume.getVolume());
                        dto.setUser(savedVolume.getUser());
                        return dto;
                    })
                    .sorted((a, b) -> a.getVolume().compareTo(b.getVolume()))
                    .toList();
        }


        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Dune-Api-Key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = "https://api.dune.com/api/v1/query/5195468/results?limit=20";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Response: " + response.getBody());

        try {
            {
                String responseBody = response.getBody();
                PancakeMonthVolumeByChainResponse pancake = objectMapper.readValue(responseBody, PancakeMonthVolumeByChainResponse.class);

                List<PancakeMonthVolumeByChainDto> pancakeByChainDto = pancake.getResult().getRows().stream()
                        .map(dto -> {
                           dto.setVolume(dto.getVolume());
                            dto.setBlockchain(dto.getBlockchain());
                            dto.setVolume(dto.getVolume());
                            return dto;
                        })
                        .sorted((a, b) -> a.getVolume().compareTo(b.getVolume()))
                        .toList();

                //якщо обробка ендпоінту успішна, то зберігаємо в бд
                List<PancakeMonthVolumeByChainModel> pancakeByChainModel = pancakeByChainDto.stream()
                        .map(dto -> {
                            PancakeMonthVolumeByChainModel pancakeMonthVolumeByChainModel = new PancakeMonthVolumeByChainModel();
                            pancakeMonthVolumeByChainModel.setBlockchain(dto.getBlockchain());
                            pancakeMonthVolumeByChainModel.setVolume(dto.getVolume());
                            pancakeMonthVolumeByChainModel.setUser(dto.getUser());
                            return pancakeMonthVolumeByChainModel;
                        }).toList();

                pancakeMonthVolumeByChainRepository.saveAll(pancakeByChainModel);
                log.info("Saved pancake month volume by chain: {}", pancakeByChainModel);

                return pancakeByChainDto;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PancakeVolumeAndTransactionsOnBnbDto> getPancakeVolumeAndTransactionsOnBnb() {
        //Перевіряємо чи є записи в бд, якщо ендпоїнт вже тригерився, то повертаємо з бд
        List<PancakeVolumeAndTransactionsOnBnbModel> savedVolumes = pancakeVolumeAndTransactionsOnBnbRepository.findAll();
        if(!savedVolumes.isEmpty()) {
            return savedVolumes.stream()
                    .map(savedVolume -> {
                        PancakeVolumeAndTransactionsOnBnbDto dto = new PancakeVolumeAndTransactionsOnBnbDto();
                        dto.setMonth(savedVolume.getMonth());
                        dto.setVolume(savedVolume.getVolume());
                        dto.setTransactions(savedVolume.getTransactions());
                        dto.setUser(savedVolume.getUser());
                        return dto;
                    })
                    .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                    .toList();
        }


        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Dune-Api-Key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);


        String url = "https://api.dune.com/api/v1/query/5195576/results?limit=1000";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Response: " + response.getBody());

        try {
            {
                String responseBody = response.getBody();
                PancakeVolumeAndTransactionsOnBnbResponse pancake = objectMapper.readValue(responseBody, PancakeVolumeAndTransactionsOnBnbResponse.class);

                List<PancakeVolumeAndTransactionsOnBnbDto> pancakeBnb = pancake.getResult().getRows().stream()
                        .map(dto -> {
                            dto.setVolume(dto.getVolume());
                            dto.setTransactions(dto.getTransactions());
                            dto.setUser(dto.getUser());
                            dto.setMonth(dto.getMonth());
                            return dto;
                        })
                        .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                        .toList();

                //якщо обробка ендпоінту успішна, то зберігаємо в бд
                List<PancakeVolumeAndTransactionsOnBnbModel> pancakeBnbModel = pancakeBnb.stream()
                        .map(dto -> {
                            PancakeVolumeAndTransactionsOnBnbModel pancakeVolumeAndTransactionsOnBnbModel = new PancakeVolumeAndTransactionsOnBnbModel();
                            pancakeVolumeAndTransactionsOnBnbModel.setMonth(dto.getMonth());
                            pancakeVolumeAndTransactionsOnBnbModel.setVolume(dto.getVolume());
                            pancakeVolumeAndTransactionsOnBnbModel.setTransactions(dto.getTransactions());
                            pancakeVolumeAndTransactionsOnBnbModel.setUser(dto.getUser());
                            return pancakeVolumeAndTransactionsOnBnbModel;
                        }).toList();

                pancakeVolumeAndTransactionsOnBnbRepository.saveAll(pancakeBnbModel);
                log.info("Saved pancake bnb month volume: {}", pancakeBnbModel);



                return pancakeBnb;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
