package com.example.camp_proj1;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MoneyInfo {

    String writer, date, money, account;
    ArrayList<String>  participants,participants_number;

    public MoneyInfo(String writer, ArrayList<String> participants, ArrayList<String> participants_number, String date, String money, String account){
        this.writer = writer;
        this.participants  = participants;
        this.date= date;
        this.money = money;
        this.account = account ;
        this.participants_number = participants_number;

    }

    public String getWriter(){
        return writer;
    }

    public ArrayList<String> getParticipants(){return participants; }

    public ArrayList<String> getParticipants_number(){return participants_number;}

    public String getDate(){ return date; }

    public String getMoney(){ return money;}

    public String getAccount(){return account; }


}

//사람이름하고 사람전화번호를 올려줌