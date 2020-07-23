package com.instantnannies.user.Retrofit;

import com.android.volley.Response;

import okhttp3.ResponseBody;

public interface RetrofitCallback {
    public void Success(Response<ResponseBody> response);

    public void Failure(Throwable errorResponse);
}
