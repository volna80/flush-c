package com.volna80.flush.ui.server;

import com.volna80.betfair.api.model.CurrentOrderSummary;
import com.volna80.betfair.api.model.Side;
import com.volna80.flush.ui.model.Message;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface IOrdersController {

    Collection<CurrentOrderSummary> getCompleteOrders();

    default List<CurrentOrderSummary> getCompleteOrders(String marketId) {
        return getCompleteOrders().stream().filter(order -> order.getMarketId().equals(marketId)).collect(Collectors.toList());
    }

    /**
     * @return list of live orders
     */
    Collection<CurrentOrderSummary> getExecutableOrders();

    default Collection<CurrentOrderSummary> getExecutableOrders(String marketId) {
        return getExecutableOrders().stream().filter(order -> order.getMarketId().equals(marketId)).collect(Collectors.toList());
    }

    Collection<CurrentOrderSummary> getExecutableOrders(String marketId, long selectionId);

    /**
     * @return list of orders in waiting state
     */
    Collection<CurrentOrderSummary> getUnconfirmedOrders();


    /**
     * @return list of orders which we can cancel (executable + unconfirmed)
     */
    Collection<CurrentOrderSummary> getCancelableOrders(String marketId, long selectionId);

    /**
     * @return lost of orders which is in a cancelling state
     */
    Collection<CurrentOrderSummary> getCancellingOrders(String marketId, long selectionId);

    Collection<CurrentOrderSummary> getUnconfirmedOrders(String marketId, long selectionId);

    void placeNewOrder(String marketId, long selectionId, int price, int size, Side side, String refId);

    void cancelOrders(String marketId, List<String> orderIds);

    /**
     * Cancel all open orders
     *
     * @param marketId market id
     */
    void cancelOrders(String marketId);

    boolean isActive();

    /**
     * @return last outgoing/incoming messages from the previous call
     */
    List<Message> getLastMessages();


    /**
     * @return all orders for a given market which have filled part
     */
    Collection<CurrentOrderSummary> getMatchedOrders(String markedId);
}
