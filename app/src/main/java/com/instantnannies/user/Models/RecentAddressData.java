package com.instantnannies.user.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecentAddressData {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;
    @SerializedName("type")
    @Expose
    public String type;


    public String recentLocationName;
}
