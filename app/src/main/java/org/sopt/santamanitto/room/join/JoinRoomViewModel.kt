package org.sopt.santamanitto.room.join

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.sopt.santamanitto.NetworkViewModel
import org.sopt.santamanitto.room.join.network.JoinRoomModel
import org.sopt.santamanitto.room.join.network.JoinRoomRequestModel
import org.sopt.santamanitto.room.network.RoomRequest
import org.sopt.santamanitto.room.network.RoomRequest.JoinRoomError
import javax.inject.Inject

@HiltViewModel
class JoinRoomViewModel @Inject constructor(
    private val roomRequest: RoomRequest
) : NetworkViewModel() {

    val invitationCode = MutableLiveData<String>(null)

    val isInvitationCodeEmpty = invitationCode.map {
        it.isNullOrEmpty()
    }

    private val _isWrongInvitationCode = MutableLiveData(false)
    val isWrongInvitationCode: LiveData<Boolean>
        get() = _isWrongInvitationCode

    private val _isAlreadyMatchedRoom = MutableLiveData(false)
    val isAlreadyMatchedRoom: LiveData<Boolean>
        get() = _isAlreadyMatchedRoom

    private val _isAlreadyEnteredRoom = MutableLiveData(false)
    val isAlreadyEnteredRoom: LiveData<Boolean>
        get() = _isAlreadyEnteredRoom

    fun joinRoom(callback: (JoinRoomModel) -> Unit) {
        roomRequest.joinRoom(JoinRoomRequestModel(invitationCode.value!!),
            object : RoomRequest.JoinRoomCallback {

                override fun onSuccessJoinRoom(joinedRoom: JoinRoomModel) {
                    callback(joinedRoom)
                }

                override fun onFailed(joinRoomError: JoinRoomError) {
                    when (joinRoomError) {
                        JoinRoomError.WrongInvitationCode -> _isWrongInvitationCode.value = true
                        JoinRoomError.AlreadyMatched -> _isAlreadyMatchedRoom.value = true
                        JoinRoomError.AlreadyEntered -> _isAlreadyEnteredRoom.value = true
                        else -> _networkErrorOccur.value = true
                    }
                }
            })
    }
}