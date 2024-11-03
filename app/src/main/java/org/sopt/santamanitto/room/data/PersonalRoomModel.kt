package org.sopt.santamanitto.room.data

import com.google.gson.annotations.SerializedName
import org.sopt.santamanitto.room.data.MyManittoModel.Member.Manitto
import org.sopt.santamanitto.room.data.MyManittoModel.Mission

data class PersonalRoomModel(
    @SerializedName("manitto") val manitto: Manitto,
    @SerializedName("mission") val mission: Mission
)
