package com.backend.moamoa.domain.asset.controller;

import com.backend.moamoa.domain.asset.dto.request.RevenueCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assets")
public class AssetController {

    @PostMapping("/setting")
    public ResponseEntity assetSetting() {

        return null;
    }

    @PostMapping("/revenueCategory")
    public ResponseEntity addRevenueCategory(RevenueCategoryRequest request){
        return null;
    }
}
