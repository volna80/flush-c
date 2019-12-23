package com.volna80.betfair.api.impl;

import com.google.gson.Gson;
import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.IAccountAPI;
import com.volna80.betfair.api.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class AccountAPI extends AbstractAPI implements IAccountAPI {

    public static final String GET_ACCOUNT_FOUNDS = "getAccountFunds/";
    public static final String GET_ACCOUNT_DETAILS = "getAccountDetails/";
    private static final Logger log = LoggerFactory.getLogger(AccountAPI.class);
    private Invocation.Builder getFounds;
    private Invocation.Builder getDetails;


    private Gson gson = new Gson();
    private IMsgBuilder<AccountFunds> accountFoundsBuilder = new MsgBuilder<AccountFunds>(AccountFunds.class, gson);
    private IMsgBuilder<AccountDetails> accountDetailsBuilder = new MsgBuilder<AccountDetails>(AccountDetails.class, gson);


    public AccountAPI(String appId, Ssoid ssoid, Client client, String uri) {
        super(appId, ssoid, client, uri);
    }

    public void init() {
        getFounds = makeRequest(GET_ACCOUNT_FOUNDS);
        getDetails = makeRequest(GET_ACCOUNT_DETAILS);
    }

    @java.lang.Override
    public AccountFunds getAccountFunds() throws BetfairException {

        if (log.isTraceEnabled()) {
            log.trace("getAccountFund:{}:{}", appId, ssoid);
        }

        return post(getFounds, accountFoundsBuilder, EMPTY_REQUEST);

    }

    @Override
    public AccountDetails getAccountDetails() throws BetfairException {
        if (log.isTraceEnabled()) {
            log.trace("getAccountDetails:{}:{}", appId, ssoid);
        }


        return post(getDetails, accountDetailsBuilder, EMPTY_REQUEST);

    }


}
