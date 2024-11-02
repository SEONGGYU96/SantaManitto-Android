package org.sopt.santamanitto.room.data


import com.google.gson.annotations.SerializedName
import org.sopt.santamanitto.main.list.RoomState
import org.sopt.santamanitto.util.TimeUtil

data class TempMyManittoModel(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("Creator")
    val creator: Creator,
    @SerializedName("deletedByCreatorDate")
    val deletedByCreatorDate: String?,
    @SerializedName("expirationDate")
    val expirationDate: String?,
    @SerializedName("id")
    val roomId: String,
    @SerializedName("invitationCode")
    val invitationCode: String,
    @SerializedName("matchingDate")
    val matchingDate: String?,
    @SerializedName("Members")
    val members: List<Member>,
    @SerializedName("Missions")
    val missions: List<Mission>,
    @SerializedName("roomName")
    val roomName: String
) {
    data class Creator(
        @SerializedName("id")
        val id: String,
        @SerializedName("manittoUserId")
        val manittoUserId: String?,
        @SerializedName("username")
        val username: String
    )

    data class Member(
        @SerializedName("manitto")
        val manitto: Manitto?,
        @SerializedName("santa")
        val santa: Santa
    ) {
        data class Manitto(
            @SerializedName("id")
            val id: String?,
            @SerializedName("username")
            val username: String?
        )

        data class Santa(
            @SerializedName("id")
            val id: String?,
            @SerializedName("username")
            val username: String?
        )
    }

    data class Mission(
        @SerializedName("content")
        val content: String?,
        @SerializedName("id")
        val id: String?
    )
}

fun TempMyManittoModel.getRoomState(): RoomState {
    return when {
        // 삭제된 방
        deletedByCreatorDate != null -> RoomState.DELETED
        // 진행중인 방 (매칭됨 && 만료되지 않음)
        matchingDate != null && expirationDate != null &&
                !TimeUtil.isExpired(expirationDate) -> RoomState.IN_PROGRESS
        // 대기중인 방 (매칭 안됨 && 만료되지 않음)
        matchingDate == null && expirationDate != null &&
                !TimeUtil.isExpired(expirationDate) -> RoomState.WAITING
        // 종료된 방 (매칭됨 && 만료됨)
        matchingDate != null && expirationDate != null &&
                TimeUtil.isExpired(expirationDate) -> RoomState.FINISHED
        // 만료된 방 (매칭 안됨 && 만료됨)
        matchingDate == null && expirationDate != null &&
                TimeUtil.isExpired(expirationDate) -> RoomState.EXPIRED

        else -> RoomState.LEFT
    }
}