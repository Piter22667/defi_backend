package com.example.defi.defi.repository;

import com.example.defi.defi.dto.PancakeMonthVolumeDto;
import com.example.defi.defi.model.PancakeMonthVolumeModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PancakeMonthVolumeRepository extends JpaRepository<PancakeMonthVolumeModel, Long> {
}
