package com.rakib.reward;

public class WithdrawRequest {

    public String id;
    public String name;
    public String phone;
    public String amount;
    public String points;
    public String account;
    public String status;

    public WithdrawRequest(String id, String name, String phone,
                           String amount, String points,
                           String account, String status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.amount = amount;
        this.points = points;
        this.account = account;
        this.status = status;
    }
}