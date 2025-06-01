package com.example.defi.defi.service;

import com.example.defi.defi.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DuneService {

    List<UniswapMonthVolumeDto> getUniMonthVolume();

    List<UniswapTotalFeePairsDto> getUniTotalFeePairs();

    List<UniFeesByChainDto> getUniFeesByChain();


    List<PancakeMonthVolumeDto> getPancakeMonthVolume();

    List<PancakeMonthVolumeByChainDto> getPancakeMonthVolumeByChain();

    List<PancakeVolumeAndTransactionsOnBnbDto> getPancakeVolumeAndTransactionsOnBnb();

}
