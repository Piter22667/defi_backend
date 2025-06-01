package com.example.defi.defi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.defi.defi.dto.UniswapMonthVolumeDto;
import com.example.defi.defi.dto.UniswapTotalFeePairsDto;
import com.example.defi.defi.repository.UniFeeByPairRepository;
import com.example.defi.defi.repository.UniMonthVolumeRepository;
import com.example.defi.defi.repository.UniswapTotalFeePairsService;
import com.example.defi.defi.service.config.DuneApiResponse;
import com.example.defi.defi.service.config.UniApiResponseForFees;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class DuneServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UniMonthVolumeRepository uniMonthVolumeRepository;
    @Mock
    private UniswapTotalFeePairsService uniswapTotalFeePairsService;

    @InjectMocks
    private DuneServiceImpl duneService;

    @Test
    void getUniMonthVolume_fetchesFromApi_whenDbIsEmpty() throws Exception {
        when(uniMonthVolumeRepository.findAll()).thenReturn(List.of());

        String apiResponse = """
            {
              "result": {
                "rows": [
                  {"month": "2024-01-01 00:00:00.000 UTC", "volume": 10000000.0},
                  {"month": "2024-02-01 00:00:00.000 UTC", "volume": 20000000.0}
                ]
              }
            }
            """;
        ResponseEntity<String> responseEntity = ResponseEntity.ok(apiResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);


        UniswapMonthVolumeDto row1 = new UniswapMonthVolumeDto();
        row1.setMonth("2024-01-01 00:00:00.000 UTC");
        row1.setVolume(10000000.0);
        UniswapMonthVolumeDto row2 = new UniswapMonthVolumeDto();
        row2.setMonth("2024-02-01 00:00:00.000 UTC");
        row2.setVolume(20000000.0);

        DuneApiResponse.Result result = new DuneApiResponse.Result();
        result.setRows(List.of(row1, row2));
        DuneApiResponse duneApiResponse = new DuneApiResponse();
        duneApiResponse.setResult(result);

        when(objectMapper.readValue(anyString(), eq(DuneApiResponse.class))).thenReturn(duneApiResponse);

        List<UniswapMonthVolumeDto> resultList = duneService.getUniMonthVolume();

        assertEquals(2, resultList.size());
        assertEquals("2024-01", resultList.get(0).getMonth());
        assertEquals(10.0, resultList.get(0).getVolume());
        assertEquals("2024-02", resultList.get(1).getMonth());
        assertEquals(20.0, resultList.get(1).getVolume());
        verify(uniMonthVolumeRepository).saveAll(anyList());
    }





    @Test
    void getUniTotalFeePairs_fetchesFromApi_whenDbIsEmpty() throws Exception {
        // Мокаємо порожню БД
        when(uniswapTotalFeePairsService.findAll()).thenReturn(List.of());

        String apiResponse = """
            {
              "result": {
                "rows": [
                  {"trading_pair": "ETH/USDT", "total_fees_usd": 5000000.0},
                  {"trading_pair": "BTC/USDT", "total_fees_usd": 2000000.0}
                ]
              }
            }
            """;
        ResponseEntity<String> responseEntity = ResponseEntity.ok(apiResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

        // Мокаємо розпарсений респонс
        UniswapTotalFeePairsDto row1 = new UniswapTotalFeePairsDto();
        row1.setTrading_pair("ETH/USDT");
        row1.setTotal_fees_usd(5000000.0);
        UniswapTotalFeePairsDto row2 = new UniswapTotalFeePairsDto();
        row2.setTrading_pair("BTC/USDT");
        row2.setTotal_fees_usd(2000000.0);

        UniApiResponseForFees.Result result = new UniApiResponseForFees.Result();
        result.setRows(List.of(row1, row2));
        UniApiResponseForFees uniApiResponseForFees = new UniApiResponseForFees();
        uniApiResponseForFees.setResult(result);

        when(objectMapper.readValue(anyString(), eq(UniApiResponseForFees.class))).thenReturn(uniApiResponseForFees);

        List<UniswapTotalFeePairsDto> resultList = duneService.getUniTotalFeePairs();

        assertEquals(2, resultList.size());
        assertEquals("ETH/USDT", resultList.get(0).getTrading_pair());
        assertEquals(5.0, resultList.get(0).getTotal_fees_usd());
        assertEquals("BTC/USDT", resultList.get(1).getTrading_pair());
        assertEquals(2.0, resultList.get(1).getTotal_fees_usd());
        verify(uniswapTotalFeePairsService).saveAll(anyList());
    }

}