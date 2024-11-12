package org.sopt.santamanitto.room.create.fragment

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.FragmentCreateMissionBinding
import org.sopt.santamanitto.room.create.adaptor.CreateMissionAdaptor
import org.sopt.santamanitto.room.create.fragment.CreateMissionsFragmentDirections.Companion.actionCreateMissionsFragmentToCreateConfirmFragment
import org.sopt.santamanitto.room.create.viewmodel.CreateRoomAndMissionViewModel
import org.sopt.santamanitto.util.FragmentUtil.hideKeyboardOnOutsideEditText
import org.sopt.santamanitto.util.base.BaseFragment
import org.sopt.santamanitto.view.dialog.RoundDialogBuilder

class CreateMissionsFragment :
    BaseFragment<FragmentCreateMissionBinding>(
        R.layout.fragment_create_mission,
        false,
    ),
    CreateMissionAdaptor.CreateMissionCallback {
    private val viewModel: CreateRoomAndMissionViewModel by activityViewModels()

    private val createMissionAdaptor = CreateMissionAdaptor(this).apply {
        setTextChangeListener(object : CreateMissionAdaptor.OnMissionTextChangeListener {
            override fun onTextChanged(newText: String?) {
                viewModel.unsavedMission.value = newText
            }
        })
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.recyclerviewCreatemission.adapter = createMissionAdaptor

        subscribeUI()

        saveMeasuredHeightOfRecyclerView()

        setOnClickListener()

        initOnBackPressedListener()

        observeUnsavedMission()

        hideKeyboardOnOutsideEditText()
    }

    override fun onMissionInserted(mission: String) {
        viewModel.addMission(mission)
        viewModel.unsavedMission.value = ""
        binding.santabottombuttonCreatemissionDone.isEnabled = true
    }

    override fun onMissionDeleted(mission: String) {
        viewModel.deleteMission(mission)
        if (!viewModel.hasMissions()) binding.santabottombuttonCreatemissionDone.isEnabled = false
    }

    private fun setOnClickListener() {
        binding.run {
            santabottombuttonCreatemissionSkip.setOnClickListener {
                if (viewModel.hasMissions()) {
                    showSkipDialog()
                } else {
                    showNoMissionDialog()
                }
            }
            santabottombuttonCreatemissionDone.setOnClickListener {
                saveUnsavedMission()
                if (viewModel.hasMissions()) {
                    navigateConfirmFragment()
                } else {
                    showNoMissionDialog()
                }
            }
            santabackgroundCreatemission.setOnBackKeyClickListener {
                saveUnsavedMission()
                findNavController().navigateUp()
            }
        }
    }

    private fun initOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveUnsavedMission()
                    isEnabled = false
                    remove()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            })
    }

    private fun saveUnsavedMission() {
        if (!viewModel.unsavedMission.value.isNullOrBlank()) {
            viewModel.addMission(viewModel.unsavedMission.value!!)
            viewModel.unsavedMission.value = ""
        }
    }

    private fun subscribeUI() {
        viewModel.missions.observe(viewLifecycleOwner) { missionList ->
            createMissionAdaptor.setList(missionList.getMissions())
            binding.recyclerviewCreatemission.scrollToPosition(createMissionAdaptor.itemCount - 1)
            binding.santabottombuttonCreatemissionDone.isEnabled = !missionList.isEmpty()
        }
    }

    private fun saveMeasuredHeightOfRecyclerView() {
        binding.recyclerviewCreatemission.run {
            viewTreeObserver.addOnGlobalLayoutListener {
                viewModel.heightOfRecyclerView =
                    height - (100 * Resources.getSystem().displayMetrics.density).toInt()
            }
        }
    }

    private fun observeUnsavedMission() {
        viewModel.unsavedMission.observe(viewLifecycleOwner) {
            binding.santabottombuttonCreatemissionDone.isEnabled =
                !it.isNullOrBlank() || viewModel.hasMissions()
        }
    }

    private fun showSkipDialog() {
        RoundDialogBuilder()
            .setContentText(
                getString(R.string.createmission_dialog_skip_has_mission),
                true,
            ).addHorizontalButton(getString(R.string.createmission_skip_bottom_button)) {
                viewModel.clearMission()
                navigateConfirmFragment()
            }.addHorizontalButton(getString(R.string.createroom_btn_next))
            .build()
            .show(parentFragmentManager, "skip_dialog")
    }

    private fun showNoMissionDialog() {
        RoundDialogBuilder()
            .setContentText(
                getString(R.string.createmission_dialog_no_mission),
                true,
            ).addHorizontalButton(getString(R.string.createmission_skip_bottom_button)) {
                navigateConfirmFragment()
            }.addHorizontalButton(getString(R.string.createroom_btn_next))
            .build()
            .show(parentFragmentManager, "done_dialog")
    }

    private fun navigateConfirmFragment() {
        findNavController().navigate(actionCreateMissionsFragmentToCreateConfirmFragment())
    }
}
