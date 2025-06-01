package com.example.defi.defi.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "uniswap_month_volume")
@Data
public class UniswapMonthVolume {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String month;
    private double volume;
}
