package org.sopt.santamanitto.user.network

import org.sopt.santamanitto.network.Response
import org.sopt.santamanitto.user.data.UserInfoModel
import org.sopt.santamanitto.user.mypage.UserNameModel
import org.sopt.santamanitto.user.mypage.UserNameRequestModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserAuthService {
    @GET("users/{userId}")
    fun getUserInfo(
        @Path("userId") userId: String
    ): Call<Response<UserInfoModel>>

    @PUT("users/{userId}")
    fun changeUserName(
        @Path("userId") userId: String,
        @Body request: UserNameRequestModel
    ): Call<Response<UserNameModel>>
}