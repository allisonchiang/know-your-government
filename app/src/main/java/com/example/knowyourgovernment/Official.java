package com.example.knowyourgovernment;

import java.io.Serializable;

public class Official implements Serializable {

    String office;
    String name;
    String party;

    String address;
    String phone;
    String url;
    String email;
    String photoURL;
    String googlePlus;
    String facebook;
    String twitter;
    String youtube;

    public Official(String office, String name, String party) {
        this.office = office;
        this.name = name;
        this.party = party;
    }

    public String getOffice() {
        return office;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }


    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getUrl() {
        return url;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getGooglePlus() {
        return googlePlus;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public void setGooglePlus(String googlePlus) {
        this.googlePlus = googlePlus;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }
}
