package com.volna80.betfair.api.model;

import com.google.gson.annotations.JsonAdapter;
import com.volna80.betfair.api.model.adapter.SizeCancelledAdapter;

import java.util.Date;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class CancelInstructionReport {

    /**
     * whether the command succeeded or failed
     */
    private InstructionReportStatus status;


    /**
     * cause of failure, or null if command succeeds
     */
    private InstructionReportErrorCode errorCode;


    /**
     * The instruction that was requested
     */
    private CancelInstruction instruction;


    @JsonAdapter(SizeCancelledAdapter.class)
    private int sizeCancelled;


    private Date cancelledDate;

    public InstructionReportStatus getStatus() {
        return status;
    }

    public void setStatus(InstructionReportStatus status) {
        this.status = status;
    }

    public InstructionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(InstructionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CancelInstruction getInstruction() {
        return instruction;
    }

    public void setInstruction(CancelInstruction instruction) {
        this.instruction = instruction;
    }

    public int getSizeCancelled() {
        return sizeCancelled;
    }

    public void setSizeCancelled(int sizeCancelled) {
        this.sizeCancelled = sizeCancelled;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    @Override
    public String toString() {
        return "CancelInstructionReport{" +
                "status=" + status +
                ", errorCode=" + errorCode +
                ", instruction=" + instruction +
                ", sizeCancelled=" + sizeCancelled +
                ", cancelledDate=" + cancelledDate +
                '}';
    }

}
