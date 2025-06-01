package com.example.defi.defi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dex_tvl")
@Data
public class DexTvlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String displayName;
    private double total7d;

}
