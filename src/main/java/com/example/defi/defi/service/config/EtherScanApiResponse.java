package com.example.defi.defi.service.config;

import lombok.Data;

import java.util.List;

@Data
public class EtherScanApiResponse {
    private String status;
    private String message;
    private List<EtherScanTxDto> result;

    @Data
    public static class EtherScanTxDto {
        private String timeStamp;
        private String hash;
        private String from;
        private String to;
        private String value;
        private String isError;
    }
}
