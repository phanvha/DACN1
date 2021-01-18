package com.utt.model;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class mUser {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("DataPothole")
    @Expose
    private data datas;

    public mUser(String status, String code, String message, data datas) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.datas = datas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public data getDatas() {
        return datas;
    }

    public void setDatas(data datas) {
        this.datas = datas;
    }

    public class data{
        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("age")
        @Expose
        private String age;
        @SerializedName("designation")
        @Expose
        private String designation;
        @SerializedName("created_at")
        @Expose
        private String created_at;
        @SerializedName("updated_at")
        @Expose
        private String updated_at;

        public data(int id, String name, String email, String age, String designation, String created_at, String updated_at) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.age = age;
            this.designation = designation;
            this.created_at = created_at;
            this.updated_at = updated_at;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
