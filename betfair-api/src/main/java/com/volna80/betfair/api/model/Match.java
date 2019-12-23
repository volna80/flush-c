package com.volna80.betfair.api.model;

import java.util.Date;

/**
 * (c) All rights reserved
 * <p/>
 * An individual bet Match, or rollup by price or avg price. Rollup depends on the requested MatchProjection
 *
 * @author nikolay.volnov@gmail.com
 */
public class Match {

    /**
     * Only present if no rollup
     */
    private String betId;

    /**
     * Only present if no rollup
     */
    private String matchId;

    /**
     * Indicates if the bet is a Back or a LAY
     */
    private Side side;

    /**
     * Either actual match price or avg match price depending on rollup.
     */
    private double price;

    /**
     * Size matched at in this fragment, or at this price or avg price depending on rollup
     */
    private double size;

    /**
     * Only present if no rollup
     */
    private Date matchDate;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    @Override
    public String toString() {
        return "match[" + betId + '|' + matchId + '|' + side + '|' + size + '@' + price + ']';
    }
}
