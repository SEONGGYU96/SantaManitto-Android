package org.sopt.santamanitto.main.list

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.ItemMymanittoBinding
import org.sopt.santamanitto.room.data.MyManittoModel
import org.sopt.santamanitto.room.data.PersonalRoomModel
import org.sopt.santamanitto.room.data.getRoomState
import org.sopt.santamanitto.room.network.RoomRequest
import org.sopt.santamanitto.user.data.source.UserMetadataSource
import org.sopt.santamanitto.util.TimeUtil
import org.sopt.santamanitto.view.recyclerview.BaseViewHolder
import org.sopt.santamanitto.view.setBackgroundTint

class BasicMyManittoViewHolder(
    parent: ViewGroup,
    private val roomRequest: RoomRequest,
    private val userMetadataSource: UserMetadataSource,
    private val cachedRoomInfo: HashMap<String, MyManittoInfoModel>,
    private var listener: ((roomId: String, isMatched: Boolean, isFinished: Boolean) -> Unit)? = null,
    private var exitListener: ((roomId: String, roomName: String, isHost: Boolean) -> Unit)? = null,
) : BaseViewHolder<MyManittoModel, ItemMymanittoBinding>(R.layout.item_mymanitto, parent) {
    private val contentText = binding.textviewMymanittoManittoinfo
    private val stateText = binding.textviewMymanittoState
    private val missionText = binding.textviewMymanittoMission
    private val loadingBar = binding.santaloadingJoinedroom
    private val exitButton = binding.buttonMymanittoExit
    private val roomName = binding.textviewMymanittoTitle

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bind(data: MyManittoModel) {
        roomName.text = data.roomName

        listener?.let { listener ->
            val isFinished = if (data.expirationDate == null) {
                true
            } else {
                TimeUtil.isExpired(data.expirationDate)
            }

            binding.root.setOnClickListener {
                listener.invoke(data.roomId, data.matchingDate != null, isFinished)
            }
        }

        exitListener?.let { listener ->
            exitButton.setOnClickListener {
                listener.invoke(
                    data.roomId,
                    data.roomName,
                    data.creator.id == userMetadataSource.getUserId(),
                )
            }
        }

        setRoomState(data)

        if (data.matchingDate == null) {
            clearLoading()
            return
        }

        if (cachedRoomInfo.containsKey(data.roomId)) {
            val info = cachedRoomInfo[data.roomId]!!
            setManittoInfo(info = info, isMatched = true)
            clearLoading()
        } else {
            requestAndCacheInfo(roomId = data.roomId, data = data)
        }
    }

    override fun clear() {
        roomName.text = ""
        contentText.text = ""
        stateText.text = ""
        missionText.text = ""
        loadingBar.visibility = View.VISIBLE
        exitButton.visibility = View.GONE
    }

    private fun requestAndCacheInfo(
        roomId: String,
        data: MyManittoModel,
    ) {
        roomRequest.getPersonalRoomInfo(
            roomId,
            object : RoomRequest.GetPersonalRoomInfoCallback {
                override fun onLoadPersonalRoomInfo(personalRoom: PersonalRoomModel) {
                    val info = MyManittoInfoModel(
                        manittoName = personalRoom.manitto.username ?: "",
                        mission = personalRoom.mission.content,
                    )

                    cachedRoomInfo[roomId] = info

                    setManittoInfo(info = info, isMatched = data.matchingDate != null)
                    clearLoading()
                }

                override fun onDataNotAvailable() {
                    loadingBar.setError(true)
                }
            },
        )
    }


    private fun setManittoInfo(
        info: MyManittoInfoModel,
        isMatched: Boolean,
    ) {
        if (isMatched) {
            missionText.text = info.mission
            contentText.text =
                String.format(getString(R.string.joinedroom_manitto_info), info.manittoName)
        } else {
            contentText.text = ""
        }
    }

    private fun clearLoading() {
        loadingBar.visibility = View.GONE
    }

    private fun setRoomState(data: MyManittoModel) {
        when (data.getRoomState()) {
            RoomState.IN_PROGRESS -> {
                showExitButton(false)
                val daysUntilExpiration = TimeUtil.getDayDiffFromNow(data.expirationDate!!)
                stateText.text = String.format(
                    getString(R.string.joinedroom_daydiff),
                    daysUntilExpiration
                )
                stateText.setBackgroundTint(R.color.red)
            }

            RoomState.WAITING -> {
                stateText.text = getString(R.string.joinedroom_state_matching)
                stateText.setBackgroundTint(R.color.dark_gray)
                showExitButton(true)
            }

            RoomState.FINISHED -> {
                showExitButton(false)
                stateText.text = getString(R.string.joinedroom_state_done)
                stateText.setBackgroundTint(R.color.gray_2)
            }

            else -> {
                throw IllegalStateException("Unexpected room state in BasicMyManittoViewHolder")
            }
        }
    }

    private fun showExitButton(isShow: Boolean) {
        if (isShow) {
            exitButton.visibility = View.VISIBLE
            missionText.visibility = View.GONE
        } else {
            exitButton.visibility = View.GONE
            missionText.visibility = View.VISIBLE
        }
    }

    private fun getString(
        @StringRes resId: Int,
    ): String {
        return itemView.context.getString(resId)
    }
}
