package com.kwjj.filicash.rv_history;

public class transactionData {
    String historyRef;
    String flow;
    String label;
    String amount;
    String date;
    String time;
    Integer isPrintable;

    public transactionData(String historyRef, String flow, String label, String amount, String date, String time, Integer isPrintable) {
        this.historyRef = historyRef;
        this.flow = flow;
        this.label = label;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.isPrintable = isPrintable;
    }

    public String getHistoryRef() {
        return historyRef;
    }

    public void setHistoryRef(String historyRef) {
        this.historyRef = historyRef;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getIsPrintable() {
        return isPrintable;
    }

    public void setIsPrintable(Integer isPrintable) {
        this.isPrintable = isPrintable;
    }
}
