package com.volna80.betfair.api.model;

/**
 * Profit and loss if selection is wins or loses
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class RunnerProfitAndLoss {


    /**
     * The unique identifier for the selection
     */
    private long selectionId;


    /**
     * Profit and loss for the market if this selection is the winner
     */
    private double ifWin = Double.NaN; //TODO int


    /**
     * Profit and loss for the market if this selection is the loser. Only returned for multi-winner odds markets.
     */
    private double ifLose = Double.NaN; //TODO int

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    public double getIfWin() {
        return ifWin;
    }

    public void setIfWin(double ifWin) {
        this.ifWin = ifWin;
    }

    public double getIfLose() {
        return ifLose;
    }

    public void setIfLose(double ifLose) {
        this.ifLose = ifLose;
    }

    @Override
    public String toString() {
        return "RunnerProfitAndLoss{" +
                "selectionId=" + selectionId +
                ", ifWin=" + ifWin +
                ", ifLose=" + ifLose +
                '}';
    }
}
