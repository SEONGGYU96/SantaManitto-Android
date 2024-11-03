package org.sopt.santamanitto.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.sopt.santamanitto.NetworkViewModel
import org.sopt.santamanitto.room.data.MyManittoModel
import org.sopt.santamanitto.room.network.RoomRequest
import org.sopt.santamanitto.util.RoomSortUtil
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val roomRequest: RoomRequest
) : NetworkViewModel() {

    private var _myManittoModelList = MutableSharedFlow<List<MyManittoModel>>(replay = 1)
    val myManittoModelList: SharedFlow<List<MyManittoModel>> = _myManittoModelList

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun fetchMyManittoList() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _isLoading.value = true
            try {
                val rooms = roomRequest.getRooms()
                Timber.e("rooms: $rooms")
                _myManittoModelList.emit(RoomSortUtil.getSortedRooms(rooms))
            } catch (e: Exception) {
                Timber.e(e)
                _networkErrorOccur.value = true
            } finally {
                Timber.e("finally")
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }

    fun exitRoom(roomId: String) {
        roomRequest.exitRoom(roomId) {
            if (it) {
                refresh()
            } else {
                _networkErrorOccur.value = true
            }
        }
    }

    fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            roomRequest.deleteRoom(roomId).fold(
                onSuccess = {
                    refresh()
                },
                onFailure = {
                    _networkErrorOccur.value = true
                }
            )
        }
    }

    fun refresh() {
        Timber.e("refresh1")
        fetchMyManittoList()
        Timber.e("refresh2")
    }
}