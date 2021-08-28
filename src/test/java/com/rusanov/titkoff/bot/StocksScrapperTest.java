package com.rusanov.titkoff.bot;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import ru.tinkoff.invest.openapi.model.streaming.CandleInterval;
import ru.tinkoff.invest.openapi.model.streaming.StreamingEvent;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@SpringBootTest
@RunWith(SpringRunner.class)
class StocksScrapperTest {

    @Autowired
    StocksScrapper scrapper;
    final String fakeFigi="XXXXXXXXXXX";
    @Test
    public void  removeOldCandlesTest() {

        for (int i = 0; i < 10; ++i) {
            scrapper.handle(new StreamingEvent.Candle
                    (new BigDecimal(100),
                            new BigDecimal(100),
                            new BigDecimal(100),
                            new BigDecimal(100),
                            new BigDecimal(100),
                            ZonedDateTime.now(),
                            CandleInterval._1MIN,
                            fakeFigi)
            );
        }
        scrapper.handle(new StreamingEvent.Candle
                (new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        ZonedDateTime.now().minusMinutes(5),
                        CandleInterval._1MIN,
                        fakeFigi) );
        scrapper.handle(new StreamingEvent.Candle
                (new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        ZonedDateTime.now().minusMinutes(15),
                        CandleInterval._1MIN,
                        fakeFigi));
        Map<String, List<StreamingEvent.Candle>> candles = (Map<String, List<StreamingEvent.Candle>>) ReflectionTestUtils.getField(scrapper,scrapper.getClass(),"candles");
        assertEquals(10, candles.get(fakeFigi).size());
    }


    @Test
    public void analyzeCandle() {
        scrapper.handle(new StreamingEvent.Candle
                (new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        ZonedDateTime.now(),
                        CandleInterval._1MIN,
                        fakeFigi));
        scrapper.handle(new StreamingEvent.Candle
                (new BigDecimal(100),
                        new BigDecimal(200),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        new BigDecimal(100),
                        ZonedDateTime.now(),
                        CandleInterval._1MIN,
                        fakeFigi));
    }
}