package com.example.defi.defi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pancake_month_volume_by_chain")
public class PancakeMonthVolumeByChainModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String blockchain;

    @Column(name = "user_count")
    private String user;
    private Double volume;


}
