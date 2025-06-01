package com.example.defi.defi.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "uni_fees_by_chain")
@Data
public class UniFeesByChainModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String chain;
    private Double total_swap_fee_usd;
}
