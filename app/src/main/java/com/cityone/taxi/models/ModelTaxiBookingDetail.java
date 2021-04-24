package com.cityone.taxi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ModelTaxiBookingDetail implements Serializable {

    @SerializedName("result")
    @Expose
    private Result result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Result implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("driver_id")
        @Expose
        private String driverId;
        @SerializedName("picuplocation")
        @Expose
        private String picuplocation;
        @SerializedName("dropofflocation")
        @Expose
        private String dropofflocation;
        @SerializedName("picuplat")
        @Expose
        private String picuplat;
        @SerializedName("pickuplon")
        @Expose
        private String pickuplon;
        @SerializedName("droplat")
        @Expose
        private String droplat;
        @SerializedName("droplon")
        @Expose
        private String droplon;
        @SerializedName("booktype")
        @Expose
        private String booktype;
        @SerializedName("picklaterdate")
        @Expose
        private String picklaterdate;
        @SerializedName("picklatertime")
        @Expose
        private String picklatertime;
        @SerializedName("car_type_id")
        @Expose
        private String carTypeId;
        @SerializedName("timezone")
        @Expose
        private String timezone;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("req_datetime")
        @Expose
        private String reqDatetime;
        @SerializedName("estimate_charge_amount")
        @Expose
        private String estimateChargeAmount;
        @SerializedName("send_drivers")
        @Expose
        private String sendDrivers;
        @SerializedName("start_time")
        @Expose
        private String startTime;
        @SerializedName("favorite_ride")
        @Expose
        private String favoriteRide;
        @SerializedName("user_rating_status")
        @Expose
        private String userRatingStatus;
        @SerializedName("tip_amount")
        @Expose
        private String tipAmount;
        @SerializedName("end_time")
        @Expose
        private String endTime;
        @SerializedName("shareride_type")
        @Expose
        private String sharerideType;
        @SerializedName("car_seats")
        @Expose
        private String carSeats;
        @SerializedName("passenger")
        @Expose
        private String passenger;
        @SerializedName("booked_seats")
        @Expose
        private String bookedSeats;
        @SerializedName("route_img")
        @Expose
        private String routeImg;
        @SerializedName("accept_time")
        @Expose
        private String acceptTime;
        @SerializedName("reason_id")
        @Expose
        private String reasonId;
        @SerializedName("payment_type")
        @Expose
        private String paymentType;
        @SerializedName("apply_code")
        @Expose
        private String applyCode;
        @SerializedName("card_id")
        @Expose
        private String cardId;
        @SerializedName("arrived_time")
        @Expose
        private String arrivedTime;
        @SerializedName("finish_date")
        @Expose
        private String finishDate;
        @SerializedName("driver_ids")
        @Expose
        private String driverIds;
        @SerializedName("user_details")
        @Expose
        private UserDetails userDetails;
        @SerializedName("driver_details")
        @Expose
        private DriverDetails driverDetails;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public String getPicuplocation() {
            return picuplocation;
        }

        public void setPicuplocation(String picuplocation) {
            this.picuplocation = picuplocation;
        }

        public String getDropofflocation() {
            return dropofflocation;
        }

        public void setDropofflocation(String dropofflocation) {
            this.dropofflocation = dropofflocation;
        }

        public String getPicuplat() {
            return picuplat;
        }

        public void setPicuplat(String picuplat) {
            this.picuplat = picuplat;
        }

        public String getPickuplon() {
            return pickuplon;
        }

        public void setPickuplon(String pickuplon) {
            this.pickuplon = pickuplon;
        }

        public String getDroplat() {
            return droplat;
        }

        public void setDroplat(String droplat) {
            this.droplat = droplat;
        }

        public String getDroplon() {
            return droplon;
        }

        public void setDroplon(String droplon) {
            this.droplon = droplon;
        }

        public String getBooktype() {
            return booktype;
        }

        public void setBooktype(String booktype) {
            this.booktype = booktype;
        }

        public String getPicklaterdate() {
            return picklaterdate;
        }

        public void setPicklaterdate(String picklaterdate) {
            this.picklaterdate = picklaterdate;
        }

        public String getPicklatertime() {
            return picklatertime;
        }

        public void setPicklatertime(String picklatertime) {
            this.picklatertime = picklatertime;
        }

        public String getCarTypeId() {
            return carTypeId;
        }

        public void setCarTypeId(String carTypeId) {
            this.carTypeId = carTypeId;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReqDatetime() {
            return reqDatetime;
        }

        public void setReqDatetime(String reqDatetime) {
            this.reqDatetime = reqDatetime;
        }

        public String getEstimateChargeAmount() {
            return estimateChargeAmount;
        }

        public void setEstimateChargeAmount(String estimateChargeAmount) {
            this.estimateChargeAmount = estimateChargeAmount;
        }

        public String getSendDrivers() {
            return sendDrivers;
        }

        public void setSendDrivers(String sendDrivers) {
            this.sendDrivers = sendDrivers;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getFavoriteRide() {
            return favoriteRide;
        }

        public void setFavoriteRide(String favoriteRide) {
            this.favoriteRide = favoriteRide;
        }

        public String getUserRatingStatus() {
            return userRatingStatus;
        }

        public void setUserRatingStatus(String userRatingStatus) {
            this.userRatingStatus = userRatingStatus;
        }

        public String getTipAmount() {
            return tipAmount;
        }

        public void setTipAmount(String tipAmount) {
            this.tipAmount = tipAmount;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getSharerideType() {
            return sharerideType;
        }

        public void setSharerideType(String sharerideType) {
            this.sharerideType = sharerideType;
        }

        public String getCarSeats() {
            return carSeats;
        }

        public void setCarSeats(String carSeats) {
            this.carSeats = carSeats;
        }

        public String getPassenger() {
            return passenger;
        }

        public void setPassenger(String passenger) {
            this.passenger = passenger;
        }

        public String getBookedSeats() {
            return bookedSeats;
        }

        public void setBookedSeats(String bookedSeats) {
            this.bookedSeats = bookedSeats;
        }

        public String getRouteImg() {
            return routeImg;
        }

        public void setRouteImg(String routeImg) {
            this.routeImg = routeImg;
        }

        public String getAcceptTime() {
            return acceptTime;
        }

        public void setAcceptTime(String acceptTime) {
            this.acceptTime = acceptTime;
        }

        public String getReasonId() {
            return reasonId;
        }

        public void setReasonId(String reasonId) {
            this.reasonId = reasonId;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getApplyCode() {
            return applyCode;
        }

        public void setApplyCode(String applyCode) {
            this.applyCode = applyCode;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getArrivedTime() {
            return arrivedTime;
        }

        public void setArrivedTime(String arrivedTime) {
            this.arrivedTime = arrivedTime;
        }

        public String getFinishDate() {
            return finishDate;
        }

        public void setFinishDate(String finishDate) {
            this.finishDate = finishDate;
        }

        public String getDriverIds() {
            return driverIds;
        }

        public void setDriverIds(String driverIds) {
            this.driverIds = driverIds;
        }

        public UserDetails getUserDetails() {
            return userDetails;
        }

        public void setUserDetails(UserDetails userDetails) {
            this.userDetails = userDetails;
        }

        public DriverDetails getDriverDetails() {
            return driverDetails;
        }

        public void setDriverDetails(DriverDetails driverDetails) {
            this.driverDetails = driverDetails;
        }

        public class DriverDetails implements Serializable{

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("user_name")
            @Expose
            private String userName;
            @SerializedName("mobile")
            @Expose
            private String mobile;
            @SerializedName("email")
            @Expose
            private String email;
            @SerializedName("password")
            @Expose
            private String password;
            @SerializedName("image")
            @Expose
            private String image;
            @SerializedName("otp")
            @Expose
            private String otp;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("lat")
            @Expose
            private String lat;
            @SerializedName("lon")
            @Expose
            private String lon;
            @SerializedName("address")
            @Expose
            private String address;
            @SerializedName("social_id")
            @Expose
            private String socialId;
            @SerializedName("date_time")
            @Expose
            private String dateTime;
            @SerializedName("ios_register_id")
            @Expose
            private Object iosRegisterId;
            @SerializedName("register_id")
            @Expose
            private String registerId;
            @SerializedName("land_mark")
            @Expose
            private String landMark;
            @SerializedName("title")
            @Expose
            private String title;
            @SerializedName("type")
            @Expose
            private String type;
            @SerializedName("document1")
            @Expose
            private String document1;
            @SerializedName("document2")
            @Expose
            private String document2;
            @SerializedName("online_status")
            @Expose
            private String onlineStatus;
            @SerializedName("sub_admin_id")
            @Expose
            private String subAdminId;
            @SerializedName("booking_status")
            @Expose
            private String bookingStatus;
            @SerializedName("car_type_id")
            @Expose
            private String carTypeId;
            @SerializedName("brand")
            @Expose
            private String brand;
            @SerializedName("car_model")
            @Expose
            private String carModel;
            @SerializedName("year_of_manufacture")
            @Expose
            private String yearOfManufacture;
            @SerializedName("car_number")
            @Expose
            private String carNumber;
            @SerializedName("car_image")
            @Expose
            private String carImage;
            @SerializedName("req_datetime")
            @Expose
            private String reqDatetime;
            @SerializedName("driver_image")
            @Expose
            private String driverImage;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getOtp() {
                return otp;
            }

            public void setOtp(String otp) {
                this.otp = otp;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLon() {
                return lon;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getSocialId() {
                return socialId;
            }

            public void setSocialId(String socialId) {
                this.socialId = socialId;
            }

            public String getDateTime() {
                return dateTime;
            }

            public void setDateTime(String dateTime) {
                this.dateTime = dateTime;
            }

            public Object getIosRegisterId() {
                return iosRegisterId;
            }

            public void setIosRegisterId(Object iosRegisterId) {
                this.iosRegisterId = iosRegisterId;
            }

            public String getRegisterId() {
                return registerId;
            }

            public void setRegisterId(String registerId) {
                this.registerId = registerId;
            }

            public String getLandMark() {
                return landMark;
            }

            public void setLandMark(String landMark) {
                this.landMark = landMark;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getDocument1() {
                return document1;
            }

            public void setDocument1(String document1) {
                this.document1 = document1;
            }

            public String getDocument2() {
                return document2;
            }

            public void setDocument2(String document2) {
                this.document2 = document2;
            }

            public String getOnlineStatus() {
                return onlineStatus;
            }

            public void setOnlineStatus(String onlineStatus) {
                this.onlineStatus = onlineStatus;
            }

            public String getSubAdminId() {
                return subAdminId;
            }

            public void setSubAdminId(String subAdminId) {
                this.subAdminId = subAdminId;
            }

            public String getBookingStatus() {
                return bookingStatus;
            }

            public void setBookingStatus(String bookingStatus) {
                this.bookingStatus = bookingStatus;
            }

            public String getCarTypeId() {
                return carTypeId;
            }

            public void setCarTypeId(String carTypeId) {
                this.carTypeId = carTypeId;
            }

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getCarModel() {
                return carModel;
            }

            public void setCarModel(String carModel) {
                this.carModel = carModel;
            }

            public String getYearOfManufacture() {
                return yearOfManufacture;
            }

            public void setYearOfManufacture(String yearOfManufacture) {
                this.yearOfManufacture = yearOfManufacture;
            }

            public String getCarNumber() {
                return carNumber;
            }

            public void setCarNumber(String carNumber) {
                this.carNumber = carNumber;
            }

            public String getCarImage() {
                return carImage;
            }

            public void setCarImage(String carImage) {
                this.carImage = carImage;
            }

            public String getReqDatetime() {
                return reqDatetime;
            }

            public void setReqDatetime(String reqDatetime) {
                this.reqDatetime = reqDatetime;
            }

            public String getDriverImage() {
                return driverImage;
            }

            public void setDriverImage(String driverImage) {
                this.driverImage = driverImage;
            }

        }

        public class UserDetails implements Serializable{

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("user_name")
            @Expose
            private String userName;
            @SerializedName("mobile")
            @Expose
            private String mobile;
            @SerializedName("email")
            @Expose
            private String email;
            @SerializedName("password")
            @Expose
            private String password;
            @SerializedName("image")
            @Expose
            private String image;
            @SerializedName("otp")
            @Expose
            private String otp;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("lat")
            @Expose
            private String lat;
            @SerializedName("lon")
            @Expose
            private String lon;
            @SerializedName("address")
            @Expose
            private String address;
            @SerializedName("social_id")
            @Expose
            private String socialId;
            @SerializedName("date_time")
            @Expose
            private String dateTime;
            @SerializedName("ios_register_id")
            @Expose
            private Object iosRegisterId;
            @SerializedName("register_id")
            @Expose
            private String registerId;
            @SerializedName("land_mark")
            @Expose
            private String landMark;
            @SerializedName("title")
            @Expose
            private String title;
            @SerializedName("type")
            @Expose
            private String type;
            @SerializedName("document1")
            @Expose
            private String document1;
            @SerializedName("document2")
            @Expose
            private String document2;
            @SerializedName("online_status")
            @Expose
            private String onlineStatus;
            @SerializedName("sub_admin_id")
            @Expose
            private String subAdminId;
            @SerializedName("booking_status")
            @Expose
            private String bookingStatus;
            @SerializedName("car_type_id")
            @Expose
            private String carTypeId;
            @SerializedName("brand")
            @Expose
            private String brand;
            @SerializedName("car_model")
            @Expose
            private String carModel;
            @SerializedName("year_of_manufacture")
            @Expose
            private String yearOfManufacture;
            @SerializedName("car_number")
            @Expose
            private String carNumber;
            @SerializedName("car_image")
            @Expose
            private String carImage;
            @SerializedName("req_datetime")
            @Expose
            private String reqDatetime;
            @SerializedName("user_image")
            @Expose
            private String userImage;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getOtp() {
                return otp;
            }

            public void setOtp(String otp) {
                this.otp = otp;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLon() {
                return lon;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getSocialId() {
                return socialId;
            }

            public void setSocialId(String socialId) {
                this.socialId = socialId;
            }

            public String getDateTime() {
                return dateTime;
            }

            public void setDateTime(String dateTime) {
                this.dateTime = dateTime;
            }

            public Object getIosRegisterId() {
                return iosRegisterId;
            }

            public void setIosRegisterId(Object iosRegisterId) {
                this.iosRegisterId = iosRegisterId;
            }

            public String getRegisterId() {
                return registerId;
            }

            public void setRegisterId(String registerId) {
                this.registerId = registerId;
            }

            public String getLandMark() {
                return landMark;
            }

            public void setLandMark(String landMark) {
                this.landMark = landMark;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getDocument1() {
                return document1;
            }

            public void setDocument1(String document1) {
                this.document1 = document1;
            }

            public String getDocument2() {
                return document2;
            }

            public void setDocument2(String document2) {
                this.document2 = document2;
            }

            public String getOnlineStatus() {
                return onlineStatus;
            }

            public void setOnlineStatus(String onlineStatus) {
                this.onlineStatus = onlineStatus;
            }

            public String getSubAdminId() {
                return subAdminId;
            }

            public void setSubAdminId(String subAdminId) {
                this.subAdminId = subAdminId;
            }

            public String getBookingStatus() {
                return bookingStatus;
            }

            public void setBookingStatus(String bookingStatus) {
                this.bookingStatus = bookingStatus;
            }

            public String getCarTypeId() {
                return carTypeId;
            }

            public void setCarTypeId(String carTypeId) {
                this.carTypeId = carTypeId;
            }

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getCarModel() {
                return carModel;
            }

            public void setCarModel(String carModel) {
                this.carModel = carModel;
            }

            public String getYearOfManufacture() {
                return yearOfManufacture;
            }

            public void setYearOfManufacture(String yearOfManufacture) {
                this.yearOfManufacture = yearOfManufacture;
            }

            public String getCarNumber() {
                return carNumber;
            }

            public void setCarNumber(String carNumber) {
                this.carNumber = carNumber;
            }

            public String getCarImage() {
                return carImage;
            }

            public void setCarImage(String carImage) {
                this.carImage = carImage;
            }

            public String getReqDatetime() {
                return reqDatetime;
            }

            public void setReqDatetime(String reqDatetime) {
                this.reqDatetime = reqDatetime;
            }

            public String getUserImage() {
                return userImage;
            }

            public void setUserImage(String userImage) {
                this.userImage = userImage;
            }

        }

    }
}
