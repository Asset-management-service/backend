package com.backend.moamoa.domain.crawling;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Builder
@Getter
public class Stock {

    private String name;

    private LocalDate date;

    //증가,감소값
    private String point;

    //종가
//  private String open_Price;

    //현재 값
    private String price;

    //시가
    private String closePrice;

    //등락률
    private String rate;

    //고가
    private String highPrice;

    //저가
    private String lowPrice;

    //거래량(천주)
    private String volume;


    //전일종가
    private String netChange;
}

