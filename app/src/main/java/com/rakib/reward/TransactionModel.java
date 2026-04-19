package com.rakib.reward;

public class TransactionModel {

    public String id;
    public String userId;
    public String points;
    public String type;
    public String reason;
    public String createdAt;
    public String name;
    public String phone;
    public String totall_amount;
    public TransactionModel(String id, String userId,
                            String points, String type,
                            String reason, String createdAt,
                            String name, String phone,String totall_amount) {

        this.id = id;
        this.userId = userId;
        this.points = points;
        this.type = type;
        this.reason = reason;
        this.createdAt = createdAt;
        this.name = name;
        this.phone = phone;
        this.totall_amount=totall_amount;
    }
}