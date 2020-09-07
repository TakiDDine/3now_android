package de.threenow.Helper;

public class URLHelper {

    public static final String base = "https://www.3now-test.com/";
//    public static final String base = "https://www.3now.de/";
    public static final String REDIRECT_URL = base;
    public static final String REDIRECT_SHARE_URL = "http://maps.google.com/maps?q=loc:";
    public static final int client_id = 2;

    public static final String client_secret = "WifS1rMi3LvuorP1G2UdtKZairUNSH2iMqrKivPf";
    public static final String STRIPE_TOKEN = "pk_live_gGgW4OJ2ulRDPaIeKBypkJEG00DvqnE8Ml"; // pk_test_69WkPQ5i5k0KwuG08mEazMPO


    public static final String image_url_signature = base + "public/";
    public static final String CURRENT_TRIP = base + "api/user/trips/current";
    String GET_RATE = "api/user/check/rate/provider";
    public static final String login = base + "oauth/token";
    public static final String register = base + "api/user/signup";
    public static final String UserProfile = base + "api/user/details";
    public static final String UseProfileUpdate = base + "api/user/update/profile";

    public static final String GET_SERVICE_LIST_API = base + "api/user/services";
    public static final String REQUEST_STATUS_CHECK_API = base + "api/user/request/check";
    public static final String ESTIMATED_FARE_DETAILS_API = base + "api/user/estimated/fare";
    public static final String ESTIMATED_FARE_ALL_API = base + "api/user/estimated/fare/all";

    public static final String SEND_REQUEST_API = base + "api/user/send/request"; // للرحلات الفورية
    public static final String SEND_REQUEST_API_SCHEDULE = base + "api/user/save/request/schedule"; // للرحلات المجدولة

    // new
    public static final String SEND_REQUEST_API_NEW = base + "api/new/send/request";
    public static final String SEND_REQUEST_Later_API = base + "api/user/save/later";// للرحلات المجدولة

    public static final String PAY_REQUEST_Later_API = base + "api/user/request/paied"; // دفع للرحلة الفورية
    public static final String PAY_REQUEST_schedule_API = base + "api/user/send/payement/schedule"; // دفع للرحلة المجدولة

    //new
    public static final String SEND_EMAIL_API_NEW = base + "api/user/send/email";

    public static final String VALIDZONE = base + "api/user/getvalidzone";
    public static final String CANCEL_REQUEST_API = base + "api/user/cancel/request";
    public static final String PAY_NOW_API = base + "api/user/payment";
    // new
    public static final String PAY_NOW_SCHEDUL_API = base + "api/user/payment/price";
    public static final String RATE_PROVIDER_API = base + "api/user/rate/provider";
    public static final String GET_USERREVIEW = base + "api/user/review";

    public static final String DELETE_CARD_FROM_ACCOUNT_API = base + "api/user/card/destory";
    public static final String GET_HISTORY_API = base + "api/user/trips";
    public static final String GET_HISTORY_DETAILS_API = base + "api/user/trip/details";
    public static final String addCardUrl = base + "api/user/add/money";
    public static final String COUPON_LIST_API = base + "api/user/promocodes";
    public static final String ADD_COUPON_API = base + "api/user/promocode/add";
    public static final String CHANGE_PASSWORD_API = base + "api/user/change/password";
    public static final String UPCOMING_TRIP_DETAILS = base + "api/user/upcoming/trip/details";
    public static final String UPCOMING_TRIPS = base + "api/user/upcoming/trips";
    public static final String GET_PROVIDERS_LIST_API = base + "api/user/show/providers";
    public static final String FORGET_PASSWORD = base + "api/user/forgot/password";
    public static final String RESET_PASSWORD = base + "api/user/reset/password";

    public static final String FACEBOOK_LOGIN = base + "api/user/auth/facebook";
    public static final String GOOGLE_LOGIN = base + "api/user/auth/google";
    public static final String LOGOUT = base + "api/user/logout";
    public static final String HELP = base + "api/user/help";
    public static final String COMPLAINT = base + "api/user/createComplaint";
    public static final String SAVE_LOCATION = base + "api/user/createDefaultLocation";

    //    Safaricom Payment
    public static final String PAYMENT_TOKEN = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    public static final String ACCOUNT_BALENCE = "https://sandbox.safaricom.co.ke/mpesa/accountbalance/v1/query";
    public static final String PAYMENT = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";


    public static final String ADD_CARD_TO_ACCOUNT_API = base + "api/user/card";
    public static final String CARD_PAYMENT_LIST = base + "api/user/card";
    public static final String GET_PAYMENT_CONFIRMATION = "api/user/payment/now?total_amount=";
    public static final String GET_DEFAULT_CARD = base + "api/user/makeCard_default?card_id=";

    public static final String ChatGetMessage = base + "api/user/firebase/getChat?request_id=";
    public static final String GET_USER_CHAT_LIST = base + "api/user/firebase/chatHistory";
    public static final String NOTIFICATION_URL = base + "api/user/notification";

    // Coupon
    public static final String COUPON_VERIFY = base + "api/user/coupon/verfiy";
    public static final String get_current_Prodiver_Location_calc_distance = base + "/api/user/currentProdiverLocation";
    public static final String COMPLETE_AS_CASH = base + "change/payment/cash";
    public static int count_call_Geocoder = 0;


}
