package org.sopt.santamanitto.main

import android.view.ViewGroup
import org.sopt.santamanitto.room.data.JoinedRoom
import org.sopt.santamanitto.recyclerview.BaseAdapter
import org.sopt.santamanitto.recyclerview.BaseViewHolder
import org.sopt.santamanitto.room.data.source.RoomDataSource
import org.sopt.santamanitto.user.data.source.UserDataSource

class JoinedRoomsAdapter(
    private val cachedUserDataSource: UserDataSource,
    private val cachedRoomDataSource: RoomDataSource
) : BaseAdapter<JoinedRoom>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<JoinedRoom, *> {
        return JoinedRoomViewHolder(parent, cachedUserDataSource, cachedRoomDataSource)
    }
}