package com.example.defi.defi.service.impl;

import com.example.defi.defi.dto.AddressRequestDto;
import com.example.defi.defi.dto.TransactionDto;
import com.example.defi.defi.service.TransactionService;
import com.example.defi.defi.service.config.EtherScanApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Value("${etherscan.api.key}")
    private String etherscanApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String ETHERSCAN_API = "https://api.etherscan.io/v2/api?chainid=1&module=account&action=txlist&address=%s&startblock=0&endblock=99999999&page=1&offset=100&sort=asc&apikey=%s";

    public TransactionServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public List<TransactionDto> getTransactions(AddressRequestDto addressRequestDto) {
        String url = String.format(ETHERSCAN_API, addressRequestDto.getAddress(), etherscanApiKey);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        System.out.println("Response from etherscan apii: " + response.getBody());

        try{
            String responseBody = response.getBody();
            System.out.println("Response body from etherscan api: " + responseBody);
            EtherScanApiResponse etherScanApiResponse = objectMapper.readValue(responseBody, EtherScanApiResponse.class);

            return etherScanApiResponse.getResult().stream()
                    .map(tx -> {
                    TransactionDto dto = new TransactionDto();
                    dto.setFrom(tx.getFrom());
                    dto.setTo(tx.getTo());
                    dto.setValue(weiToEth(tx.getValue()));
                    dto.setIsError(tx.getIsError());
                    dto.setHash(tx.getHash());
                    dto.setTimeStamp(tx.getTimeStamp());
                    return dto;
                    })
                    .sorted((t1, t2) -> Long.compare(Long.parseLong(t2.getTimeStamp()), Long.parseLong(t1.getTimeStamp())))
                    .toList();



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String weiToEth(String wei) {
        BigDecimal eth = new BigDecimal(wei).divide(new BigDecimal("1000000000000000000"), 18, RoundingMode.HALF_UP);
        eth = eth.setScale(4, RoundingMode.HALF_UP);
        return eth.stripTrailingZeros().toPlainString(); //  stripTrailingZeros для видалення зайвих нулів
    }


}
