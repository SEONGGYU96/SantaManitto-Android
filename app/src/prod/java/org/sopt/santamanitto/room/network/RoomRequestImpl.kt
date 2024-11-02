package org.sopt.santamanitto.room.network

import okhttp3.ResponseBody
import org.sopt.santamanitto.network.AuthRetrofitClient
import org.sopt.santamanitto.network.RequestCallback
import org.sopt.santamanitto.network.Response
import org.sopt.santamanitto.network.start
import org.sopt.santamanitto.room.create.network.CreateRoomModel
import org.sopt.santamanitto.room.create.network.CreateRoomRequestModel
import org.sopt.santamanitto.room.create.network.ModifyRoomRequestModel
import org.sopt.santamanitto.room.data.TempMyManittoModel
import org.sopt.santamanitto.room.data.TempPersonalRoomModel
import org.sopt.santamanitto.room.join.network.JoinRoomErrorModel
import org.sopt.santamanitto.room.join.network.JoinRoomModel
import org.sopt.santamanitto.room.join.network.JoinRoomRequestModel
import org.sopt.santamanitto.room.manittoroom.network.ManittoRoomModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit

class RoomRequestImpl(
    private val roomService: RoomService,
    @AuthRetrofitClient private val retrofitClient: Retrofit
) : RoomRequest {

    companion object {
        private const val JOIN_ROOM_ERROR_ALREADY_MATCHED = "이미 매칭이 완료된 방입니다"
        private const val JOIN_ROOM_ERROR_DUPLICATED_MEMBER = "이미 있는 멤버입니다"
        private const val JOIN_ROOM_ERROR_WRONG_INVITATION_CODE = "초대코드가 잘못되었습니다"
        private const val JOIN_ROOM_ERROR_ALREADY_ENTERED = "이미 입장했던 방입니다"
    }

    override suspend fun getRooms(): List<TempMyManittoModel> {
        val response = roomService.getRooms()

        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception("Failed to get rooms: ${response.message}")
        }
    }

    override fun createRoom(
        request: CreateRoomRequestModel,
        callback: RoomRequest.CreateRoomCallback
    ) {
        roomService.createRoom(request).start(object : RequestCallback<CreateRoomModel> {
            override fun onSuccess(data: CreateRoomModel) {
                callback.onRoomCreated(data)
            }

            override fun onFail() {
                callback.onFailed()
            }
        })
    }

    override fun modifyRoom(
        roomId: String,
        request: ModifyRoomRequestModel,
        callback: (onSuccess: Boolean) -> Unit
    ) {
        roomService.modifyRoom(roomId, request).start(callback)
    }

    override fun joinRoom(request: JoinRoomRequestModel, callback: RoomRequest.JoinRoomCallback) {
        roomService.joinRoom(request).enqueue(object : Callback<Response<JoinRoomModel>> {
            override fun onResponse(
                call: Call<Response<JoinRoomModel>>,
                response: retrofit2.Response<Response<JoinRoomModel>>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccessJoinRoom(response.body()!!.data)
                } else {
                    val error = when (convert(response.errorBody()!!).message) {
                        JOIN_ROOM_ERROR_ALREADY_MATCHED -> RoomRequest.JoinRoomError.AlreadyMatched
                        JOIN_ROOM_ERROR_DUPLICATED_MEMBER -> RoomRequest.JoinRoomError.DuplicatedMember
                        JOIN_ROOM_ERROR_WRONG_INVITATION_CODE -> RoomRequest.JoinRoomError.WrongInvitationCode
                        JOIN_ROOM_ERROR_ALREADY_ENTERED -> RoomRequest.JoinRoomError.AlreadyEntered
                        else -> {
                            RoomRequest.JoinRoomError.Els
                        }
                    }
                    callback.onFailed(error)
                }
            }

            override fun onFailure(call: Call<Response<JoinRoomModel>>, t: Throwable) {
                callback.onFailed(RoomRequest.JoinRoomError.Els)
            }
        })
    }

    override fun getManittoRoomData(roomId: String, callback: RoomRequest.GetManittoRoomCallback) {
        roomService.getManittoRoomData(roomId).start(object : RequestCallback<ManittoRoomModel> {
            override fun onSuccess(data: ManittoRoomModel) {
                callback.onLoadManittoRoomData(data)
            }

            override fun onFail() {
                callback.onFailed()
            }
        })
    }

    override fun matchManitto(roomId: String, callback: (onSuccess: Boolean) -> Unit) {
        roomService.matchManitto(roomId).start(callback)
    }

    override fun getPersonalRoomInfo(
        roomId: String,
        callback: RoomRequest.GetPersonalRoomInfoCallback
    ) {
        roomService.getRoomPersonalInfo(roomId)
            .start(object : RequestCallback<TempPersonalRoomModel> {
                override fun onSuccess(data: TempPersonalRoomModel) {
                    callback.onLoadPersonalRoomInfo(data)
                }

                override fun onFail() {
                    callback.onDataNotAvailable()
                }
            })
    }

    override fun exitRoom(roomId: String, callback: (onSuccess: Boolean) -> Unit) {
        roomService.exitRoom(ExitRoomRequestModel(roomId)).start(callback)
    }

    override fun removeHistory(roomId: String, callback: (onSuccess: Boolean) -> Unit) {
        roomService.removeHistory(roomId).start(callback)
    }

    fun convert(errorBody: ResponseBody): JoinRoomErrorModel {
        return retrofitClient.responseBodyConverter<JoinRoomErrorModel>(
            JoinRoomErrorModel::class.java,
            JoinRoomErrorModel::class.java.annotations
        ).convert(errorBody)!!
    }
}