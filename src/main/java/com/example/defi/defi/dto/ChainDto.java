package com.example.defi.defi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChainDto {
    private String gecko_id;
    private double tvl;
    private String tokenSymbol;
    private String name;

    String getGecko_id() {
        return gecko_id;
    }

}
