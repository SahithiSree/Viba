package com.android.viba;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GetSet {

    public String homeuid, date, time, homepi, homepd, adimage, adname, adrole,
            aduid, adcity, ademail, username, comment, comuid, userimage, adctry;

    public GetSet() {}

    public GetSet(String homeuid, String date, String time, String homepi, String homepd, String adimage, String adname, String adrole,
                  String aduid, String adcity, String ademail, String username, String comment,
                  String comuid, String userimage, String adctry) {
        this.homeuid = homeuid;
        this.date = date;
        this.time = time;
        this.homepi = homepi;
        this.homepd = homepd;
        this.adimage = adimage;
        this.adname = adname;
        this.adrole = adrole;
        this.aduid = aduid;
        this.adcity = adcity;
        this.ademail = ademail;
        this.username = username;
        this.comment = comment;
        this.comuid = comuid;
        this.userimage = userimage;
        this.adctry = adctry;
    }

    public String getHomeuid() {
        return homeuid;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getHomepi() {
        return homepi;
    }

    public String getHomepd() {
        return homepd;
    }

    public String getAdimage() {
        return adimage;
    }

    public String getAdname() {
        return adname;
    }

    public String getAdrole() {
        return adrole;
    }

    public String getAduid() {
        return aduid;
    }

    public String getAdcity() {
        return adcity;
    }

    public String getAdemail() {
        return ademail;
    }

    public String getUsername() {
        return username;
    }

    public String getCommments() {
        return comment;
    }

    public String getComuid() {
        return comuid;
    }

    public String getUserimage() {
        return userimage;
    }

    public String getAdctry() {
        return adctry;
    }
}
