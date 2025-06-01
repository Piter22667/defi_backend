package com.example.defi.defi.service.config;

import com.example.defi.defi.dto.PancakeVolumeAndTransactionsOnBnbDto;
import com.example.defi.defi.dto.UniswapTotalFeePairsDto;
import lombok.Data;

import java.util.List;

@Data
public class PancakeVolumeAndTransactionsOnBnbResponse {
    private Result result;

    @Data
    public static class Result {
        private List<PancakeVolumeAndTransactionsOnBnbDto> rows;
    }
}
