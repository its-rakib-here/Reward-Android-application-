package com.rakib.reward;

public class User_TransactionModel {

    public String type;      // points / withdraw
    public String title;
    public String amount;
    public String action;    // add / deduct / pending / approved
    public String date;
    public String points;
    public User_TransactionModel(String type, String title, String amount, String action, String date,String points) {
        this.type = type;
        this.title = title;
        this.amount = amount;
        this.action = action;
        this.date = date;
        this.points=points;
    }
}
