package com.backend.moamoa.domain.asset.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetCategoriesResponse {

    private List<String> categoryName = new ArrayList<>();

}
