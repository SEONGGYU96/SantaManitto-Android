package org.sopt.santamanitto.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

data class SimpleResponse(
    val statusCode: Int,
    val message: String,
    val data: String
)

fun Call<SimpleResponse>.start(callback: (Boolean) -> Unit) {
    val TAG = javaClass.simpleName + " response"
    enqueue(object : Callback<SimpleResponse> {
        override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
            if (response.isSuccessful) {
                callback.invoke(true)
            } else {
                Timber.tag(TAG).e("response is not successful. ${response.message()}")
                callback.invoke(false)
            }
        }

        override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
            Timber.tag(TAG).e("request is fail. ${t.message}")
            callback.invoke(false)
        }
    })
}