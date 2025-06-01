package com.example.defi.defi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {
    private String timeStamp;
    private String hash;
    private String from;
    private String to;
    private String value;
    private String isError;
}
