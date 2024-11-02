package org.sopt.santamanitto.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.sopt.santamanitto.NetworkViewModel
import org.sopt.santamanitto.room.data.TempMyManittoModel
import org.sopt.santamanitto.room.network.RoomRequest
import org.sopt.santamanitto.util.RoomSortUtil
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val roomRequest: RoomRequest
) : NetworkViewModel() {

    private var _myManittoModelList = MutableSharedFlow<List<TempMyManittoModel>>(replay = 1)
    val myManittoModelList: SharedFlow<List<TempMyManittoModel>> = _myManittoModelList

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun fetchMyManittoList() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _isLoading.value = true
            try {
                val rooms = roomRequest.getRooms()
                _myManittoModelList.emit(RoomSortUtil.getSortedRooms(rooms))
            } catch (e: Exception) {
                _networkErrorOccur.value = true
            } finally {
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

    fun refresh() {
        fetchMyManittoList()
    }
}