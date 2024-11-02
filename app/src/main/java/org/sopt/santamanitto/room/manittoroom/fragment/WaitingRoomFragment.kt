package org.sopt.santamanitto.room.manittoroom.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.FragmentWaitingRoomBinding
import org.sopt.santamanitto.room.manittoroom.ManittoRoomViewModel
import org.sopt.santamanitto.room.manittoroom.MemberAdapter
import org.sopt.santamanitto.room.manittoroom.fragment.WaitingRoomFragmentDirections.Companion.actionWaitingRoomFragmentToCreateRoomFragmentModify
import org.sopt.santamanitto.room.manittoroom.fragment.WaitingRoomFragmentDirections.Companion.actionWaitingRoomFragmentToFinishFragment
import org.sopt.santamanitto.room.manittoroom.fragment.WaitingRoomFragmentDirections.Companion.actionWaitingRoomFragmentToMatchingFragment
import org.sopt.santamanitto.util.ClipBoardUtil
import org.sopt.santamanitto.util.base.BaseFragment
import org.sopt.santamanitto.view.dialog.RoundDialogBuilder

@AndroidEntryPoint
class WaitingRoomFragment :
    BaseFragment<FragmentWaitingRoomBinding>(R.layout.fragment_waiting_room, false) {
    companion object {
        const val INVITATION_CODE_LABEL = "InvitationCode"
    }

    private val viewModel: ManittoRoomViewModel by activityViewModels()

    private val memberAdapter = MemberAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (viewModel.isMatched) {
            if (viewModel.isFinished) {
                navigateFinishFragment()
            } else {
                navigateMatchingFragment()
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        setOnClickListener()

        subscribeUI()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshManittoRoomInfo()
    }

    private fun initView() {
        binding.run {
            vm = viewModel
            recyclerviewWaitingroom.adapter = memberAdapter
        }
    }

    private fun setOnClickListener() {
        binding.run {
            santabackgroundWaitingroom.setOnBackKeyClickListener {
                requireActivity().finish()
            }
            textviewWaitingroomInvitationcode.setOnClickListener {
                ClipBoardUtil.copy(
                    requireContext(),
                    INVITATION_CODE_LABEL,
                    viewModel.invitationCode,
                )
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    Snackbar
                        .make(
                            binding.root,
                            getString(R.string.waitingroom_snackbar_invitation_code),
                            Snackbar.LENGTH_SHORT,
                        ).show()
                }
            }
            santabottombuttonWaitingroomModify.setOnClickListener {
                navigateModifyFragment()
            }
            santabottombuttonWaitingroomMatch.setOnClickListener {
                viewModel.match()
                navigateMatchingFragment()
            }
            buttonWaitingroomRefresh.setOnClickListener {
                viewModel.refreshManittoRoomInfo()
            }
        }
    }

    private fun subscribeUI() {
        viewModel.isExpired.observe(viewLifecycleOwner) { isExpired ->
            if (isExpired) {
                RoundDialogBuilder()
                    .setContentText(getString(R.string.mymanitto_epired_dialog))
                    .addHorizontalButton(getString(R.string.mymanitto_epired_dialog_button)) {
                        requireActivity().finish()
                    }.enableCancel(false)
                    .build()
                    .show(childFragmentManager, "expired")
            }
        }
    }

    private fun navigateModifyFragment() {
        findNavController().navigate(actionWaitingRoomFragmentToCreateRoomFragmentModify(viewModel.roomId))
    }

    private fun navigateFinishFragment() {
        findNavController().navigate(actionWaitingRoomFragmentToFinishFragment())
    }

    private fun navigateMatchingFragment() {
        findNavController().navigate(actionWaitingRoomFragmentToMatchingFragment())
    }
}
