package com.volna80.betfair.api;

import com.volna80.betfair.api.impl.AccountAPI;
import com.volna80.betfair.api.impl.BetfairAPI;
import com.volna80.betfair.api.impl.BettingAPI;
import com.volna80.betfair.api.model.Ssoid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BetfairApiFactory implements IBetfairApiFactory {

    //https://api.developer.betfair.com/services/webapps/docs/display/1smk3cen4v3lu3yomq5qye0ni/Accounts+API
    public static final String ACCOUNT_API_UK_EXCHANGE_JSON_REST = "https://api.betfair.com/exchange/account/rest/v1.0";
    public static final String BETTING_API_UK_EXCHANGE_JSON_REST = "https://api.betfair.com/exchange/betting/rest/v1.0";
    private static final Logger log = LoggerFactory.getLogger(BetfairApiFactory.class);
    private String accountUrl = null;
    private String bettingUrl = null;
    private String appId = null;
    private Ssoid ssoid = null;

    private volatile boolean initialized = false;
    private volatile boolean stopped = false;

    private List<Client> clients = new ArrayList<Client>();

    public BetfairApiFactory setAccountUrl(String accountUrl) {
        this.accountUrl = accountUrl;
        return this;
    }

    public BetfairApiFactory setBettingUrl(String bettingUrl) {
        this.bettingUrl = bettingUrl;
        return this;
    }

    public BetfairApiFactory setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public BetfairApiFactory setSsoid(Ssoid ssoid) {
        this.ssoid = ssoid;
        return this;
    }

    @Override
    public synchronized void init() {

        checkNotNull(accountUrl);
        checkNotNull(bettingUrl);
        checkNotNull(appId);
        checkNotNull(ssoid);


        log.info("account url : {}", accountUrl);
        log.info("betting url : {}", bettingUrl);
        log.info("app key : {}", appId);
        log.info("ssoid : {}", ssoid.hashCode());
        log.info("the factory has been init");
        initialized = true;
    }

    @Override
    public synchronized void close() {
        if (stopped) {
            //do nothing
            return;
        }

        clients.forEach(Client::close);

        stopped = true;
    }

    @Override
    public IBetfairAPI makeService() {

        if (!initialized) {
            throw new IllegalStateException("the factory hasn't been initialized");
        }
        if (stopped) {
            throw new IllegalStateException("the factory has been stopped");
        }


        Client client = ClientBuilder.newClient();
        clients.add(client);

        AccountAPI accountAPI = new AccountAPI(appId, ssoid, client, accountUrl);
        accountAPI.init();

        BettingAPI bettingAPI = new BettingAPI(appId, ssoid, client, bettingUrl);
        bettingAPI.init();

        BetfairAPI api = new BetfairAPI();
        api.setAccountAPI(accountAPI);
        api.setBettingAPI(bettingAPI);

        return api;
    }
}
