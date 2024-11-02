package org.sopt.santamanitto.user.data.controller

import org.sopt.santamanitto.user.data.UserInfoModel

interface UserAuthController {

    interface GetUserInfoCallback {
        fun onUserInfoLoaded(userInfoModel: UserInfoModel)

        fun onDataNotAvailable()
    }

    suspend fun changeUserName(newName: String): Result<Boolean>

    fun getUserInfo(userId: String, callback: GetUserInfoCallback)

    suspend fun withdraw(): Result<Unit>
}