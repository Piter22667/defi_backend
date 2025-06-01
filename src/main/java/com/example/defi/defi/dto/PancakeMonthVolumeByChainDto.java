package com.example.defi.defi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PancakeMonthVolumeByChainDto {

    private String blockchain;
    private String user;
    private Double volume;
}
