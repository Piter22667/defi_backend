package com.example.defi.defi.service.config;

import com.example.defi.defi.dto.UniFeesByChainDto;
import com.example.defi.defi.dto.UniswapTotalFeePairsDto;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
public class UniFeeByChainResponse {
    private Result result;

    @Data
    public static class Result {
        private List<UniFeesByChainDto> rows;
    }
}
