package com.cityone.parentmodels;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelPayCardsPro implements Serializable {

    private ArrayList<Cards> cards;
    private int result_size;

    public void setCards(ArrayList<Cards> cards){
        this.cards = cards;
    }
    public ArrayList<Cards> getCards(){
        return this.cards;
    }
    public void setResult_size(int result_size){
        this.result_size = result_size;
    }
    public int getResult_size(){
        return this.result_size;
    }

    public class Cards implements Serializable
    {
        private String bin;

        private String status;

        private String token;

        private String holder_name;

        private String expiry_year;

        private String expiry_month;

        private String transaction_reference;

        private String type;

        private String number;

        public void setBin(String bin){
            this.bin = bin;
        }
        public String getBin(){
            return this.bin;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setToken(String token){
            this.token = token;
        }
        public String getToken(){
            return this.token;
        }
        public void setHolder_name(String holder_name){
            this.holder_name = holder_name;
        }
        public String getHolder_name(){
            return this.holder_name;
        }
        public void setExpiry_year(String expiry_year){
            this.expiry_year = expiry_year;
        }
        public String getExpiry_year(){
            return this.expiry_year;
        }
        public void setExpiry_month(String expiry_month){
            this.expiry_month = expiry_month;
        }
        public String getExpiry_month(){
            return this.expiry_month;
        }
        public void setTransaction_reference(String transaction_reference){
            this.transaction_reference = transaction_reference;
        }
        public String getTransaction_reference(){
            return this.transaction_reference;
        }
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setNumber(String number){
            this.number = number;
        }
        public String getNumber(){
            return this.number;
        }
    }


}
