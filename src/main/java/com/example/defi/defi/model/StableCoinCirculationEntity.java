package com.example.defi.defi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "stable_coin_circulation")
@Data
public class StableCoinCirculationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long date;
    private double peggedUSD;


}
