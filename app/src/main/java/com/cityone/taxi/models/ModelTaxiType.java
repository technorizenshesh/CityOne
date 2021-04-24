package com.cityone.taxi.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelTaxiType implements Serializable {

    private ArrayList<Result> result;
    private String status;
    private String message;

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }
    public ArrayList<Result> getResult() {
        return this.result;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return this.status;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }

    public class Result
    {
        private String id;

        private String car_name;

        private String car_image;

        private String charge;

        private String no_of_seats;

        private String min_charge;

        private String per_km;

        private String hold_charge;

        private String ride_time_charge_permin;

        private String service_tax;

        private String free_time_min;

        private String status;

        private String distance;

        private String miles;

        private int perMin;

        private String amount;

        private String cab_find;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setCar_name(String car_name){
            this.car_name = car_name;
        }
        public String getCar_name(){
            return this.car_name;
        }
        public void setCar_image(String car_image){
            this.car_image = car_image;
        }
        public String getCar_image(){
            return this.car_image;
        }
        public void setCharge(String charge){
            this.charge = charge;
        }
        public String getCharge(){
            return this.charge;
        }
        public void setNo_of_seats(String no_of_seats){
            this.no_of_seats = no_of_seats;
        }
        public String getNo_of_seats(){
            return this.no_of_seats;
        }
        public void setMin_charge(String min_charge){
            this.min_charge = min_charge;
        }
        public String getMin_charge(){
            return this.min_charge;
        }
        public void setPer_km(String per_km){
            this.per_km = per_km;
        }
        public String getPer_km(){
            return this.per_km;
        }
        public void setHold_charge(String hold_charge){
            this.hold_charge = hold_charge;
        }
        public String getHold_charge(){
            return this.hold_charge;
        }
        public void setRide_time_charge_permin(String ride_time_charge_permin){
            this.ride_time_charge_permin = ride_time_charge_permin;
        }
        public String getRide_time_charge_permin(){
            return this.ride_time_charge_permin;
        }
        public void setService_tax(String service_tax){
            this.service_tax = service_tax;
        }
        public String getService_tax(){
            return this.service_tax;
        }
        public void setFree_time_min(String free_time_min){
            this.free_time_min = free_time_min;
        }
        public String getFree_time_min(){
            return this.free_time_min;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setDistance(String distance){
            this.distance = distance;
        }
        public String getDistance(){
            return this.distance;
        }
        public void setMiles(String miles){
            this.miles = miles;
        }
        public String getMiles(){
            return this.miles;
        }
        public void setPerMin(int perMin){
            this.perMin = perMin;
        }
        public int getPerMin(){
            return this.perMin;
        }
        public void setAmount(String amount){
            this.amount = amount;
        }
        public String getAmount(){
            return this.amount;
        }
        public void setCab_find(String cab_find){
            this.cab_find = cab_find;
        }
        public String getCab_find(){
            return this.cab_find;
        }
    }


}
