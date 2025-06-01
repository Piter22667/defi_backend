package com.example.defi.defi.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "stable_coin_circulation")
@Data
public class UniFeeByChain {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String chain;
    private String total_swap_fee_usd;

}
