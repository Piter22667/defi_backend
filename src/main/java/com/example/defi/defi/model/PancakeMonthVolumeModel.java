package com.example.defi.defi.model;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Table(name = "pancake_month_volume")
@Data
public class PancakeMonthVolumeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String month;

    @Column(name = "user_count")
    private long user;
    private double volume;


}
