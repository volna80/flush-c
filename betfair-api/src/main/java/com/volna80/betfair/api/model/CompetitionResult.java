package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class CompetitionResult {

    private Competition competition;
    private int marketCount;
    private String competitionRegion;


    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public int getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(int marketCount) {
        this.marketCount = marketCount;
    }

    public String getCompetitionRegion() {
        return competitionRegion;
    }

    public void setCompetitionRegion(String competitionRegion) {
        this.competitionRegion = competitionRegion;
    }

    @Override
    public String toString() {
        return "CompetitionResult{" +
                "competition=" + competition +
                ", marketCount=" + marketCount +
                ", competitionRegion='" + competitionRegion + '\'' +
                '}';
    }
}
