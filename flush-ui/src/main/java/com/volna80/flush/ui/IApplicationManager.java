package com.volna80.flush.ui;

import com.volna80.betfair.api.model.Ssoid;
import com.volna80.flush.ui.server.IOrdersController;

import java.io.IOException;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface IApplicationManager {


    /**
     * Init and start UI
     */
    void start() throws IOException;

    /**
     * invokes after successful logon
     *
     * @param ssoid session token
     */
    void logonPassed(Ssoid ssoid);

    /**
     * Shutdown the application
     *
     * @param reason  reason
     * @param isError true if exit is unexpected due to an application error
     */
    void exit(String reason, boolean isError);

    //===================================================================
    // core services
    //===================================================================

    void openBuySell(String marketId, long selectionId);


    //===================================================================

    void showInstrumentViewer();

    void showBlotter();

    void closeLogon();

    void showPreferences();


    void registerOrderController(IOrdersController ordersController);

    void showMarketOverview();
}
