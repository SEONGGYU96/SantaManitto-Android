package org.sopt.santamanitto.room.join

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.FragmentJoinRoomBinding
import org.sopt.santamanitto.room.join.network.JoinRoomModel
import org.sopt.santamanitto.room.manittoroom.ManittoRoomActivity
import org.sopt.santamanitto.util.FragmentUtil.hideKeyboardOnOutsideEditText
import org.sopt.santamanitto.util.base.BaseFragment
import org.sopt.santamanitto.view.dialog.RoundDialogBuilder
import timber.log.Timber

@AndroidEntryPoint
class JoinRoomFragment : BaseFragment<FragmentJoinRoomBinding>(R.layout.fragment_join_room, false) {

    companion object {
        private const val TAG = "JoinRoomFragment"
    }

    private val joinRoomViewModel: JoinRoomViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = joinRoomViewModel
        subscribeUI()
        setOnClickListener()
        hideKeyboardOnOutsideEditText()

        binding.santabackgroundJoinroom.setMiddleTitleFontWeight(500)
    }

    private fun subscribeUI() {
        joinRoomViewModel.run {
            isAlreadyMatchedRoom.observe(
                viewLifecycleOwner, ::showAlreadyMatchedDialog
            )
            isDuplicatedMember.observe(
                viewLifecycleOwner, ::showDuplicatedMemberDialog
            )
            isWrongInvitationCode.observe(
                viewLifecycleOwner, ::showWrongInvitationCodeDialog
            )
            isAlreadyEnteredRoom.observe(
                viewLifecycleOwner, ::showAlreadyEnteredDialog
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

    private fun startManittoRoomActivity(joinRoomResponse: JoinRoomModel) {
        requireActivity().run {
            startActivity(Intent(this, ManittoRoomActivity::class.java).apply {
                putExtra(ManittoRoomActivity.EXTRA_ROOM_ID, joinRoomResponse.room.id)
            })
        }
        findNavController().navigateUp()
    }

    private fun showAlreadyMatchedDialog(isShow: Boolean) {
        if (isShow) {
            showErrorDialog(getString(R.string.joinroom_dialog_already_matched))
        }
    }

    private fun showDuplicatedMemberDialog(isShow: Boolean) {
        if (isShow) {
            showErrorDialog(getString(R.string.joinroom_dialog_duplicated_member))
        }
    }

    private fun showWrongInvitationCodeDialog(isShow: Boolean) {
        if (isShow) {
            showErrorDialog(getString(R.string.joinroom_dialog_wrong_invitation_code))
        }
    }

    private fun showAlreadyEnteredDialog(isShow: Boolean) {
        if (isShow) {
            showErrorDialog(getString(R.string.joinroom_dialog_already_entered))
        }
    }

    private fun showErrorDialog(message: String) {
        Timber.d("showErrorDialog: $message")
        RoundDialogBuilder()
            .setContentText(message)
            .addHorizontalButton(getString(R.string.dialog_confirm))
            .build()
            .show(parentFragmentManager, "error_dialog")
    }
}