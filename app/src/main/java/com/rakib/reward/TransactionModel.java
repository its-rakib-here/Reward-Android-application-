package com.rakib.reward;

public class TransactionModel {

    String id, userId, points, type, reason, createdAt;

    public TransactionModel(String id, String userId, String points,
                            String type, String reason, String createdAt) {

        this.id = id;
        this.userId = userId;
        this.points = points;
        this.type = type;
        this.reason = reason;
        this.createdAt = createdAt;
    }
}