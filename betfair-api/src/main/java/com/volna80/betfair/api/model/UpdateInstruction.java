package com.volna80.betfair.api.model;

/**
 * Instruction to update LIMIT bet's persistence of an order that do not affect exposure
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class UpdateInstruction {

    /**
     * Unique identifier for the bet
     */
    private String betId;


    /**
     * The new persistence type to update this bet to
     */
    private PersistenceType newPersistenceType;


    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public PersistenceType getNewPersistenceType() {
        return newPersistenceType;
    }

    public void setNewPersistenceType(PersistenceType newPersistenceType) {
        this.newPersistenceType = newPersistenceType;
    }

    @Override
    public String toString() {
        return "update[" + newPersistenceType + '|' + betId + ']';
    }
}
