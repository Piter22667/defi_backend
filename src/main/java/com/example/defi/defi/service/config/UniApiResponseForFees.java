package com.example.defi.defi.service.config;

import com.example.defi.defi.dto.UniswapMonthVolumeDto;
import com.example.defi.defi.dto.UniswapTotalFeePairsDto;
import lombok.Data;

import java.util.List;

@Data
public class UniApiResponseForFees {
    private Result result;

        @Data
        public static class Result {
            private List<UniswapTotalFeePairsDto> rows;
        }
}
