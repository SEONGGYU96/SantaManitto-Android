package org.sopt.santamanitto.room.network

import org.sopt.santamanitto.room.create.network.CreateRoomModel
import org.sopt.santamanitto.room.create.network.CreateRoomRequestModel
import org.sopt.santamanitto.room.create.network.ModifyRoomRequestModel
import org.sopt.santamanitto.room.data.MyManittoModel
import org.sopt.santamanitto.room.data.PersonalRoomModel
import org.sopt.santamanitto.room.join.network.JoinRoomRequestModel
import org.sopt.santamanitto.room.join.network.JoinRoomResponseModel
import org.sopt.santamanitto.room.manittoroom.network.ManittoRoomModel

interface RoomRequest {

    interface CreateRoomCallback {
        fun onRoomCreated(createdRoom: CreateRoomModel)

        fun onFailed()
    }

    interface JoinRoomCallback {
        fun onSuccessJoinRoom(joinedRoom: JoinRoomResponseModel)

        fun onFailed(joinRoomError: JoinRoomError)
    }

    interface GetManittoRoomCallback {
        fun onLoadManittoRoomData(manittoRoom: ManittoRoomModel)

        fun onFailed()
    }

    interface GetPersonalRoomInfoCallback {
        fun onLoadPersonalRoomInfo(personalRoom: PersonalRoomModel)

        fun onDataNotAvailable()
    }

    enum class JoinRoomError {
        WrongInvitationCode, AlreadyMatched, AlreadyEntered, Els
    }

    suspend fun getRooms(): List<MyManittoModel>

    fun createRoom(request: CreateRoomRequestModel, callback: CreateRoomCallback)

    fun modifyRoom(
        roomId: String,
        request: ModifyRoomRequestModel,
        callback: (onSuccess: Boolean) -> Unit
    )

    fun joinRoom(request: JoinRoomRequestModel, callback: JoinRoomCallback)

    fun getManittoRoomData(roomId: String, callback: GetManittoRoomCallback)

    fun matchManitto(roomId: String, callback: (onSuccess: Boolean) -> Unit)

    fun getPersonalRoomInfo(roomId: String, callback: GetPersonalRoomInfoCallback)

    fun exitRoom(roomId: String, callback: (onSuccess: Boolean) -> Unit)

    suspend fun deleteRoom(roomId: String): Result<Unit>

}