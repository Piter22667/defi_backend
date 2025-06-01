package com.example.defi.defi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProtocolDto {
    private double total24h;
    private double total48hto24h;
    private double total7d;
    private double total14dto7d;
    private double total60dto30d;
    private double total30d;
    private double total1y;
    private double totalAllTime;
    private double average1y;
    private double change_1d;
    private double change_7d;
    private double change_1m;
    private double change_7dover7d;
    private double change_30dover30d;
    private double total7DaysAgo;
    private double total30DaysAgo;
    private String defillamaId;
    private String name;
    private String displayName;
    private String module;
    private String category;
    private String logo;
    private List<String> chains;
    private String protocolType;
    private String methodologyURL;
    private Map<String, Map<String, Double>> breakdown24h;
    private Map<String, Map<String, Double>> breakdown30d;
}