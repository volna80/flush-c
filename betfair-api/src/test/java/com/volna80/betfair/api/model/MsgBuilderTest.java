package com.volna80.betfair.api.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.volna80.betfair.api.model.adapter.Precision;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MsgBuilderTest {

    private Gson gson;


    @Before
    public void setUp() throws Exception {

        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        gson = builder.create();


    }

    @Test
    public void competitionResult() {
        MsgBuilder<CompetitionResult[]> builder = new MsgBuilder<CompetitionResult[]>(CompetitionResult[].class, gson);

        CompetitionResult[] result = builder.build(
                "[" +
                        "{\"competition\":{\"id\":\"2472064\",\"name\":\"Ladies World Matchplay Singles 2014\"},\"marketCount\":4,\"competitionRegion\":\"GBR\"}," +
                        "{\"competition\":{\"id\":\"857992\",\"name\":\"Чили Примера В\"},\"marketCount\":20,\"competitionRegion\":\"CHL\"}]");

        assertEquals(2, result.length);

        CompetitionResult c = result[0];

        assertEquals(4, c.getMarketCount());
        assertEquals("GBR", c.getCompetitionRegion());
        assertEquals("2472064", c.getCompetition().getId());
        assertEquals("Ladies World Matchplay Singles 2014", c.getCompetition().getName());

    }


    @Test
    public void accountFunds() {
        MsgBuilder<AccountFunds> builder = new MsgBuilder<>(AccountFunds.class, gson);
        AccountFunds funds = builder.build("{\"availableToBetBalance\":23.75,\"exposure\":-4.0,\"retainedCommission\":0.0,\"exposureLimit\":-10000.0}");
        assertEquals(23.75, funds.getAvailableToBetBalance(), Precision.PRECISION);
    }

    @Test
    public void accountDetails() {
        MsgBuilder<AccountDetails> builder = new MsgBuilder<>(AccountDetails.class, gson);
        AccountDetails result = builder.build("{\"currencyCode\":\"USD\",\"firstName\":\"Nikolay\",\"lastName\":\"Volnov\",\"localeCode\":\"ru\",\"region\":\"GBR\",\"timezone\":\"EET\",\"discountRate\":0.0,\"pointsBalance\":0}");
        assertEquals("Nikolay", result.getFirstName());

    }

    @Test
    public void timeRange() {
        MsgBuilder<TimeRangeResult[]> builder = new MsgBuilder<>(TimeRangeResult[].class, gson);
        TimeRangeResult[] result = builder.build("[{\"timeRange\":{\"from\":\"2005-05-05T06:00:00.000Z\",\"to\":\"2005-05-05T07:00:00.000Z\"},\"marketCount\":4}]");
        assertEquals(1, result.length);
        assertEquals(4, result[0].getMarketCount());
    }

    @Test
    public void eventTypes() {
        MsgBuilder<EventTypeResult[]> eventTypesBuilder = new MsgBuilder<>(EventTypeResult[].class, gson);
        EventTypeResult[] result = eventTypesBuilder.build("[{\"eventType\":{\"id\":\"468328\",\"name\":\"Гандбол \"},\"marketCount\":68}]");
        assertEquals(1, result.length);
        assertEquals("468328", result[0].eventType.getId());


    }


    @Test
    public void marketCatalogue() throws IOException {
        MsgBuilder<MarketCatalogue[]> builder = new MsgBuilder<>(MarketCatalogue[].class, gson);

//        MarketCatalogue[] result = builder.build("[" +
//                "{" +
//                "\"marketId\":\"1.113058146\"," +
//                "\"marketName\":\"Ставки\"," +
//                "\"marketStartTime\":\"2014-03-10T19:00:00.000Z\"," +
//                "\"description\":" +
//                    "{" +
//                     "\"persistenceEnabled\":true," +
//                     "\"bspMarket\":false," +
//                     "\"marketTime\":\"2014-03-10T19:00:00.000Z\"," +
//                     "\"suspendTime\":\"2014-03-10T19:00:00.000Z\"," +
//                     "\"bettingType\":\"ODDS\"," +
//                     "\"turnInPlayEnabled\":true," +
//                     "\"marketType\":\"MATCH_ODDS\"," +
//                     "\"marketBaseRate\":5.0," +
//                     "\"discountAllowed\":true," +
//                     "\"wallet\":\"Английский счет\"," +
//                     "\"rules\":\"<br><a href=\\\"http://www.stats.betradar.com/statistics/betfaircom/?language=en\\\" target=\\\"_blank\\\"><img src=\\\"http://content-cache.betfair.com/images/en_GB/mr_fg.gif\\\" title=\\\"Form Guide\\\" border=\\\"0\\\"></a><a href=\\\"http://www.livescore.betradar.com/?alias=betfair&language=en\\\" target=\\\"_blank\\\"><img src=\\\"http://content-cache.betfair.com/images/en_GB/mr_ls.gif\\\" title=\\\"Live Scores\\\" border=\\\"0\\\"></a>.<br>\"," +
//                     "\"rulesHasDate\":true" +
//                     "}," +
//                "\"totalMatched\":94882.80743999999," +
//                "\"runners\":" +
//                    "[" +
//                        "{" +
//                         "\"selectionId\":10543," +
//                         "\"runnerName\":\"Осасуна\"," +
//                         "\"handicap\":0.0," +
//                         "\"sortPriority\":1," +
//                         "\"metadata\":" +
//                             "{" +
//                              "\"runnerId\":\"62647930\"" +
//                              "}" +
//                         "}," +
//                         "{" +
//                         "\"selectionId\":13360," +
//                         "\"runnerName\":\"Малага\"," +
//                         "\"handicap\":0.0," +
//                         "\"sortPriority\":2," +
//                         "\"metadata\":" +
//                             "{" +
//                              "\"runnerId\":\"62647931\"" +
//                              "}" +
//                         "}," +
//                         "{" +
//                          "\"selectionId\":58805," +
//                          "\"runnerName\":\"Ничья\"," +
//                          "\"handicap\":0.0," +
//                          "\"sortPriority\":3," +
//                          "\"metadata\":" +
//                              "{\"runnerId\":\"62647932\"}" +
//                          "}" +
//                    "]," +
//                "\"eventType\":{\"id\":\"1\",\"name\":\"Футбол \"}," +
//                "\"competition\":{;\"id\":\"117\",\"name\":\"Чемпионат Испании\"}," +
//                "\"event\":{\"id\":\"27159921\",\"name\":\"Осасуна - Малага\",\"countryCode\":\"ES\",\"timezone\":\"GMT\",\"openDate\":\"2014-03-10T19:00:00.000Z\"}" +
//                "}" +"]"
//        );
//
//        assertEquals(1, result.length);
//        assertEquals("1.113058146", result[0].getMarketId());


        URL file = MsgBuilderTest.class.getResource("/flush/market-catalogue.txt");

        BufferedReader br = new BufferedReader(new FileReader(file.getFile()));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String json = sb.toString();
            builder.build(json);
        } finally {
            br.close();
        }


    }
}
