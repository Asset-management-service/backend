package com.backend.moamoa.domain.asset.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AssetSettingRequest {

    private Integer budget;

    private List<String> fixedCategories = new ArrayList<>();

}
