package com.example.defi.defi.service.config;

import com.example.defi.defi.dto.PancakeMonthVolumeDto;
import com.example.defi.defi.dto.UniswapMonthVolumeDto;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
public class PancakeMonthVolumeApiResponse {

    private Result result;

    @Data
    public static class Result {
        private List<PancakeMonthVolumeDto> rows;
    }
}

