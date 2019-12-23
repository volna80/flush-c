package com.volna80.betfair.api.model;

import java.util.List;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class UpdateExecutionReport {

    /**
     * Echo of the customerRef if passed.
     */
    private String customerRef;


    private ExecutionReportStatus status;

    private ExecutionReportErrorCode errorCode;

    private String marketId;

    private List<UpdateInstructionReport> instructionReports;

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

    public List<UpdateInstructionReport> getInstructionReports() {
        return instructionReports;
    }

    public void setInstructionReports(List<UpdateInstructionReport> instructionReports) {
        this.instructionReports = instructionReports;
    }

    @Override
    public String toString() {
        return "report[" + customerRef + '|' +
                status + '|' +
                errorCode + '|' +
                marketId + '|' +
                instructionReports +
                ']';
    }
}
