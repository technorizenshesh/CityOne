
package com.cityone.stores.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemSubModel {

    @SerializedName("result")
    @Expose
    private List<Result> result = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
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

    public class Result {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("restaurant_id")
        @Expose
        private String restaurantId;
        @SerializedName("sub_admin_id")
        @Expose
        private String subAdminId;
        @SerializedName("date_time")
        @Expose
        private String dateTime;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("main_category_id")
        @Expose
        private String mainCategoryId;
        @SerializedName("created_at")
        @Expose
        private String createdAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRestaurantId() {
            return restaurantId;
        }

        public void setRestaurantId(String restaurantId) {
            this.restaurantId = restaurantId;
        }

        public String getSubAdminId() {
            return subAdminId;
        }

        public void setSubAdminId(String subAdminId) {
            this.subAdminId = subAdminId;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMainCategoryId() {
            return mainCategoryId;
        }

        public void setMainCategoryId(String mainCategoryId) {
            this.mainCategoryId = mainCategoryId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

    }

}

