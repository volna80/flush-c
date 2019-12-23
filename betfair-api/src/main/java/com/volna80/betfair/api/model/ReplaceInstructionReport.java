package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ReplaceInstructionReport {

    /**
     * whether the command succeeded or failed
     */
    private InstructionReportStatus status;

    /**
     * cause of failure, or null if command succeeds
     */
    private InstructionReportErrorCode errorCode;

    /**
     * Cancelation report for the original order
     */
    private CancelInstructionReport cancelInstructionReport;

    /**
     * Placement report for the new order
     */
    private PlaceInstructionReport placeInstructionReport;


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

    public CancelInstructionReport getCancelInstructionReport() {
        return cancelInstructionReport;
    }

    public void setCancelInstructionReport(CancelInstructionReport cancelInstructionReport) {
        this.cancelInstructionReport = cancelInstructionReport;
    }

    public PlaceInstructionReport getPlaceInstructionReport() {
        return placeInstructionReport;
    }

    public void setPlaceInstructionReport(PlaceInstructionReport placeInstructionReport) {
        this.placeInstructionReport = placeInstructionReport;
    }

    @Override
    public String toString() {
        return "ReplaceInstructionReport{" +
                "status=" + status +
                ", errorCode=" + errorCode +
                ", cancelInstructionReport=" + cancelInstructionReport +
                ", placeInstructionReport=" + placeInstructionReport +
                '}';
    }
}
