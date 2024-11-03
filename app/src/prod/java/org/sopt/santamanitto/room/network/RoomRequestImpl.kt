package org.sopt.santamanitto.room.network

import org.sopt.santamanitto.network.RequestCallback
import org.sopt.santamanitto.network.Response
import org.sopt.santamanitto.network.start
import org.sopt.santamanitto.room.create.network.CreateRoomModel
import org.sopt.santamanitto.room.create.network.CreateRoomRequestModel
import org.sopt.santamanitto.room.create.network.ModifyRoomRequestModel
import org.sopt.santamanitto.room.data.TempMyManittoModel
import org.sopt.santamanitto.room.data.TempPersonalRoomModel
import org.sopt.santamanitto.room.join.network.JoinRoomRequestModel
import org.sopt.santamanitto.room.join.network.JoinRoomResponseModel
import org.sopt.santamanitto.room.manittoroom.network.ManittoRoomModel
import retrofit2.Call
import retrofit2.Callback

class RoomRequestImpl(
    private val roomService: RoomService,
) : RoomRequest {

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
        roomService.joinRoom(request).enqueue(object : Callback<Response<JoinRoomResponseModel>> {
            override fun onResponse(
                call: Call<Response<JoinRoomResponseModel>>,
                response: retrofit2.Response<Response<JoinRoomResponseModel>>
            ) {
                if (response.isSuccessful && response.body()?.statusCode == 201) {
                    callback.onSuccessJoinRoom(response.body()?.data!!)
                } else {
                    val error = when (response.code()) {
                        403 -> RoomRequest.JoinRoomError.AlreadyMatched
                        404 -> RoomRequest.JoinRoomError.WrongInvitationCode
                        409 -> RoomRequest.JoinRoomError.AlreadyEntered
                        else -> {
                            RoomRequest.JoinRoomError.Els
                        }
                    }
                    callback.onFailed(error)
                }
            }

            override fun onFailure(call: Call<Response<JoinRoomResponseModel>>, t: Throwable) {
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
        roomService.exitRoom(roomId).start(callback)
    }

    override fun removeHistory(roomId: String, callback: (onSuccess: Boolean) -> Unit) {
        roomService.removeHistory(roomId).start(callback)
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> {
        val response = roomService.deleteRoom(roomId)

        return if (response.statusCode == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to delete room: ${response.message}"))
        }
    }
}