package com.mogujie.jarvis.dto.generate;

public class OperationLogWithBLOBs extends OperationLog {
    private String operationType;

    private String preOperationContent;

    private String afterOperationContent;

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getPreOperationContent() {
        return preOperationContent;
    }

    public void setPreOperationContent(String preOperationContent) {
        this.preOperationContent = preOperationContent;
    }

    public String getAfterOperationContent() {
        return afterOperationContent;
    }

    public void setAfterOperationContent(String afterOperationContent) {
        this.afterOperationContent = afterOperationContent;
    }
}