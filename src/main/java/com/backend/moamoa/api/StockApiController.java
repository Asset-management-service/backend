package com.backend.moamoa.api;

import com.backend.moamoa.asset.StockCrawling;
import com.backend.moamoa.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class StockApiController {

    private final StockCrawling stockCrawling;


    @GetMapping("/stock/code/{name}")
    public ResponseEntity getStockCode(@PathVariable("name") String name) {
        String stockCode = stockCrawling.getStockCode(name);
        return ResponseEntity.status(HttpStatus.OK).body(name);
    }

    @GetMapping("/stock/value/{name}")
    public ResponseEntity getStockValue(@PathVariable("name")String name) throws IOException {
        String stockCode = stockCrawling.getStockCode(name);
        Stock crawling = stockCrawling.Crawling(stockCode);

        return ResponseEntity.status(HttpStatus.OK).body(crawling);

    }
}
