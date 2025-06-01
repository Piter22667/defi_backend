package com.example.defi.defi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PancakeMonthVolumeDto {

    private String month;
    private long user;
    private double volume;
}
