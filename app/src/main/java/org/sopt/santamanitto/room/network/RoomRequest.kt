package org.sopt.santamanitto.room.network

import org.sopt.santamanitto.room.create.network.CreateRoomModel
import org.sopt.santamanitto.room.create.network.CreateRoomRequestModel
import org.sopt.santamanitto.room.create.network.ModifyRoomRequestModel
import org.sopt.santamanitto.room.data.TempMyManittoModel
import org.sopt.santamanitto.room.data.TempPersonalRoomModel
import org.sopt.santamanitto.room.join.network.JoinRoomModel
import org.sopt.santamanitto.room.join.network.JoinRoomRequestModel
import org.sopt.santamanitto.room.manittoroom.network.ManittoRoomModel
import org.sopt.santamanitto.room.manittoroom.network.MatchedMissionsModel

interface RoomRequest {

    interface CreateRoomCallback {
        fun onRoomCreated(createdRoom: CreateRoomModel)

        fun onFailed()
    }

    interface JoinRoomCallback {
        fun onSuccessJoinRoom(joinedRoom: JoinRoomModel)

        fun onFailed(joinRoomError: JoinRoomError)
    }

    interface GetManittoRoomCallback {
        fun onLoadManittoRoomData(manittoRoom: ManittoRoomModel)

        fun onFailed()
    }

    interface MatchManittoCallback {
        fun onSuccessMatching(missions: List<MatchedMissionsModel>)

        fun onFailed()
    }

    interface GetPersonalRoomInfoCallback {
        fun onLoadPersonalRoomInfo(personalRoom: TempPersonalRoomModel)

        fun onDataNotAvailable()
    }

    enum class JoinRoomError {
        WrongInvitationCode, DuplicatedMember, AlreadyMatched, AlreadyEntered, Els
    }

    suspend fun getRooms(): List<TempMyManittoModel>

    fun createRoom(request: CreateRoomRequestModel, callback: CreateRoomCallback)

    fun modifyRoom(
        roomId: String,
        request: ModifyRoomRequestModel,
        callback: (onSuccess: Boolean) -> Unit
    )

    fun joinRoom(request: JoinRoomRequestModel, callback: JoinRoomCallback)

    fun getManittoRoomData(roomId: String, callback: GetManittoRoomCallback)

    fun matchManitto(roomId: String, callback: MatchManittoCallback)

    fun getPersonalRoomInfo(roomId: String, callback: GetPersonalRoomInfoCallback)

    fun exitRoom(roomId: String, callback: (onSuccess: Boolean) -> Unit)

    fun removeHistory(roomId: String, callback: (onSuccess: Boolean) -> Unit)

}