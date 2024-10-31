package org.sopt.santamanitto.room.manittoroom.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
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
import javax.inject.Inject
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

    private val viewModel: ManittoRoomViewModel by activityViewModels()

    @Inject
    lateinit var resultAdapter: ResultAdapter

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
            // getPersonalRelationInfo()
        }

        setOnClickListener()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val value = savedInstanceState?.getBoolean(SCREEN_KEY, true) ?: true
        if (!value) {
            showResultView()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(SCREEN_KEY, isFinishScreen)
        super.onSaveInstanceState(outState)
    }

    private fun setOnClickListener() {
        binding.run {
            santabottombuttonFinish.setOnClickListener {
                showResultView()
            }
            santabackgroundFinish.setOnBackKeyClickListener {
                requireActivity().finish()
            }
            santabottombuttonReturn.setOnClickListener {
                requireActivity().finish()
            }
            santabuttonFinishExit.setOnClickListener {
                showExitDialog()
            }
        }
    }

    private fun showResultView() {
        isFinishScreen = false
        //finish 뷰 가리기, result 뷰 세팅
        finishBinding.root.visibility = GONE
        resultBinding.run {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            root.visibility = VISIBLE
            recyclerviewResult.adapter = resultAdapter
        }

        //총 인원 설정
        viewModel.members.observe(viewLifecycleOwner) {
            resultBinding.textviewResultTitle.text =
                String.format(getString(R.string.result_title), it.size)
        }

        //버튼 동작 변경
        binding.santabottombuttonFinish.run {
            setText(R.string.result_bottom)
            setOnClickListener {
                requireActivity().finish()
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