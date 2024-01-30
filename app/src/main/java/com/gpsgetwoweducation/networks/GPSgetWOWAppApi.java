package com.gpsgetwoweducation.networks;

import com.google.gson.JsonObject;
import com.gpsgetwoweducation.pojo.insertgpstrackingdata.SendGpsDataBody;
import com.gpsgetwoweducation.pojo.stoplivecoordinates.StopLiveCoordinatesData;
import com.gpsgetwoweducation.pojo.userlogin.SendOTPData;
import com.gpsgetwoweducation.pojo.userlogin.VerifyOTPData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface GPSgetWOWAppApi {

    @GET(ApiUrls.REGISTERED_BUSINESS_CUSTOMER)
    Call<JsonObject> getRegisteredInstitution(@Query("current_latitude") String current_latitude,
                                              @Query("current_longitude") String current_longitude);

    // Registered Customer Mobile Number
    @GET(ApiUrls.REGISTERED_CUSTOMER_MOBILE_NUMBER)
    Call<JsonObject> getRegisteredCustomerMobileNumber(@Query("registered_mobile_number") String registered_mobile_number,
                                                       @Query("registered_mobile_country_code") String registered_mobile_country_code,
                                                       @Query("app_name") String app_name);

    @GET(ApiUrls.USER_LOGIN)
    Call<JsonObject> getUserLogin(@Query("user_id") String user_id,
                                  @Query("user_password") String user_password,
                                  @Query("app_name") String app_name);

    @POST(ApiUrls.SEND_OTP)
    Call<JsonObject> sendOTP(@Body SendOTPData sendOTPData);

    @PUT(ApiUrls.VERIFY_OTP)
    Call<JsonObject> verifyOTP(@Body VerifyOTPData verifyOTPData);

    @GET(ApiUrls.APP_ADMINISTRATOR_LIST)
    Call<JsonObject> getAppAdministratorList(@Query("app_name") String app_name);


    // Insert GPS Tracking
    @POST(ApiUrls.INSERT_GPS_TRACKING)
    Call<JsonObject> insertGpsTracking(@Body SendGpsDataBody sendGpsDataBody);


    // Current Tracking Status
    @GET(ApiUrls.CURRENT_TRACKING_STATUS)
    Call<JsonObject> getCurrentStatus(@Query("country_code") String country_code,
                                      @Query("customer_id") String customer_id,
                                      @Query("user_id") String user_id);

    // Past Tracking Data
    @GET(ApiUrls.PAST_TRACKING_DATA)
    Call<JsonObject> getPastTrackingData(@Query("country_code") String country_code,
                                         @Query("customer_id") String customer_id,
                                         @Query("user_id") String user_id);


    @POST(ApiUrls.STOP_LIVE_COORDINATES)
    Call<JsonObject> stopLiveCoordinates(@Body StopLiveCoordinatesData stopLiveCoordinatesData);
}
