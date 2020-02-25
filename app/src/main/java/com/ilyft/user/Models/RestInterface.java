package com.ilyft.user.Models;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RestInterface {

    @POST("mpesa/stkpush/v1/processrequest")
    Call<PaymentResponse> createSocialLogin(@Header("X-Requested-With") String requestWith,
                                            @Header("Content-Type") String contentType,
                                            @Header("Authorization") String Authorization,
                                            @Body PaymentRequest paymentRequest);

    @GET("api/user/check/rate/provider")
    Call<GetUserRate> getUserRate(@Header("X-Requested-With") String requestWith,
                                  @Header("Authorization") String Authorization);

    @POST("api/user/rate/provider")
    Call<ResponseBody> postUserRate(@Header("X-Requested-With") String requestWith,
                                    @Header("Authorization") String Authorization,
                                    @Body PostUserRate postUserRate);

}
