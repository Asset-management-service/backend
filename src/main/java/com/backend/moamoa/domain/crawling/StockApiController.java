package com.backend.moamoa.domain.crawling;

import com.backend.moamoa.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
@Slf4j
public class StockApiController {

    private final StockCrawling stockCrawling;


    @GetMapping("/code")
    public ResponseEntity getStockCode(@RequestBody Map<String,Object> map) {

        String stockCode = stockCrawling.getStockCode(String.valueOf(map.get("name")));

        return ResponseEntity.status(HttpStatus.OK).body(stockCode);
    }

    @GetMapping("/value")
    public ResponseEntity getStockValue(@RequestBody Map<String,Object> map) throws IOException {

        String stockCode = stockCrawling.getStockCode(String.valueOf(map.get("name")));
        Stock crawling = stockCrawling.crawling(stockCode);

        return ResponseEntity.status(HttpStatus.OK).body(crawling);

    }
}
