package org.sopt.santamanitto.user.data.controller

import org.sopt.santamanitto.network.RequestCallback
import org.sopt.santamanitto.network.start
import org.sopt.santamanitto.user.data.UserInfoModel
import org.sopt.santamanitto.user.mypage.UserNameRequestModel
import org.sopt.santamanitto.user.network.UserAuthService
import timber.log.Timber

class RetrofitUserAuthController(private val userAuthService: UserAuthService) :
    UserAuthController {
    override suspend fun changeUserName(
        newName: String,
    ): Result<Boolean> {
        return try {
            val response = userAuthService.changeUserName(UserNameRequestModel(newName))
            if (response.statusCode == 200) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to change user name: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserInfo(
        userId: String,
        callback: UserAuthController.GetUserInfoCallback,
    ) {
        userAuthService.getUserInfo(userId).start(
            object : RequestCallback<UserInfoModel> {
                override fun onSuccess(data: UserInfoModel) {
                    callback.onUserInfoLoaded(data)
                }

                override fun onFail() {
                    callback.onDataNotAvailable()
                }
            },
        )
    }

    override suspend fun withdraw(): Result<Unit> {
        return try {
            val response = userAuthService.withdraw()
            if (response.statusCode == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to withdraw: ${response.message}"))
            }
        } catch (e: Exception) {
            Timber.e("${e.message}")
            Result.failure(e)
        }
    }
}
