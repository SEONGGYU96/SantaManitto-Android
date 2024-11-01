package org.sopt.santamanitto.room.manittoroom.network

import com.google.gson.annotations.SerializedName

data class ManittoRoomMember(
    val santa: SantaRoomInfo,
    val manitto: ManittoRoomInfo
) {
    data class SantaRoomInfo(
        @SerializedName("id") val userId: String,
        @SerializedName("username") val userName: String,
        @SerializedName("missionId") val missionId: String,
    )

    data class ManittoRoomInfo(
        @SerializedName("id") val userId: String,
        @SerializedName("username") val userName: String,
    )
}
