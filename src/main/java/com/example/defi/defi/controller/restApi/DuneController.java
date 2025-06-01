package com.example.defi.defi.controller.restApi;

import com.example.defi.defi.dto.*;
import com.example.defi.defi.service.DuneService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dune")
public class DuneController {

    private final DuneService duneService;

    public DuneController(DuneService duneService) {
        this.duneService = duneService;
    }

    @GetMapping("/uniMonthVolume")
    public List<UniswapMonthVolumeDto> getUniMonthVolume() {
        return duneService.getUniMonthVolume();
    } //  місячний об'єм Uniswap по всім парам

    @GetMapping("/uniTotalFeePairs")
    public List<UniswapTotalFeePairsDto> getUniTotalFeePairs() {
        return duneService.getUniTotalFeePairs();
    }//  топ 10 пар Uniswap по комісії за останні 7 днів (uniswap v1)

    @GetMapping("/uniTotalFeeByChain")
    public List<UniFeesByChainDto> getUniTotalFeeByChain() {
        return duneService.getUniFeesByChain();
    } //  об'єм комісії Uniswap по топ 10 мережам



    // PancakeSwap methods
    @GetMapping("/pancakeMonthVolume")
    public List<PancakeMonthVolumeDto> getPancakeMonthVolume() {
        return duneService.getPancakeMonthVolume();
    } //  місячний об'єм PancakeSwap по всім парам сумарно

    @GetMapping("/pancakeVolumeByChain")
    public List<PancakeMonthVolumeByChainDto> getPancakeVolumeByChain() {
        return duneService.getPancakeMonthVolumeByChain();
    } //  розподіл об'єму PancakeSwap по топ 8 мережам за останній місяць

    @GetMapping("/pancakeVolumeAndTransactionsOnBnb")
    public List<PancakeVolumeAndTransactionsOnBnbDto> getPancakeVolumeAndTransactionsOnBnb() {
        return duneService.getPancakeVolumeAndTransactionsOnBnb();
    }
    //  об'єм PancakeSwap та кількість транзакцій на BNB за останній місяць

}





