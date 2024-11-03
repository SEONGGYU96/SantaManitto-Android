package org.sopt.santamanitto.room.join

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.FragmentJoinRoomBinding
import org.sopt.santamanitto.room.join.network.JoinRoomResponseModel
import org.sopt.santamanitto.room.manittoroom.ManittoRoomActivity
import org.sopt.santamanitto.util.FragmentUtil.hideKeyboardOnOutsideEditText
import org.sopt.santamanitto.util.base.BaseFragment

@AndroidEntryPoint
class JoinRoomFragment : BaseFragment<FragmentJoinRoomBinding>(R.layout.fragment_join_room, false) {

    private val joinRoomViewModel: JoinRoomViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = joinRoomViewModel
        subscribeUI()
        setOnClickListener()
        hideKeyboardOnOutsideEditText()

        binding.santabackgroundJoinroom.setMiddleTitleFontWeight(500)

        observeSantaEditText()
    }

    private fun observeSantaEditText() {
        binding.edittextJoinroomInvitationcode.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hideWarningMessage()
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    disableJoinButton()
                } else {
                    enableJoinButton()
                }
            }
        })
    }

    private fun subscribeUI() {
        joinRoomViewModel.run {
            isAlreadyMatchedRoom.observe(
                viewLifecycleOwner, ::handleAlreadyMatchedError
            )
            isWrongInvitationCode.observe(
                viewLifecycleOwner, ::handleWrongInvitationCodeError
            )
            isAlreadyEnteredRoom.observe(
                viewLifecycleOwner, ::handleAlreadyJoinedError
            )
        }
    }

    private fun setOnClickListener() {
        binding.run {
            santabackgroundJoinroom.setOnBackKeyClickListener {
                findNavController().navigateUp()
            }

            santabottomButtonJoinroom.setOnClickListener {
                joinRoomViewModel.joinRoom(this@JoinRoomFragment::startManittoRoomActivity)
            }
        }
    }

    private fun startManittoRoomActivity(joinRoomResponse: JoinRoomResponseModel) {
        requireActivity().run {
            startActivity(Intent(this, ManittoRoomActivity::class.java).apply {
                putExtra(ManittoRoomActivity.EXTRA_ROOM_ID, joinRoomResponse.roomId)
            })
        }
        findNavController().navigateUp()
    }

    private fun showWarningMessage(message: String) {
        binding.clJoinroomAlert.visibility = View.VISIBLE
        binding.textviewInvitecodeAlert.text = message
        disableJoinButton()
    }

    // 이미 매칭 진행된 방일 때 경고 메시지 처리
    private fun handleAlreadyMatchedError(isShow: Boolean) {
        if (isShow) {
            showWarningMessage(getString(R.string.joinroom_alert_already_matched))
        }
    }

    // 잘못된 초대 코드일 때 경고 메시지 처리
    private fun handleWrongInvitationCodeError(isShow: Boolean) {
        if (isShow) {
            showWarningMessage(getString(R.string.joinroom_alert_wrong_invitation_code))
        }
    }

    // 이미 방에 입장했을 때 경고 메시지 처리
    private fun handleAlreadyJoinedError(isShow: Boolean) {
        if (isShow) {
            showWarningMessage(getString(R.string.joinroom_alert_already_joined))
        }
    }

    private fun hideWarningMessage() {
        binding.clJoinroomAlert.visibility = View.GONE
    }

    private fun disableJoinButton() {
        binding.santabottomButtonJoinroom.isEnabled = false
    }

    private fun enableJoinButton() {
        binding.santabottomButtonJoinroom.isEnabled = true
    }
}