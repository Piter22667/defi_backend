package com.example.defi.defi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PancakeVolumeAndTransactionsOnBnbDto {

    private String month;
    private Long transactions;
    private Long  user;
    private double volume;


}
