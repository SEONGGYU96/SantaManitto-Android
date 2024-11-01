package org.sopt.santamanitto.room.manittoroom.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.FragmentManittoRoomFinishBinding
import org.sopt.santamanitto.databinding.LayoutFinishBinding
import org.sopt.santamanitto.databinding.LayoutResultBinding
import org.sopt.santamanitto.room.manittoroom.ManittoRoomViewModel
import org.sopt.santamanitto.room.manittoroom.ResultAdapter
import org.sopt.santamanitto.util.BindingAdapters.setLayoutHeight
import org.sopt.santamanitto.view.dialog.RoundDialogBuilder
import timber.log.Timber
import android.view.View.OnLayoutChangeListener as OnLayoutChangeListener1

@AndroidEntryPoint
class FinishFragment : Fragment() {

    companion object {
        private const val TAG = "FinishFragment"
        private const val SCREEN_KEY = "isFinishScreen"
    }

    private lateinit var binding: FragmentManittoRoomFinishBinding

    private lateinit var finishBinding: LayoutFinishBinding
    private lateinit var resultBinding: LayoutResultBinding

    private var _adapter: ResultAdapter? = null
    val adapter
        get() = requireNotNull(_adapter)

    private val viewModel: ManittoRoomViewModel by activityViewModels()

    private var isFinishScreen = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManittoRoomFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel

            root.addOnLayoutChangeListener(object : OnLayoutChangeListener1 {
                override fun onLayoutChange(
                    v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                    oldTop: Int, oldRight: Int, oldBottom: Int
                ) {
                    binding.root.removeOnLayoutChangeListener(this)

                    initMissionText()
                }
            })
        }

        finishBinding = binding.includeFinishMatched.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        resultBinding = binding.includeFinishResult

        viewModel.run {
            refreshManittoRoomInfo()
        }

        setResultBinding()
        setOnClickListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(SCREEN_KEY, isFinishScreen)
        super.onSaveInstanceState(outState)
    }

    private fun setResultBinding() {
        resultBinding.run {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            _adapter = ResultAdapter()
            adapter.submitList(viewModel.members.value)
            recyclerviewResult.adapter = adapter
        }
        viewModel.members.observe(viewLifecycleOwner) {
            resultBinding.textviewResultTitle.text =
                String.format(getString(R.string.result_title), it.size)
        }
    }

    private fun setOnClickListener() {
        binding.run {
            santabackgroundFinish.setOnBackKeyClickListener {
                requireActivity().finish()
            }
            santabottombuttonReturn.setOnClickListener {
                requireActivity().finish()
            }
            santabuttonFinishExit.setOnClickListener {
                showExitDialog()
            }
            santabottombuttonFinish.setOnClickListener {
                if (isFinishScreen) {
                    isFinishScreen = false
                    binding.santabottombuttonFinish.setText(R.string.finish_bottom_button)
                } else {
                    isFinishScreen = true
                    binding.santabottombuttonFinish.setText(R.string.result_bottom)
                }
                finishBinding.root.isVisible = isFinishScreen
                resultBinding.root.isVisible = !isFinishScreen
            }
        }
    }

    private fun initMissionText() {
        finishBinding.textviewFinishMission.run {
            //현재 폰트의 2줄 크기를 반영하기 위함. 기본 텍스트가 \n\n 임
            setLayoutHeight(this, this.height)
            viewModel.missionToMe.observe(viewLifecycleOwner) { mission ->
                text =
                    if (mission.isNullOrEmpty()) getString(R.string.matched_no_mission) else mission
            }
        }
    }

    private fun showExitDialog() {
        if (viewModel.roomName.value == null) {
            Timber.tag(TAG).e("showExitDialog(): roomName is null")
            return
        }
        RoundDialogBuilder()
            .setContentText(requireContext().getString(R.string.exit_dialog_history))
            .addHorizontalButton(requireContext().getString(R.string.dialog_cancel))
            .addHorizontalButton(requireContext().getString(R.string.dialog_confirm)) {
                viewModel.removeHistory {
                    requireActivity().finish()
                }
            }
            .build()
            .show(childFragmentManager, "exit")
    }
}