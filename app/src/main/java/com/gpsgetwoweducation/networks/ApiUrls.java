package
        com.gpsgetwoweducation.networks;

public class ApiUrls {
    //Registered Customer URLS
    public static final String BASE_URL_REGISTERED_CUSTOMER = "https://exploreapi.getwow.education/api/";
    public static final String REGISTERED_BUSINESS_CUSTOMER = "explore-getwow-education-app/launch/get-registered-institution";

    //Registered Customer Mobile Number URLS
    public static final String BASE_URL_REGISTERED_CUSTOMER_MOBILE_NUMBER = "https://launchappapi.getwow.education/api/";
    public static final String REGISTERED_CUSTOMER_MOBILE_NUMBER = "wow-launch-app/user-login/login-with-registered-mobile-number";

    // User Login URLS
    public static final String BASE_URL_USER_LOGIN = "https://launchappapi.getwow.education/api/";
    public static final String USER_LOGIN = "wow-launch-app/user-login/login-with-user-password";

    // Send OTP
    public static final String SEND_OTP = "wow-launch-app/otp-verification/otp-receive";
    // Verification OTP
    public static final String VERIFY_OTP = "wow-launch-app/otp-verification/otp-verify";

    public static final String APP_ADMINISTRATOR_LIST = "wow-launch-app/user-login/get-app-administrator-list";

    // Insert GPS Tracking URLS

    public static final String BASE_URL_INSERT_GPS_TRACKING = "https://u27api.getwow.education/api/";

    public static final String INSERT_GPS_TRACKING = "insert-gps-tracking-master-data";

    // Current Tracking Status
    public static final String CURRENT_TRACKING_STATUS = "get-current-trips";

    // Past Tracking Data
    public static final String PAST_TRACKING_DATA = "get-history-trips";

    // Stop Live Coordinates
    public static final String STOP_LIVE_COORDINATES = "stop-live-coordinates";
}