package com.volna80.betfair.api;

import com.volna80.betfair.api.model.AccountDetails;
import com.volna80.betfair.api.model.AccountFunds;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface IAccountAPI {

    AccountFunds getAccountFunds() throws BetfairException;

    AccountDetails getAccountDetails() throws BetfairException;

}
