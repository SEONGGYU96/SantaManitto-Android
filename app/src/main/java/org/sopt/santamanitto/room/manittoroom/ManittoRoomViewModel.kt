package org.sopt.santamanitto.room.manittoroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.sopt.santamanitto.NetworkViewModel
import org.sopt.santamanitto.room.data.PersonalRoomModel
import org.sopt.santamanitto.room.manittoroom.network.ManittoRoomMember
import org.sopt.santamanitto.room.manittoroom.network.ManittoRoomModel
import org.sopt.santamanitto.room.network.RoomRequest
import org.sopt.santamanitto.user.data.source.UserMetadataSource
import org.sopt.santamanitto.util.TimeUtil
import javax.inject.Inject

@HiltViewModel
class ManittoRoomViewModel @Inject constructor(
    private val userMetadataSource: UserMetadataSource,
    private val roomRequest: RoomRequest
) : NetworkViewModel() {

    private var _roomId = ""
    var roomId: String
        get() = _roomId
        set(value) {
            _roomId = value
        }

    var isMatched = false
    var isFinished = false

    private var _invitationCode = ""
    val invitationCode: String
        get() = _invitationCode

    private val _roomName = MutableLiveData<String>(null)
    val roomName: LiveData<String>
        get() = _roomName

    private val _period = MutableLiveData<Int>()
    val period: LiveData<Int>
        get() = _period

    private val _isExpired = MutableLiveData(false)
    val isExpired: LiveData<Boolean>
        get() = _isExpired

    private val _expiration = MutableLiveData<String>(null)
    val expiration: LiveData<String>
        get() = _expiration

    private val _members = MutableLiveData<List<ManittoRoomMember>>()
    val members: LiveData<List<ManittoRoomMember>>
        get() = _members

    private val _isAdmin = MutableLiveData(false)
    val isAdmin: LiveData<Boolean>
        get() = _isAdmin

    private val _myManittoName = MutableLiveData("")
    val myManittoName: LiveData<String>
        get() = _myManittoName

    private val _mySantaName = MutableLiveData("")
    val mySantaName: LiveData<String>
        get() = _mySantaName

    private val _myMission = MutableLiveData<String?>("")
    val myMission: LiveData<String?>
        get() = _myMission

    private val _missionToMe = MutableLiveData("")
    val missionToMe: LiveData<String?>
        get() = _missionToMe

    val myName: String
        get() = userMetadataSource.getUserName()

    private val _canStart = MutableLiveData<Boolean>()
    val canStart: LiveData<Boolean>
        get() = _canStart

    fun refreshManittoRoomInfo() {
        startLoading()
        roomRequest.getManittoRoomData(roomId, object : RoomRequest.GetManittoRoomCallback {
            override fun onLoadManittoRoomData(manittoRoom: ManittoRoomModel) {
                manittoRoom.run {
                    _roomName.value = roomName
                    _expiration.value = expirationDate
                    _isExpired.value = TimeUtil.getDayDiffFromNow(expirationDate) < 0
                    _members.value = members
                    _invitationCode = invitationCode
                    _isAdmin.value = userMetadataSource.getUserId() == creator.userId
                    _canStart.value = _isAdmin.value!! && members.size > 1
                    this@ManittoRoomViewModel.isMatched = isMatched
                    _period.value = getPeriod(createdAt, expirationDate)
                    if (!manittoRoom.matchingDate.isNullOrBlank()) {
                        _missionToMe.value =
                            findMissionContentByUserId(userMetadataSource.getUserId(), this)
                        _mySantaName.value =
                            findManittoUserNameByUserId(userMetadataSource.getUserId(), this)
                    }
                    stopLoading()
                }
            }

            override fun onFailed() {
                _networkErrorOccur.value = true
            }
        })
    }

    fun findMissionContentByUserId(userId: String, manittoRoomModel: ManittoRoomModel): String? {
        val matchingMember = manittoRoomModel.members.find { it.manitto.userId == userId }
        val santaMissionId = matchingMember?.santa?.missionId ?: return null
        return manittoRoomModel.missions.find { it.missionId == santaMissionId }?.content
    }

    fun findManittoUserNameByUserId(userId: String, manittoRoomModel: ManittoRoomModel): String? {
        val matchingMember = manittoRoomModel.members.find { it.santa.userId == userId }
        return matchingMember?.manitto?.userName
    }

    fun match() {
        startLoading()
        roomRequest.matchManitto(roomId) { isSuccess ->
            if (isSuccess) {
                isMatched = true
                stopLoading()
            } else {
                _networkErrorOccur.value = true
            }
        }
    }

    fun getPersonalRelationInfo() {
        startLoading()
        roomRequest.getPersonalRoomInfo(roomId, object : RoomRequest.GetPersonalRoomInfoCallback {
            override fun onLoadPersonalRoomInfo(personalRoom: PersonalRoomModel) {
                _myManittoName.value = personalRoom.manitto.username
                _myMission.value = personalRoom.mission.content
                stopLoading()
            }

            override fun onDataNotAvailable() {
                _networkErrorOccur.value = true
            }
        })
    }

    fun removeHistory(callback: () -> Unit) {
        roomRequest.removeHistory(roomId) { isRemoved ->
            if (isRemoved) {
                callback.invoke()
            } else {
                _networkErrorOccur.value = true
            }
        }
    }

    private fun getPeriod(createdAt: String, expiration: String): Int =
        TimeUtil.getDayDiff(expiration, createdAt)
}