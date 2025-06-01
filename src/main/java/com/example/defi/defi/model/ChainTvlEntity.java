package com.example.defi.defi.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chain_tvl")
@Data
public class ChainTvlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String name;
    private String tokenSymbol;
    private Double tvl;

}
