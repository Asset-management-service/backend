package com.backend.moamoa.domain.crawling;

import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StockCrawling {

    public String getStockCode(String findName) {

        String code="";
        String webName;
        String url = "https://search.naver.com/search.naver?query="+findName;
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            webName = doc.selectFirst("span.stk_nm").text();
            isTrueStockName(findName, webName);
            code = doc.selectFirst("em.t_nm").text();

        } catch (NullPointerException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_STOCK,String.format("[%s] 이름을 찾을수 없습니다.",findName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return code;
    }

    public Stock crawling(String code) throws IOException {

        log.info("Stock code {}",code);
        String url = "https://invest.zum.com/domestic/item/"+code;

        Stock stock = null;
        Document doc;
        List<String> result = new ArrayList<>();

        try {
            doc = Jsoup.connect(url).get();

            Element title = doc.selectFirst("div.stock_board");

            // board에 있는 데이터
            Map<String, String> board = board(title);

            // sidebar 데이터
            Element info = doc.selectFirst("ul[data-v-d2826eb4]");
            Elements select = info.select("span.data");

            for (Element element : select) {
                result.add(subSpecialWord(element.text()));
            }

            stock = Stock.builder().closePrice(result.get(0))
                    .highPrice(result.get(1))
                    .lowPrice(result.get(2))
                    .netChange(result.get(3))
                    .volume(result.get(4))
                    .name(board.get("name"))
                    .rate(board.get("rate"))
                    .price(board.get("price"))
                    .point(board.get("point"))
                    .date(LocalDate.now())
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stock;
    }

    public Map<String, String> board(Element title) {
        Map<String,String> result = new HashMap<>();
        result.put("name",subSpecialWord(title.selectFirst("a").text()));
        result.put("price",subSpecialWord(title.selectFirst("span.price").text()));
        result.put("point",subSpecialWord(title.selectFirst("span.point").text()));
        result.put("rate",subSpecialWord(title.selectFirst("span.per").text()));
        return result;
    }

    public String subSpecialWord(String str) {
        return str.replaceAll("[(),]","");
    }

    public void isTrueStockName(String findName, String webName) {
        if (!webName.equals(findName)) {
            throw new CustomException(ErrorCode.NOT_FOUND_STOCK,String.format("[%s] 종목을 찾을 수 없습니다.",findName));
        }
    }

}