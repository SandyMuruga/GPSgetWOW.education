package com.gpsgetwoweducation.networks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static String BASE_URL = ApiUrls.BASE_URL_REGISTERED_CUSTOMER;
    public static String BASE_URL_REGISTERED_CUSTOMER_MOBILE_NUMBER = ApiUrls.BASE_URL_REGISTERED_CUSTOMER_MOBILE_NUMBER; // registered customer mobile number
    public static String BASE_URL_USER_LOGIN = ApiUrls.BASE_URL_USER_LOGIN; // login
    public static String BASE_URL_INSERT_GPS_TRACKING = ApiUrls.BASE_URL_INSERT_GPS_TRACKING; // insert gps tracking

    private static Retrofit retrofit = null;
    private static Retrofit retrofitRegisteredCustomerMobileNumber;
    private static Retrofit retrofitUserLogin = null;

    private static Retrofit retrofitInsertGPSTracking = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = createRetrofit(BASE_URL);
        }
        return retrofit;
    }

    public static Retrofit getRegisteredCustomerMobileNumber() {
        if (retrofitRegisteredCustomerMobileNumber == null) {
            retrofitRegisteredCustomerMobileNumber = createRetrofit(BASE_URL_REGISTERED_CUSTOMER_MOBILE_NUMBER);
        }
        return retrofitRegisteredCustomerMobileNumber;
    }

    public static Retrofit getUserLogin() {
        if (retrofitUserLogin == null) {
            retrofitUserLogin = createRetrofit(BASE_URL_USER_LOGIN);
        }
        return retrofitUserLogin;
    }

    // insert gps tracking
    public static Retrofit getInsertGPSTracking() {
        if (retrofitInsertGPSTracking == null) {
            retrofitInsertGPSTracking = createRetrofit(BASE_URL_INSERT_GPS_TRACKING);
        }
        return retrofitInsertGPSTracking;
    }

    private static Retrofit createRetrofit(String baseUrl) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(240, TimeUnit.SECONDS)
                .connectTimeout(240, TimeUnit.SECONDS)
                .addInterceptor(new NetworkInterceptor())
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
