package com.example.defi.defi.service.config;

import com.example.defi.defi.dto.PancakeMonthVolumeByChainDto;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
public class PancakeMonthVolumeByChainResponse {
    private Result result;

    @Data
    public static class Result {
        private List<PancakeMonthVolumeByChainDto> rows;
    }
}
