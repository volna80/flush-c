package com.volna80.betfair.api.impl;

import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.model.AccountDetails;
import com.volna80.betfair.api.model.AccountFunds;
import com.volna80.betfair.api.model.Ssoid;
import com.volna80.betfair.api.model.adapter.Precision;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class AccountAPITest {

    public static final String DUMMY_URL = "https://localhost";
    private static final Logger log = LoggerFactory.getLogger(AccountAPITest.class);
    private AccountAPI accountAPI;
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
        when(builder.post(Mockito.<Entity>any())).then(new Answer<Response>() {
            @Override
            public Response answer(InvocationOnMock invocationOnMock) throws Throwable {
                Entity entity = (Entity) invocationOnMock.getArguments()[0];
                assertEquals(MediaType.APPLICATION_JSON_TYPE, entity.getMediaType());
                String json = (String) entity.getEntity();

                new JSONObject(json);
                //no exception, json is fine

                return response;
            }
        });

        accountAPI = new AccountAPI("dummy", new Ssoid("dummy-ssoid"), client, DUMMY_URL);
        accountAPI.init();
    }


    @Test
    public void getFounds() throws BetfairException {


        when(response.readEntity(String.class)).thenReturn("{\"availableToBetBalance\":14.63,\"exposure\":0.0,\"retainedCommission\":0.0,\"exposureLimit\":-10000.0}");

        AccountFunds funds = accountAPI.getAccountFunds();

        assertEquals(14.63, funds.getAvailableToBetBalance(), Precision.PRECISION);
    }

    @Test
    public void getDetails() throws BetfairException {
        when(response.readEntity(String.class)).thenReturn("{timezone:\"EET\", region:\"GBR\", pointsBalance:0, localeCode:\"ru\", lastName:\"Volnov\", firstName:\"Nikolay\", discountRate:0.0, currencyCode:\"USD\"}");

        AccountDetails details = accountAPI.getAccountDetails();
        assertEquals("Volnov", details.getLastName());
        assertEquals("Nikolay", details.getFirstName());

    }
}
