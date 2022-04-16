package com.backend.moamoa.domain.asset.dto.request;

import com.backend.moamoa.domain.asset.entity.RevenueExpenditureType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRevenueExpenditureRequest {

    private RevenueExpenditureType revenueExpenditureType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    private String categoryName;

    private String paymentMethod;

    private int cost;

    private String content;

}
