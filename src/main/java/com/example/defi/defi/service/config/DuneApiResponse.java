package com.example.defi.defi.service.config;

import com.example.defi.defi.dto.UniswapMonthVolumeDto;
import lombok.Data;

import java.util.List;

@Data
public class DuneApiResponse {
    private Result result;

    @Data
    public static class Result {
        private List<UniswapMonthVolumeDto> rows;
    }
}
