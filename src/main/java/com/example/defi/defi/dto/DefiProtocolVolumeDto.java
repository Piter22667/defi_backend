package com.example.defi.defi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefiProtocolVolumeDto {
    private String project;
    private double volume_24h;
    private double volume_7d;
    private int rank;
}
