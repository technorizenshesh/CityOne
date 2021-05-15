package com.cityone.entertainment.movies.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelTheaterDetails implements Serializable {

    private ArrayList<String> slote;
    private Result result;
    private String message;
    private String status;

    public void setSlote(ArrayList<String> slote){
        this.slote = slote;
    }
    public ArrayList<String> getSlote() {
        return this.slote;
    }
    public void setResult(Result result){
        this.result = result;
    }
    public Result getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public class Result implements Serializable
    {
        private String id;

        private String movie_id;

        private String name;

        private String address;

        private String description;

        private String treater_start_date;

        private String treater_end_date;

        private String treater_time_slote1;

        private String treater_time_slote2;

        private String treater_time_slote3;

        private String treater_image;

        private String seat;

        private String date_time;

        private String sub_admin_id;

        private String exclusive;

        private String exclusive_seat;

        private String normal;

        private String normal_seat;

        private String classic;

        private String classic_seat;

        private String exclusive_price;

        private String normal_price;

        private String classic_price;

        public String getExclusive() {
            return exclusive;
        }

        public void setExclusive(String exclusive) {
            this.exclusive = exclusive;
        }

        public String getExclusive_seat() {
            return exclusive_seat;
        }

        public void setExclusive_seat(String exclusive_seat) {
            this.exclusive_seat = exclusive_seat;
        }

        public String getNormal() {
            return normal;
        }

        public void setNormal(String normal) {
            this.normal = normal;
        }

        public String getNormal_seat() {
            return normal_seat;
        }

        public void setNormal_seat(String normal_seat) {
            this.normal_seat = normal_seat;
        }

        public String getClassic() {
            return classic;
        }

        public void setClassic(String classic) {
            this.classic = classic;
        }

        public String getClassic_seat() {
            return classic_seat;
        }

        public void setClassic_seat(String classic_seat) {
            this.classic_seat = classic_seat;
        }

        public String getExclusive_price() {
            return exclusive_price;
        }

        public void setExclusive_price(String exclusive_price) {
            this.exclusive_price = exclusive_price;
        }

        public String getNormal_price() {
            return normal_price;
        }

        public void setNormal_price(String normal_price) {
            this.normal_price = normal_price;
        }

        public String getClassic_price() {
            return classic_price;
        }

        public void setClassic_price(String classic_price) {
            this.classic_price = classic_price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTreater_time_slote1() {
            return treater_time_slote1;
        }

        public void setTreater_time_slote1(String treater_time_slote1) {
            this.treater_time_slote1 = treater_time_slote1;
        }

        public String getTreater_time_slote2() {
            return treater_time_slote2;
        }

        public void setTreater_time_slote2(String treater_time_slote2) {
            this.treater_time_slote2 = treater_time_slote2;
        }

        public String getTreater_time_slote3() {
            return treater_time_slote3;
        }

        public void setTreater_time_slote3(String treater_time_slote3) {
            this.treater_time_slote3 = treater_time_slote3;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setMovie_id(String movie_id){
            this.movie_id = movie_id;
        }
        public String getMovie_id(){
            return this.movie_id;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setTreater_start_date(String treater_start_date){
            this.treater_start_date = treater_start_date;
        }
        public String getTreater_start_date(){
            return this.treater_start_date;
        }
        public void setTreater_end_date(String treater_end_date){
            this.treater_end_date = treater_end_date;
        }
        public String getTreater_end_date(){
            return this.treater_end_date;
        }
        public void setTreater_time_slote(String treater_time_slote){
            this.treater_time_slote1 = treater_time_slote;
        }
        public String getTreater_time_slote(){
            return this.treater_time_slote1;
        }
        public void setTreater_image(String treater_image){
            this.treater_image = treater_image;
        }
        public String getTreater_image(){
            return this.treater_image;
        }
        public void setSeat(String seat){
            this.seat = seat;
        }
        public String getSeat(){
            return this.seat;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setSub_admin_id(String sub_admin_id){
            this.sub_admin_id = sub_admin_id;
        }
        public String getSub_admin_id(){
            return this.sub_admin_id;
        }
    }

    @BindingAdapter("image")
    public static void loadImage(ImageView imageView,String url) {
        Picasso.get().load(url).into(imageView);
    }

}
