package com.example.defi.defi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "uniswap_total_fee_pairs")
@Data
public class UniswapTotalFeePairsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String trading_pair;
    private Double total_fees_usd;
}
