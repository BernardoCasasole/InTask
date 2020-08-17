package com.example.chatapp.fragments;

import com.example.chatapp.notifications.MyResponse;
import com.example.chatapp.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
                "Content-Type:application/json",
                "Authorization:key=AAAAyaQfz2Y:APA91bEX_YNER7T8FKwLdVeXicUhM_fJLlLMTFyg4EzxkmTQflqJ5XZBuBr3M4Nz55SD0ulp1zrHYsqwAdTUOlKvuBP0k-nh0S1Qn5g8aiaLWCErmCZmO8Siw6xGk0adHvhVszZYHYrD"

    })

    @POST("fcm/send")

    Call<MyResponse> sendNotification(@Body Sender body);

}
