package org.sopt.santamanitto.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.sopt.santamanitto.NetworkViewModel
import org.sopt.santamanitto.room.data.JoinedRoom
import org.sopt.santamanitto.user.data.source.UserDataSource
import javax.inject.Named

class MainViewModel @ViewModelInject constructor(
    @Named("cached") private val userCachedDataSource: UserDataSource
) : NetworkViewModel() {

    private var _joinedRooms = MutableLiveData<List<JoinedRoom>?>(null)
    val joinedRooms: LiveData<List<JoinedRoom>?>
        get() = _joinedRooms

    fun getJoinedRooms() {
        userCachedDataSource.getJoinedRoom(
            userCachedDataSource.getUserId(),
            object : UserDataSource.GetJoinedRoomsCallback {
                override fun onJoinedRoomsLoaded(rooms: List<JoinedRoom>) {
                    _isLoading.value = false
                    _joinedRooms.value = rooms
                }

                override fun onDataNotAvailable() {
                    _networkErrorOccur.value = true
                }
            })
    }
}