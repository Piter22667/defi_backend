package com.example.defi.defi.service.impl;

import com.example.defi.defi.dto.*;
import com.example.defi.defi.model.ChainTvlEntity;
import com.example.defi.defi.model.DexTvlEntity;
import com.example.defi.defi.model.StableCoinCirculationEntity;
import com.example.defi.defi.repository.ChainTvlRepository;
import com.example.defi.defi.repository.DexTvlRepository;
import com.example.defi.defi.repository.StableCoinCirculationRepository;
import com.example.defi.defi.service.DefiLamaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefiLamaServiceImpl implements DefiLamaService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final ChainTvlRepository chainTvlRepository;
    private final DexTvlRepository dexTvlRepository;
    private final StableCoinCirculationRepository stableCoinCirculationRepository;

    public DefiLamaServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper, ChainTvlRepository chainTvlRepository, DexTvlRepository dexTvlRepository, StableCoinCirculationRepository stableCoinCirculationRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.chainTvlRepository = chainTvlRepository;
        this.dexTvlRepository = dexTvlRepository;
        this.stableCoinCirculationRepository = stableCoinCirculationRepository;
    }


    @Override
    public List<ChainDto> getTvlByChain() {
        List<ChainTvlEntity> savedEntities = chainTvlRepository.findAll();
        if(!savedEntities.isEmpty()) {
            return savedEntities.stream()
                    .map(entity -> {
                        ChainDto dto = new ChainDto();
                        dto.setName(entity.getName());
                        dto.setTvl(entity.getTvl());
                        dto.setTokenSymbol(entity.getTokenSymbol());
                        return dto;
                    })
                    .toList();
        }

        String url = "https://api.llama.fi/v2/chains\n";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        System.out.println(response.getBody());

        try{

            List<ChainDto> chainDtos = objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, ChainDto.class));
            chainDtos.forEach(chainDto -> chainDto.setTvl(Math.round(chainDto.getTvl() / 1_000_000_000.0 * 100.0) / 100.0));

            List<ChainDto> sortedChains = chainDtos.stream()
                    .sorted((c1, c2) -> Double.compare(c2.getTvl(), c1.getTvl()))
                    .toList(); // Sort by TVL in descending order

            List<ChainDto> topTenChains = new ArrayList<>(sortedChains.stream()
            .limit(10) // Get the first 10 elements
                    .toList());

            System.out.println(topTenChains + " top ten chains");

            double othersTvl = sortedChains.stream()
                    .skip(10) // Skip the first 10 elements
                    .mapToDouble(ChainDto::getTvl)
                    .sum();
            System.out.println("Others TVL: " + othersTvl);

            if(othersTvl > 0) {
                ChainDto others = new ChainDto();
                others.setName("Others");
                others.setTvl(othersTvl);
                topTenChains = new ArrayList<>(topTenChains);
                topTenChains.add(others);
            }

            System.out.println(topTenChains + " top ten chains after adding others");


            //зберігаємо в бд, якщо обробка ендпоінту успішна
            List<ChainTvlEntity> chainTvlEntities = topTenChains.stream()
                    .map(dto -> {
                        ChainTvlEntity chainTvlEntity = new ChainTvlEntity();
                        chainTvlEntity.setName(dto.getName());
                        chainTvlEntity.setTvl(dto.getTvl());
                        chainTvlEntity.setTokenSymbol(dto.getTokenSymbol());
                        return chainTvlEntity;
                    }).toList();

            chainTvlRepository.saveAll(chainTvlEntities);
            log.info("Saved {} chain TVLs", chainTvlEntities.size());

            return topTenChains;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<DexDto> getDexVolume() {
        List<DexTvlEntity> savedEntities = dexTvlRepository.findAll();
        if(!savedEntities.isEmpty()) {
            return savedEntities.stream()
                    .map(entity -> {
                        DexDto dto = new DexDto();
                        dto.setDisplayName(entity.getDisplayName());
                        dto.setTotal7d(entity.getTotal7d());
                        return dto;
                    })
                    .toList();
        }

        String url = "https://api.llama.fi/overview/dexs?excludeTotalDataChart=true&excludeTotalDataChartBreakdown=true&dataType=dailyVolume";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        try{
            DexVolumeDto dexVolumeDto = objectMapper.readValue(response.getBody(), DexVolumeDto.class);

            List<ProtocolDto> sortedProtocols = dexVolumeDto.getProtocols().stream()
                    .sorted((p1, p2) -> Double.compare(p2.getTotal7d(), p1.getTotal7d()))
                    .toList();

            List<DexDto> toptenDex = sortedProtocols.stream()
                    .limit(15)
                    .map(protocol -> {
                        DexDto dexDto = new DexDto();
                        dexDto.setDisplayName(protocol.getDisplayName());
                        dexDto.setTotal7d(Math.round(protocol.getTotal7d() / 1_000_000_000.0 * 100.0) / 100.0);
                        return dexDto;
                    }).toList();

            log.info("{} amount of DEXs top 15 DEXs{}", toptenDex.size(), toptenDex);

            double othersTotal = sortedProtocols.stream()
                    .skip(15)
                    .mapToDouble(ProtocolDto::getTotal7d)
                    .sum();

            if (othersTotal > 0) {
                DexDto others = new DexDto();
                others.setDisplayName("Others");
                others.setTotal7d(Math.round(othersTotal / 1_000_000_000.0 * 100.0) / 100.0);
                toptenDex = new ArrayList<>(toptenDex);
                toptenDex.add(others);
            }

            //якщо обробка ендпоінту успішна, то зберігаємо в бд
            List<DexTvlEntity> dexTvlEntities = toptenDex.stream()
                    .map(dto -> {
                        DexTvlEntity dexTvlEntity = new DexTvlEntity();
                       dexTvlEntity.setDisplayName(dto.getDisplayName());
                        dexTvlEntity.setTotal7d(dto.getTotal7d());
                        return dexTvlEntity;
                    }).toList();

            dexTvlRepository.saveAll(dexTvlEntities);
            log.info("Saved {} dex Volumes", dexTvlEntities.size());

            return toptenDex;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<UsdCirculationDto> getUsdCirculation() {
        List<StableCoinCirculationEntity> existCirculation = stableCoinCirculationRepository.findAll();
        if(!existCirculation.isEmpty()) {
            return existCirculation.stream()
                    .map(entity -> {
                        UsdCirculationDto.Circulating circulating = new UsdCirculationDto.Circulating();
                        circulating.setPeggedUSD(entity.getPeggedUSD());

                        UsdCirculationDto dto = new UsdCirculationDto();
                        dto.setDate(entity.getDate());
                        dto.setTotalCirculatingUSD(circulating);
                        return dto;

                    })
                    .toList();
        }

        String url = "https://stablecoins.llama.fi/stablecoincharts/all?stablecoin=1";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        if (response.getBody() == null || response.getBody().isEmpty()) {
            DefiLamaServiceImpl.log.error("Error fethching data from API");
            return new ArrayList<>();
        }

        try{
            List<UsdCirculationDto> usdCirculationDto = objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, UsdCirculationDto.class));

            // сортуємо по роках
            Map<Integer, List<UsdCirculationDto>> byYear = usdCirculationDto.stream()
                    .collect(Collectors.groupingBy(dto -> {
                        long unix = dto.getDate();
                        LocalDate date = Instant.ofEpochSecond(unix).atZone(ZoneOffset.UTC).toLocalDate();
                        return date.getYear();
                    }));

            List<UsdCirculationDto> groupedCirculationByPeriod = new ArrayList<>();

            for(Map.Entry<Integer, List<UsdCirculationDto>> entry : byYear.entrySet()) {
                List<UsdCirculationDto> yearList = entry.getValue().stream()
                        .sorted((dto1, dto2) -> Long.compare(dto1.getDate(), dto2.getDate()))
                        .toList(); // сортуємо по даті в межах року (всі записи в межах одного року)

                if(!yearList.isEmpty()) {
                    groupedCirculationByPeriod.add(yearList.get(0)); //  беремо перший запис року
                    System.out.println("Year list: " + yearList);
                }
                if (yearList.size() > 1) {
                    groupedCirculationByPeriod.add(yearList.get(yearList.size() - 1));
                }
            }

            if (groupedCirculationByPeriod.size() > 16) {
                groupedCirculationByPeriod = groupedCirculationByPeriod.stream()
                        .sorted((a, b) -> Long.compare(a.getDate(), b.getDate()))
                        .limit(16)
                        .toList();
            }


            List<StableCoinCirculationEntity> usdEntity = groupedCirculationByPeriod.stream()
                    .map(dto -> {
                        StableCoinCirculationEntity entity = new StableCoinCirculationEntity();
                        entity.setDate(dto.getDate());
                        entity.setPeggedUSD(dto.getTotalCirculatingUSD().getPeggedUSD());
                        return entity;
                    }).toList();
            stableCoinCirculationRepository.saveAll(usdEntity);
            log.info("Saved {} stable coin circulation", usdEntity);

            return groupedCirculationByPeriod;
            //todo: глибше вникнути в логіку, чому так, можливо, треба буде змінити
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
