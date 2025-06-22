package com.example.doanmobile.model;

import androidx.annotation.Nullable;
import java.io.Serializable;

/** Model bản ghi users/{uid} */
public class UserModel implements Serializable {
    private String userId;
    private String mail;
    private String passwordHash;
    private String role;   // "user", "admin"...

    @Nullable private String sdt;
    @Nullable private String fullName;
    @Nullable private String ngaySinh;
    @Nullable private String gioiTinh;

    public UserModel() {}          // required by Firestore

    public UserModel(String uid, String mail, String passwordHash,String fullName) {
        this.userId       = uid;
        this.mail         = mail;
        this.passwordHash = passwordHash;
        this.role         = "user";
        this.fullName =fullName;
    }

    public UserModel(String userId, String mail, String passwordHash) {
        this.userId = userId;
        this.mail = mail;
        this.passwordHash = passwordHash;
    }

    @Nullable
    public String getName() {
        return fullName;
    }

    public void setName(@Nullable String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Nullable
    public String getSdt() {
        return sdt;
    }

    public void setSdt(@Nullable String sdt) {
        this.sdt = sdt;
    }

    @Nullable
    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(@Nullable String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    @Nullable
    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(@Nullable String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }
}
