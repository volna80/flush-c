package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class UpdateInstructionReport {

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
    private UpdateInstruction instruction;

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

    public UpdateInstruction getInstruction() {
        return instruction;
    }

    public void setInstruction(UpdateInstruction instruction) {
        this.instruction = instruction;
    }

    @Override
    public String toString() {
        return "[" + status + '|' + errorCode + '|' + instruction + ']';
    }
}
