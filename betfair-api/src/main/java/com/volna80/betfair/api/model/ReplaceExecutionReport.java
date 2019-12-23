package com.volna80.betfair.api.model;

import java.util.List;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ReplaceExecutionReport {

    List<ReplaceInstructionReport> instructionReports;
    /**
     * Echo of the customerRef if passed.
     */
    private String customerRef;
    private ExecutionReportStatus status;
    private ExecutionReportErrorCode errorCode;
    private String marketId;

    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public ExecutionReportStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionReportStatus status) {
        this.status = status;
    }

    public ExecutionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ExecutionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public List<ReplaceInstructionReport> getInstructionReports() {
        return instructionReports;
    }

    public void setInstructionReports(List<ReplaceInstructionReport> instructionReports) {
        this.instructionReports = instructionReports;
    }

    @Override
    public String toString() {
        return "ReplaceExecutionReport{" +
                "customerRef='" + customerRef + '\'' +
                ", status=" + status +
                ", errorCode=" + errorCode +
                ", marketId='" + marketId + '\'' +
                ", instructionReports=" + instructionReports +
                '}';
    }
}
