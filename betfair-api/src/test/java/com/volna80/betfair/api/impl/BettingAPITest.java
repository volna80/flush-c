package com.volna80.betfair.api.impl;

import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.model.*;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static com.volna80.betfair.api.model.adapter.Precision.PRECISION;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BettingAPITest {

    public static final String DUMMY_URL = "https://localhost";
    private static final Logger log = LoggerFactory.getLogger(BettingAPITest.class);
    private BettingAPI api2;
    private Response response;


    @Before
    public void setup() {


        Client client = mock(Client.class);
        WebTarget webTarget = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        response = mock(Response.class);


        when(response.getStatus()).thenReturn(OK.getStatusCode());
        when(client.target(DUMMY_URL)).thenReturn(webTarget);
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builder);
        when(builder.post(Mockito.<Entity>any())).then(invocationOnMock -> {
            Entity entity = (Entity) invocationOnMock.getArguments()[0];
            assertEquals(MediaType.APPLICATION_JSON_TYPE, entity.getMediaType());
            String json = (String) entity.getEntity();

            new JSONObject(json);
            //no exception, json is fine

            return response;
        });


        api2 = new BettingAPI("dummy", new Ssoid("dummy-ssoid"), client, DUMMY_URL);
        api2.init();
    }


    @Test
    public void listEventTypes() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("[{\"eventType\":{\"id\":\"468328\",\"name\":\"Гандбол\"}," +
                "\"marketCount\":25},{\"eventType\":{\"id\":\"1\",\"name\":\"Футбол \"},\"marketCount\":19473}," +
                "{\"eventType\":{\"id\":\"2\",\"name\":\"Теннис \"},\"marketCount\":624}]");


        final long start = System.nanoTime();
        List<EventType> evenTypes = api2.listEventTypes(new MarketFilter(), "ru");
        final long stop = System.nanoTime();
        log.info("listEventType : latency={} ms, result={}", (stop - start) / 1000000, evenTypes);
        assertTrue(evenTypes.size() > 0);
        EventType type = evenTypes.get(0);
        assertEquals("468328", type.getId());
        assertEquals("Гандбол", type.getName());
    }

    @Test
    public void listCompetitions() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("[" +
                "{\"competition\":{\"id\":\"33051\",\"name\":\"Кубок России\"},\"marketCount\":8,\"competitionRegion\":\"RUS\"}," +
                "{\"competition\":{\"id\":\"857992\",\"name\":\"Чили Примера В\"},\"marketCount\":15,\"competitionRegion\":\"CHL\"}," +
                "{\"competition\":{\"id\":\"2077087\",\"name\":\"Spanish Liga Asobal 2013/14 (M)\"},\"marketCount\":8,\"competitionRegion\":\"ESP\"}," +
                "{\"competition\":{\"id\":\"4141221\",\"name\":\"Formula 1 2014\"},\"marketCount\":30,\"competitionRegion\":\"International\"}," +
                "{\"competition\":{\"id\":\"7\",\"name\":\"Чемпионат Австрии\"},\"marketCount\":125,\"competitionRegion\":\"AUT\"}," +
                "{\"competition\":{\"id\":\"9\",\"name\":\"Первая Лига \"},\"marketCount\":25,\"competitionRegion\":\"AUT\"}," +
                "{\"competition\":{\"id\":\"4955688\",\"name\":\"Scotland v England 2014\"},\"marketCount\":23,\"competitionRegion\":\"GBR\"}," +
                "{\"competition\":{\"id\":\"2742944\",\"name\":\"All Ireland Senior Hurling Championship 2014\"},\"marketCount\":1,\"competitionRegion\":\"IRL\"}," +
                "{\"competition\":{\"id\":\"13\",\"name\":\"Бразилия Кампеонато\"},\"marketCount\":560,\"competitionRegion\":\"BRA\"}]");

        final long start = System.nanoTime();
        List<CompetitionResult> competitions = api2.listCompetition(new MarketFilter(), "ru");
        final long stop = System.nanoTime();
        log.info("listCompetitions : latency={} ms, result={}", (stop - start) / 1000000, competitions);

        assertEquals(9, competitions.size());

        CompetitionResult comp = competitions.get(0);
        assertEquals("RUS", comp.getCompetitionRegion());
        assertEquals(8, comp.getMarketCount());
        assertEquals("33051", comp.getCompetition().getId());
        assertEquals("Кубок России", comp.getCompetition().getName());
    }


    @Test
    public void listTimeRanges() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("[" +
                "{\"timeRange\":{\"from\":\"2005-05-05T06:00:00.000Z\",\"to\":\"2005-05-05T07:00:00.000Z\"},\"marketCount\":4}," +
                "{\"timeRange\":{\"from\":\"2006-09-12T15:00:00.000Z\",\"to\":\"2006-09-12T16:00:00.000Z\"},\"marketCount\":1}," +
                "{\"timeRange\":{\"from\":\"2008-03-08T14:00:00.000Z\",\"to\":\"2008-03-08T15:00:00.000Z\"},\"marketCount\":1}," +
                "{\"timeRange\":{\"from\":\"2008-10-10T15:00:00.000Z\",\"to\":\"2008-10-10T16:00:00.000Z\"},\"marketCount\":1}," +
                "{\"timeRange\":{\"from\":\"2011-09-13T18:00:00.000Z\",\"to\":\"2011-09-13T19:00:00.000Z\"},\"marketCount\":1}," +
                "{\"timeRange\":{\"from\":\"2012-09-22T11:00:00.000Z\",\"to\":\"2012-09-22T12:00:00.000Z\"},\"marketCount\":1}]");

        Map<TimeRange, Integer> result = api2.listTimeRanges(new MarketFilter(), TimeGranularity.HOURS);
        log.info("timeRanges={}", result);

        assertEquals(6, result.size());
    }

    @Test
    public void listMarketTypes() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("[" +
                "{\"marketType\":\"NONSPORT\",\"marketCount\":118}," +
                "{\"marketType\":\"WINNER\",\"marketCount\":54}," +
                "{\"marketType\":\"UNDIFFERENTIATED\",\"marketCount\":223}," +
                "{\"marketType\":\"SPECIAL\",\"marketCount\":112}," +
                "{\"marketType\":\"TOURNAMENT_WINNER\",\"marketCount\":49}," +
                "{\"marketType\":\"OUTRIGHT_WINNER\",\"marketCount\":26}," +
                "{\"marketType\":\"TOP_N_FINISH\",\"marketCount\":7}," +
                "{\"marketType\":\"UNUSED\",\"marketCount\":116}," +
                "{\"marketType\":\"STEWARDS\",\"marketCount\":2}," +
                "{\"marketType\":\"HEAD_TO_HEAD\",\"marketCount\":7}," +
                "{\"marketType\":\"TOP_BATSMAN\",\"marketCount\":8}," +
                "{\"marketType\":\"TOP_WICKETS_TAKER\",\"marketCount\":1}," +
                "{\"marketType\":\"WINNING_MARGIN\",\"marketCount\":49}]");

        Map<String, Integer> result = api2.listMarketTypes(new MarketFilter(), "en");
        log.info("marketTypes={}", result);

        assertEquals(13, result.size());
        assertNotNull(result.get("WINNER"));
        assertEquals(Integer.valueOf(54), result.get("WINNER"));
    }

    @Test
    public void listCountries() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("[" +
                "{\"countryCode\":\"GB\",\"marketCount\":3077}," +
                "{\"countryCode\":\"UA\",\"marketCount\":356}," +
                "{\"countryCode\":\"SI\",\"marketCount\":26}," +
                "{\"countryCode\":\"IE\",\"marketCount\":443}," +
                "{\"countryCode\":\"BR\",\"marketCount\":2256}," +
                "{\"countryCode\":\"SK\",\"marketCount\":68}," +
                "{\"countryCode\":\"CH\",\"marketCount\":284}," +
                "{\"countryCode\":\"RU\",\"marketCount\":322}," +
                "{\"countryCode\":\"PL\",\"marketCount\":177}," +
                "{\"countryCode\":\"CZ\",\"marketCount\":409}," +
                "{\"countryCode\":\"DK\",\"marketCount\":701}," +
                "{\"countryCode\":\"RO\",\"marketCount\":452}," +
                "{\"countryCode\":\"BG\",\"marketCount\":227}]");

        List<CountryCodeResult> result = api2.listCountries(new MarketFilter(), "en");
        log.info("countries=" + result);

        assertEquals(13, result.size());

        Optional<CountryCodeResult> country = result.stream().filter(c -> c.getCountryCode().equals("RU")).findFirst();
        assertTrue(country.isPresent());
        assertEquals(322, country.get().getMarketCount());
    }

    @Test
    public void listEvents() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("[" +
                "{\"event\":{\"id\":\"27194006\",\"name\":\"Dallas v New York Red Bulls\",\"countryCode\":\"US\",\"timezone\":\"Europe/London\",\"openDate\":\"2014-05-04T19:00:00.000Z\"},\"marketCount\":40}," +
                "{\"event\":{\"id\":\"27194007\",\"name\":\"Kansas City v Columbus\",\"countryCode\":\"US\",\"timezone\":\"Europe/London\",\"openDate\":\"2014-05-04T20:00:00.000Z\"},\"marketCount\":40}," +
                "{\"event\":{\"id\":\"27113620\",\"name\":\"USA v Germany\",\"countryCode\":\"BR\",\"timezone\":\"Europe/London\",\"openDate\":\"2014-06-26T16:00:00.000Z\"},\"marketCount\":25}," +
                "{\"event\":{\"id\":\"27113622\",\"name\":\"South Korea v Belgium\",\"countryCode\":\"BR\",\"timezone\":\"Europe/London\",\"openDate\":\"2014-06-26T20:00:00.000Z\"},\"marketCount\":25}," +
                "{\"event\":{\"id\":\"27183450\",\"name\":\"ICC World Twenty20 2016\",\"countryCode\":\"IN\",\"timezone\":\"Asia/Calcutta\",\"openDate\":\"2016-02-01T11:30:00.000Z\"},\"marketCount\":1}," +
                "{\"event\":{\"id\":\"27113616\",\"name\":\"Portugal v Ghana\",\"countryCode\":\"BR\",\"timezone\":\"Europe/London\",\"openDate\":\"2014-06-26T16:00:00.000Z\"},\"marketCount\":25}," +
                "{\"event\":{\"id\":\"27113618\",\"name\":\"Algeria v Russia\",\"countryCode\":\"BR\",\"timezone\":\"Europe/London\",\"openDate\":\"2014-06-26T20:00:00.000Z\"},\"marketCount\":25}," +
                "{\"event\":{\"id\":\"27033458\",\"name\":\"Scotland Independence Referendum 2014\",\"countryCode\":\"GB\",\"timezone\":\"Europe/London\",\"openDate\":\"2014-09-18T06:00:00.000Z\"},\"marketCount\":2}," +
                "{\"event\":{\"id\":\"27194010\",\"name\":\"Marseille v Lyon\",\"countryCode\":\"FR\",\"timezone\":\"Europe/London\",\"openDate\":\"2014-05-04T19:00:00.000Z\"},\"marketCount\":83}]");

        Map<Event, Integer> result = api2.listEvents(new MarketFilter(), "en");
        log.info("events={}", result);

        assertEquals(9, result.size());

        Optional<Event> event = result.keySet().stream().filter(e -> e.getId().equals("27194010")).findFirst();

        assertTrue(event.isPresent());

        assertEquals("Marseille v Lyon", event.get().getName());
        assertEquals("FR", event.get().getCountryCode());
        assertEquals("Europe/London", event.get().getTimezone());
        assertEquals(Integer.valueOf(83), result.get(event.get()));

    }

    @Test
    public void listMarketCatalogue() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("[" +
                "{\"marketId\":\"1.113418588\",\"marketName\":\"Match Odds\",\"totalMatched\":25860.669937000006,\"competition\":{\"id\":\"31\",\"name\":\"Barclays Premier League\"}}," +
                "{\"marketId\":\"1.113883751\",\"marketName\":\"Match Odds\",\"totalMatched\":5071428.855486,\"competition\":{\"id\":\"117\",\"name\":\"Primera Division\"}}," +
                "{\"marketId\":\"1.113986214\",\"marketName\":\"Match Odds\",\"totalMatched\":534.0260870000001,\"competition\":{\"id\":\"5007228\",\"name\":\"Mutua Madrid Open 2014 WTA\"}}," +
                "{\"marketId\":\"1.113995160\",\"marketName\":\"Match Odds\",\"totalMatched\":5730.782521,\"competition\":{\"id\":\"5009885\",\"name\":\"Mutua Madrid Open - 2014 - ATP\"}}," +
                "{\"marketId\":\"1.113986552\",\"marketName\":\"Match Odds\",\"totalMatched\":4665.108594,\"competition\":{\"id\":\"5007228\",\"name\":\"Mutua Madrid Open 2014 WTA\"}}," +
                "{\"marketId\":\"1.113986588\",\"marketName\":\"Match Odds\",\"totalMatched\":144.415254,\"competition\":{\"id\":\"5007228\",\"name\":\"Mutua Madrid Open 2014 WTA\"}}," +
                "{\"marketId\":\"1.113995142\",\"marketName\":\"Match Odds\",\"totalMatched\":4670.7590390000005,\"competition\":{\"id\":\"5009885\",\"name\":\"Mutua Madrid Open - 2014 - ATP\"}}," +
                "{\"marketId\":\"1.108039554\",\"marketName\":\"Winner 2016\",\"totalMatched\":9580.658404,\"competition\":{\"id\":\"4527196\",\"name\":\"Euro 2016\"}}," +
                "{\"marketId\":\"1.114001400\",\"marketName\":\"Match Odds\",\"totalMatched\":194.611446,\"competition\":{\"id\":\"5007228\",\"name\":\"Mutua Madrid Open 2014 WTA\"}}," +
                "{\"marketId\":\"1.113424999\",\"marketName\":\"Match Odds\",\"totalMatched\":224053.536648,\"competition\":{\"id\":\"31\",\"name\":\"Barclays Premier League\"}}]");

        MarketFilter filter = new MarketFilter();
        Set<MarketProjection> projections = new HashSet<MarketProjection>();
        projections.add(MarketProjection.COMPETITION);

        List<MarketCatalogue> result = api2.listMarketCatalogue(filter, projections, MarketSort.MAXIMUM_AVAILABLE, 10, "en");

        log.info("marketCatalogue", result);

        assertTrue(result.size() == 10);

        Optional<MarketCatalogue> market = result.stream().filter(m -> m.getMarketId().equals("1.113424999")).findFirst();

        assertTrue(market.isPresent());
        assertEquals("Match Odds", market.get().getMarketName());
        assertEquals(224053.536648, market.get().getTotalMatched(), PRECISION);
        assertEquals("31", market.get().getCompetition().getId());
        assertEquals("Barclays Premier League", market.get().getCompetition().getName());
    }


    @Test
    public void testMakeRequest() {
        BettingAPI api = new BettingAPI("appId", new Ssoid("ssoid"), null, null);

        Set<MarketProjection> projections = new HashSet<>();
        projections.add(MarketProjection.COMPETITION);

        String req = api.makeRequest(new MarketFilter(), projections, MarketSort.LAST_TO_START, 10, "en");
        new JSONObject(req);

        MarketFilter filter = new MarketFilter();
        filter.setBspOnly(true);
        filter.setInPlayOnly(true);
        filter.setTextQuery("blabla");
        new JSONObject(api.makeRequest(filter, projections, MarketSort.FIRST_TO_START, 10, "en"));
    }


    @Test
    public void listMarketBooks() throws BetfairException {


        when(response.readEntity(String.class)).thenReturn("[" +
                "{\"marketId\":\"1.112028598\",\"isMarketDataDelayed\":true,\"status\":\"OPEN\",\"betDelay\":0,\"bspReconciled\":true,\"complete\":true,\"inplay\":true," +
                "\"numberOfWinners\":1,\"numberOfRunners\":22,\"numberOfActiveRunners\":19,\"lastMatchTime\":\"2014-05-04T18:51:18.546Z\"," +
                "\"totalMatched\":828368.88,\"totalAvailable\":68296.75,\"crossMatching\":true,\"runnersVoidable\":true,\"version\":729386071," +
                "\"runners\":[" +
                "{\"selectionId\":7812754,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1.43,\"totalMatched\":329269.21,\"ex\":{\"availableToBack\":[{\"price\":1.42,\"size\":140.78},{\"price\":1.41,\"size\":1365.64},{\"price\":1.4,\"size\":684.68}],\"availableToLay\":[{\"price\":1.43,\"size\":60.89},{\"price\":1.44,\"size\":353.46},{\"price\":1.45,\"size\":700.5}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7812755,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":5.8,\"totalMatched\":142409.02,\"ex\":{\"availableToBack\":[{\"price\":5.7,\"size\":6.07},{\"price\":5.6,\"size\":153.87},{\"price\":5.5,\"size\":584.18}],\"availableToLay\":[{\"price\":5.8,\"size\":154.16},{\"price\":5.9,\"size\":43.36},{\"price\":6.0,\"size\":59.03}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7812752,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":16.5,\"totalMatched\":185427.54,\"ex\":{\"availableToBack\":[{\"price\":14.0,\"size\":16.86},{\"price\":13.0,\"size\":36.05},{\"price\":12.5,\"size\":69.33}],\"availableToLay\":[{\"price\":16.0,\"size\":3.35},{\"price\":16.5,\"size\":5.66}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7812761,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":60.0,\"totalMatched\":11506.05,\"ex\":{\"availableToBack\":[{\"price\":50.0,\"size\":3.39},{\"price\":46.0,\"size\":9.7},{\"price\":44.0,\"size\":84.33}],\"availableToLay\":[{\"price\":60.0,\"size\":14.91},{\"price\":65.0,\"size\":34.66}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7610784,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":42.0,\"totalMatched\":49722.56,\"ex\":{\"availableToBack\":[{\"price\":36.0,\"size\":15.85}],\"availableToLay\":[{\"price\":42.0,\"size\":79.54}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":6550705,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":180.0,\"totalMatched\":24422.87,\"ex\":{\"availableToBack\":[{\"price\":200.0,\"size\":3.35}],\"availableToLay\":[{\"price\":210.0,\"size\":0.06},{\"price\":250.0,\"size\":6.0}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7610786,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":310.0,\"totalMatched\":32497.67,\"ex\":{\"availableToBack\":[{\"price\":310.0,\"size\":5.83},{\"price\":300.0,\"size\":8.43},{\"price\":290.0,\"size\":3.35}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":8064929,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":380.0,\"totalMatched\":25197.44,\"ex\":{\"availableToBack\":[{\"price\":380.0,\"size\":3.12},{\"price\":370.0,\"size\":0.1},{\"price\":360.0,\"size\":3.35}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7812756,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":580.0,\"totalMatched\":11286.31,\"ex\":{\"availableToBack\":[{\"price\":580.0,\"size\":3.35}],\"availableToLay\":[{\"price\":860.0,\"size\":0.01}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":6533198,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":490.0,\"totalMatched\":8810.21,\"ex\":{\"availableToBack\":[{\"price\":490.0,\"size\":3.35}],\"availableToLay\":[{\"price\":1000.0,\"size\":7.65}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7812758,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":450.0,\"totalMatched\":2280.64,\"ex\":{\"availableToBack\":[{\"price\":450.0,\"size\":3.35}],\"availableToLay\":[{\"price\":1000.0,\"size\":5.14}],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7812760,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":1386.64,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":26.89}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7812762,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":82.88,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":67.06},{\"price\":100.0,\"size\":84.33},{\"price\":2.0,\"size\":3.37},{\"price\":1.01,\"size\":3.37}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":8064931,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":272.37,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":37.69},{\"price\":460.0,\"size\":3.35},{\"price\":450.0,\"size\":3.37},{\"price\":210.0,\"size\":3.35},{\"price\":200.0,\"size\":16.42},{\"price\":100.0,\"size\":84.33},{\"price\":2.02,\"size\":3.35},{\"price\":2.0,\"size\":3.37},{\"price\":1.01,\"size\":1700.15}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":6533199,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":33.73,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":86.29},{\"price\":100.0,\"size\":84.33},{\"price\":2.0,\"size\":3.37},{\"price\":1.01,\"size\":3.37}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7812763,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":705.91,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":81.42},{\"price\":700.0,\"size\":3.35},{\"price\":690.0,\"size\":2.75},{\"price\":630.0,\"size\":5.46},{\"price\":590.0,\"size\":3.35},{\"price\":100.0,\"size\":84.33},{\"price\":55.0,\"size\":421.67},{\"price\":50.0,\"size\":8.43},{\"price\":2.02,\"size\":3.35},{\"price\":2.0,\"size\":3.37},{\"price\":1.01,\"size\":1703.51}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":2311149,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":203.44,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":69.24},{\"price\":100.0,\"size\":84.33},{\"price\":2.02,\"size\":3.35},{\"price\":2.0,\"size\":3.37},{\"price\":1.01,\"size\":1693.44}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":7812757,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":2654.86,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":120.55},{\"price\":580.0,\"size\":0.12},{\"price\":170.0,\"size\":3.35},{\"price\":160.0,\"size\":1.68},{\"price\":110.0,\"size\":3.35},{\"price\":100.0,\"size\":89.39},{\"price\":65.0,\"size\":168.67},{\"price\":60.0,\"size\":5.06},{\"price\":4.1,\"size\":3.35},{\"price\":4.0,\"size\":18.54},{\"price\":2.02,\"size\":3.35},{\"price\":2.0,\"size\":6.74},{\"price\":1.01,\"size\":1703.53}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":6203944,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":102.93,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":94.55},{\"price\":100.0,\"size\":84.33},{\"price\":2.0,\"size\":3.37},{\"price\":1.01,\"size\":3.37}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":6203964,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":40.48,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":82.91},{\"price\":100.0,\"size\":84.33},{\"price\":2.0,\"size\":3.37},{\"price\":1.01,\"size\":3.37}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":6550719,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":42.54,\"ex\":{\"availableToBack\":[{\"price\":1000.0,\"size\":91.85},{\"price\":100.0,\"size\":84.33},{\"price\":2.0,\"size\":3.37},{\"price\":1.01,\"size\":3.37}],\"availableToLay\":[],\"tradedVolume\":[]}}," +
                "{\"selectionId\":6203941,\"handicap\":0.0,\"status\":\"ACTIVE\",\"lastPriceTraded\":1000.0,\"totalMatched\":13.49," +
                "\"ex\":" +
                "{\"availableToBack\":[{\"price\":1000.0,\"size\":89.66},{\"price\":100.0,\"size\":84.33},{\"price\":2.0,\"size\":3.37},{\"price\":1.01,\"size\":3.37}]," +
                "\"availableToLay\":[{\"price\":1000.0,\"size\":89.66}],\"tradedVolume\":[]}}]}]");

        final String market = "1.112028598";

        List<String> marketIds = new ArrayList<String>() {{
            add(market);
        }};
        PriceProjection priceProjection = new PriceProjection();
        priceProjection.setVirtualise(false);
        priceProjection.setPriceData(PriceData.EX_ALL_OFFERS);

        List<MarketBook> resp = api2.listMarketBook(marketIds, priceProjection, OrderProjection.ALL, MatchProjection.ROLLED_UP_BY_PRICE, null, "en");

        assertEquals(1, resp.size());
        MarketBook mb = resp.get(0);

        assertEquals("1.112028598", mb.getMarketId());
        assertTrue(mb.isMarketDataDelayed());
        assertEquals(MarketStatus.OPEN, mb.getStatus());
        assertEquals(0, mb.getBetDelay());
        assertTrue(mb.isBspReconciled());
        assertTrue(mb.isComplete());
        assertTrue(mb.isInplay());
        assertEquals(1, mb.getNumberOfWinners());
        assertEquals(22, mb.getNumberOfRunners());
        assertEquals(19, mb.getNumberOfActiveRunners());
        assertEquals(68296.75, mb.getTotalAvailable(), PRECISION);
        assertEquals(true, mb.isCrossMatching());
        assertEquals(true, mb.isRunnersVoidable());
        assertEquals(729386071, mb.getVersion());

        Optional<Runner> runner = mb.getRunners().stream().filter(r -> r.getSelectionId() == 6203941).findFirst();
        assertTrue(runner.isPresent());
        assertEquals(0., runner.get().getHandicap(), PRECISION);
        assertEquals(RunnerStatus.ACTIVE, runner.get().getStatus());
        assertEquals(100000, runner.get().getLastPriceTraded());
        assertEquals(1349, runner.get().getTotalMatched());

        assertEquals(4, runner.get().getEx().getAvailableToBack().size());
        assertEquals(1, runner.get().getEx().getAvailableToLay().size());
        assertEquals(0, runner.get().getEx().getTradedVolume().size());

        log.info("marketBook={}", resp);
    }

    @Test
    public void testMarketRequest2() {

        BettingAPI api = new BettingAPI("appId", new Ssoid("ssoid"), null, null);

        List<String> marketIds = new ArrayList<String>() {{
            add("1.113381938");
            add("1.113381938");
            add("1.113381938");
        }};

        ExBestOffersOverrides ex = new ExBestOffersOverrides();
        ex.setBestPricesDepth(10);
        ex.setRollupLiabilityFactor(10);
        ex.setRollupLimit(10);
        ex.setRollupLiabilityThreshold(10);
        ex.setRollupModel(RollupModel.STAKE);

        PriceProjection priceProjection = new PriceProjection();
        priceProjection.setRolloverStakes(true);
        priceProjection.setVirtualise(false);
        priceProjection.setPriceData(PriceData.EX_ALL_OFFERS);
        priceProjection.setExBestOffersOverrides(ex);

        String req = api.makeRequest(marketIds, priceProjection, OrderProjection.ALL, MatchProjection.ROLLED_UP_BY_AVG_PRICE, "rub", "ru");

        new JSONObject(req);

    }


    @Test
    public void listCurrentOrders() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("{\"currentOrders\":[{\"betId\":\"40398169894\",\"marketId\":\"1.115010029\",\"selectionId\":5517,\"handicap\":0.0," +
                "\"priceSize\":{\"price\":1.41,\"size\":4.0},\"bspLiability\":0.0,\"side\":\"LAY\",\"status\":\"EXECUTABLE\",\"persistenceType\":\"LAPSE\",\"orderType\":\"LIMIT\"," +
                "\"placedDate\":\"2014-08-23T07:33:26.000Z\",\"averagePriceMatched\":0.0,\"sizeMatched\":2.0,\"sizeRemaining\":4.0,\"sizeLapsed\":1.0,\"sizeCancelled\":1.0," +
                "\"sizeVoided\":1.1,\"regulatorCode\":\"MALTA LOTTERIES AND GAMBLING AUTHORITY\"}],\"moreAvailable\":true}");

        CurrentOrderSummaryReport report = api2.listCurrentOrders(null, null, OrderProjection.ALL, null, null, OrderBy.BY_MARKET, SortDir.EARLIEST_TO_LATEST, 0, 1000);

        assertEquals(1, report.getCurrentOrders().size());
        CurrentOrderSummary order = report.getCurrentOrders().get(0);

        assertEquals("40398169894", order.getBetId());
        assertEquals("1.115010029", order.getMarketId());
        assertEquals(5517, order.getSelectionId());
        assertEquals(new PriceSize(141, 400), order.getPriceSize());
        assertEquals(Side.LAY, order.getSide());
        assertEquals(OrderStatus.EXECUTABLE, order.getStatus());
        assertEquals(PersistenceType.LAPSE, order.getPersistenceType());
        assertEquals(OrderType.LIMIT, order.getOrderType());
        assertEquals(200, order.getSizeMatched());
        assertEquals(400, order.getSizeRemaining());
        assertEquals(100, order.getSizeLapsed());
        assertEquals(100, order.getSizeCancelled());
        assertEquals(110, order.getSizeVoided());
        assertEquals("MALTA LOTTERIES AND GAMBLING AUTHORITY", order.getRegulatorCode());
        assertTrue(report.isMoreAvailable());

    }


    @Test
    public void cancelOrders() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("{\"customerRef\":\"customerRef\",\"status\":\"SUCCESS\",\"marketId\":\"1.112028598\",\"instructionReports\":[]}");

        final String market = "1.112028598";


        CancelExecutionReport report = api2.cancelOrders(market, null, "customerRef");

//        CancelExecutionReport report = api.cancelOrders("1.113424163", null, "customerRef");
        assertEquals("customerRef", report.getCustomerRef());

    }


    @Test
    public void placeOrders() throws BetfairException {

        when(response.readEntity(String.class)).thenReturn("{\"customerRef\":\"customerRef1399230921404-1\",\"status\":\"SUCCESS\",\"marketId\":\"1.112028598\",\"instructionReports\":[{\"status\":\"SUCCESS\",\"instruction\":{\"selectionId\":7812754,\"limitOrder\":{\"size\":4.0,\"price\":1.01,\"persistenceType\":\"LAPSE\"},\"orderType\":\"LIMIT\",\"side\":\"LAY\"},\"betId\":\"37023094055\",\"placedDate\":\"2014-05-04T19:15:22.000Z\",\"averagePriceMatched\":0.0,\"sizeMatched\":0.0}]}");

        //1.112028598 - formula one - champion 2014

        final String market = "1.112028598";

        List<PlaceInstruction> instructions = new ArrayList<PlaceInstruction>();

        PlaceInstruction order1 = new PlaceInstruction();
        order1.setSelectionId(7812754); //Luis Hemelton
        order1.setSide(Side.LAY);
        order1.setOrderType(OrderType.LIMIT);

        LimitOrder limit = new LimitOrder();
        limit.setPrice(101);
        limit.setSize(400);
        limit.setPersistenceType(PersistenceType.LAPSE);
        order1.setLimitOrder(limit);

        instructions.add(order1);

        PlaceExecutionReport report = api2.placeOrders(market, instructions, "customerRef1399230921404-1");

        assertEquals("customerRef1399230921404-1", report.getCustomerRef());
        assertEquals(market, report.getMarketId());
        assertEquals(ExecutionReportStatus.SUCCESS, report.getStatus());
        assertEquals(1, report.getInstructionReports().size());


    }

}
