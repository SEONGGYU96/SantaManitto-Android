package org.sopt.santamanitto.room.create.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.sopt.santamanitto.R
import org.sopt.santamanitto.base.BaseFragment
import org.sopt.santamanitto.databinding.FragmentCreateRoomBinding
import org.sopt.santamanitto.dialog.RoundDialogBuilder
import org.sopt.santamanitto.room.create.setExpirationDiff
import org.sopt.santamanitto.room.create.setExpirationPreview
import org.sopt.santamanitto.room.create.setExpirationTime
import org.sopt.santamanitto.room.create.data.ExpirationLiveData
import org.sopt.santamanitto.room.create.setExpirationPeriod
import org.sopt.santamanitto.room.create.viewmodel.CreateRoomAndMissionViewModel
import org.sopt.santamanitto.view.SantaEditText
import org.sopt.santamanitto.view.SantaPeriodPicker
import org.sopt.santamanitto.view.santanumberpicker.SantaNumberPicker
import org.sopt.santamanitto.view.setTextColorById

class CreateRoomFragment : BaseFragment<FragmentCreateRoomBinding>(R.layout.fragment_create_room, true) {

    companion object {
        private const val MAX_ROOM_NAME_LENGTH = 17
    }

    private val viewModel: CreateRoomAndMissionViewModel by activityViewModels()

    private val args: CreateRoomFragmentArgs by navArgs()

    private var isNewRoom = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = this@CreateRoomFragment.viewModel

        loadData()

        initView()

        refreshUI(viewModel.expirationLiveData)

        subscribeUI()

        setOnClickListener()
    }

    private fun loadData() {
        viewModel.start(args.roomId)
        isNewRoom = args.roomId == -1
    }

    private fun initView() {
        binding.textviewCreateroomExpirationdescription.text = String.format(
            getString(R.string.createroom_expiration_description),
            SantaPeriodPicker.MINIMUM_PERIOD,
            SantaPeriodPicker.MAXIMUM_PERIOD
        )

        binding.textviewCreateroomAlert.text = String.format(getString(R.string.santanameinput_alert), MAX_ROOM_NAME_LENGTH)

        binding.edittextCreateroomRoomname.run {
            addTextChangeListener(SantaEditText.SantaEditLimitLengthWatcher(this, MAX_ROOM_NAME_LENGTH) {
                if (it) {
                    binding.textviewCreateroomAlert.setTextColorById(R.color.red)
                } else {
                    binding.textviewCreateroomAlert.setTextColorById(R.color.gray_3)
                }
            })
        }

        if (!isNewRoom) {
            binding.run {
                santabackgroundCreateroom.hideDescription()
                santabottombuttonCreateroom.text = getString(R.string.createroom_modify_done)
                santabackgroundCreateroom.title = getString(R.string.createroom_modify_title)
            }
        }
    }

    private fun setOnClickListener() {
        binding.run {
            santabottombuttonCreateroom.setOnClickListener {
                if (isNewRoom) {
                    findNavController().navigate(CreateRoomFragmentDirections.actionCreateRoomFragmentToCreateMissionsFragment())
                } else {
                    this@CreateRoomFragment.viewModel.modifyRoom {
                        findNavController().navigateUp()
                    }
                }
            }
            santabackgroundCreateroom.setOnBackKeyClickListener {
                if (isNewRoom) {
                    requireActivity().finish()
                } else {
                    findNavController().navigateUp()
                }
            }
            santaperiodpickerCreateroomExpiration.setOnPeriodChangedListener { period ->
                this@CreateRoomFragment.viewModel.setPeriod(period)
            }
            santaswitchCreateroomAmpm.setOnSwitchChangedListener { isAm ->
                this@CreateRoomFragment.viewModel.setAmPm(!isAm)
            }
            textviewCreateroomExpirationpreview.setOnClickListener {
                showTimePicker()
            }
        }
    }

    private fun subscribeUI() {
        viewModel.run {
            expirationLiveData.observe(viewLifecycleOwner, this@CreateRoomFragment::refreshUI)

            hint.observe(viewLifecycleOwner) { previousRoomName ->
                if (!previousRoomName.isNullOrEmpty()) {
                    binding.edittextCreateroomRoomname.hint = previousRoomName
                }
            }
        }
    }

    private fun refreshUI(expiration: ExpirationLiveData) {
        binding.run {
            setExpirationDiff(textviewCreateroomPreviewstart, expiration)
            setExpirationPreview(textviewCreateroomPreviewdate, expiration)
            setExpirationTime(textviewCreateroomExpirationpreview, expiration)
            setExpirationPeriod(santaperiodpickerCreateroomExpiration, expiration)
        }
    }

    private fun showTimePicker() {
        RoundDialogBuilder()
                .setTitle(getString(R.string.createroom_dialog_expiration_time_setting))
                .setContentView(getPickerView())
                .addHorizontalButton(getString(R.string.dialog_cancel))
                .addHorizontalButton(getString(R.string.dialog_confirm)) {
                    val hour = it!!.findViewById<SantaNumberPicker>(R.id.santanumberpicker_pickerdialog_hour)
                            .getCurrentNumber()
                    val minute = it.findViewById<SantaNumberPicker>(R.id.santanumberpicker_pickerdialog_minute)
                            .getCurrentNumber()
                    viewModel.setTime(hour, minute)
                }
                .build()
                .show(parentFragmentManager, "createroom_timepicker")
    }

    private fun getPickerView(): View {
        return LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_room_time_picker, binding.root as ViewGroup, false)
                .apply {
                    findViewById<SantaNumberPicker>(R.id.santanumberpicker_pickerdialog_hour)
                            .setInitialPosition(viewModel.expirationLiveData.hour - 1)
                    findViewById<SantaNumberPicker>(R.id.santanumberpicker_pickerdialog_minute)
                            .setInitialPosition(viewModel.expirationLiveData.minute)
                }
    }
}