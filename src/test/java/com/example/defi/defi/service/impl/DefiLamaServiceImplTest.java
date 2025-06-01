package com.example.defi.defi.service.impl;

import com.example.defi.defi.dto.ChainDto;
import com.example.defi.defi.dto.DexDto;
import com.example.defi.defi.dto.DexVolumeDto;
import com.example.defi.defi.dto.ProtocolDto;
import com.example.defi.defi.model.ChainTvlEntity;
import com.example.defi.defi.model.DexTvlEntity;
import com.example.defi.defi.repository.ChainTvlRepository;
import com.example.defi.defi.repository.DexTvlRepository;
import com.example.defi.defi.repository.StableCoinCirculationRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefiLamaServiceImplTest {

    @Mock
    private ChainTvlRepository chainTvlRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private DexTvlRepository dexTvlRepository;
    @Mock
    private StableCoinCirculationRepository stableCoinCirculationRepository;

    @InjectMocks
    private DefiLamaServiceImpl defiLamaServiceImpl;



   @Test
    void getTvlByChain_fetchesFromApi_whenDbIsEmpty() throws Exception {
        when(chainTvlRepository.findAll()).thenReturn(List.of());

        String apiResponse = "[{\"name\":\"Ethereum\",\"tvl\":20000000000.0,\"tokenSymbol\":\"ETH\"},{\"name\":\"Polygon\",\"tvl\":5000000000.0,\"tokenSymbol\":\"MATIC\"}]";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(apiResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

       DefiLamaServiceImpl realService = new DefiLamaServiceImpl(
               restTemplate,
               new ObjectMapper(),
               chainTvlRepository,
               dexTvlRepository,
               stableCoinCirculationRepository
       );

        ChainDto eth = new ChainDto();
        eth.setName("Ethereum");
        eth.setTvl(20.0);
        eth.setTokenSymbol("ETH");
        ChainDto matic = new ChainDto();
        matic.setName("Polygon");
        matic.setTvl(5.0);
        matic.setTokenSymbol("MATIC");


        List<ChainDto> result = realService.getTvlByChain();

        assertEquals(2, result.size());
        assertEquals("Ethereum", result.get(0).getName());
        verify(chainTvlRepository).saveAll(anyList());
    }



    @Test
    void getDexVolume_fetchesFromApi_whenDbIsEmpty() throws Exception {
        when(dexTvlRepository.findAll()).thenReturn(List.of());

        String apiResponse = "{\"protocols\":[{\"displayName\":\"Uniswap\",\"total7d\":15000000000.0},{\"displayName\":\"Sushiswap\",\"total7d\":3000000000.0}]}";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(apiResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

        DexVolumeDto dexVolumeDto = new DexVolumeDto();
        ProtocolDto uni = new ProtocolDto();
        uni.setDisplayName("Uniswap");
        uni.setTotal7d(15000000000.0);
        ProtocolDto sushi = new ProtocolDto();
        sushi.setDisplayName("Sushiswap");
        sushi.setTotal7d(3000000000.0);
        dexVolumeDto.setProtocols(List.of(uni, sushi));

        when(objectMapper.readValue(anyString(), eq(DexVolumeDto.class))).thenReturn(dexVolumeDto);

        DefiLamaServiceImpl realService = new DefiLamaServiceImpl(
                restTemplate,
                objectMapper,
                chainTvlRepository,
                dexTvlRepository,
                stableCoinCirculationRepository
        );

        List<DexDto> result = realService.getDexVolume();

        assertEquals(2, result.size());
        assertEquals("Uniswap", result.get(0).getDisplayName());
        assertEquals(15.0, result.get(0).getTotal7d());
        assertEquals("Sushiswap", result.get(1).getDisplayName());
        assertEquals(3.0, result.get(1).getTotal7d());
        verify(dexTvlRepository).saveAll(anyList());
    }
}
