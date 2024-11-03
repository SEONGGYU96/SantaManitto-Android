package org.sopt.santamanitto.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.FragmentMainBinding
import org.sopt.santamanitto.main.list.MyManittoListAdapter
import org.sopt.santamanitto.room.create.CreateRoomActivity
import org.sopt.santamanitto.room.manittoroom.ManittoRoomActivity
import org.sopt.santamanitto.view.dialog.exit.ExitDialogCreator
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    @Inject
    lateinit var adapter: MyManittoListAdapter

    private val viewModel: MainViewModel by viewModels()

    private var backPressedTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            FragmentMainBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@MainFragment
                viewModel = this@MainFragment.viewModel
                recyclerviewMainHistory.adapter = adapter
                recyclerviewMainHistory.visibility = View.GONE
                constraintlayoutMainNomymanitto.visibility = View.VISIBLE
            }

        binding.santabackgroundMain.isBackKeyEnabled = false

        subscribeUI()

        setOnClickListener()

        initBackPressedCallback()

        return binding.root
    }

    private fun subscribeUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myManittoModelList
                    .collect { list ->
                        adapter.submitList(list)

                        Timber.e("list: $list")

                        if (list.isEmpty()) {
                            binding.recyclerviewMainHistory.visibility = View.INVISIBLE
                            binding.constraintlayoutMainNomymanitto.visibility = View.VISIBLE
                        } else {
                            binding.recyclerviewMainHistory.visibility = View.VISIBLE
                            binding.constraintlayoutMainNomymanitto.visibility = View.INVISIBLE
                        }
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRefreshing.collect { isRefreshing ->
                    binding.progressbarMainJoinedRooms.isVisible = isRefreshing
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initJoinedRooms()
    }

    private fun setOnClickListener() {
        binding.run {
            santaimageroundbuttonMainMakeroom.setOnClickListener {
                startCreateRoomActivity()
            }
            santaimageroundbuttonMainJoin.setOnClickListener {
                navigateJoinRoomFragment()
            }
            imagebuttonMainSetting.setOnClickListener {
                navigateSettingFragment()
            }
        }

        adapter.run {
            setOnItemClickListener { roomId, isMatched, isFinished ->
                requireActivity().run {
                    startActivity(
                        Intent(this, ManittoRoomActivity::class.java).apply {
                            putExtra(ManittoRoomActivity.EXTRA_ROOM_ID, roomId)
                            putExtra(ManittoRoomActivity.EXTRA_IS_MATCHED, isMatched)
                            putExtra(ManittoRoomActivity.EXTRA_IS_FINISHED, isFinished)
                        },
                    )
                }
            }
            setOnExitClickListener { roomId, roomName, isHost ->
                ExitDialogCreator.create(requireContext(), roomName, isHost) {
                    viewModel.exitRoom(roomId)
                }.show(childFragmentManager, "exit")
            }
            setOnRemoveClickListener { roomId ->
                viewModel.deleteRoom(roomId)
            }
        }
    }

    private fun initJoinedRooms() {
        viewModel.fetchMyManittoList()
    }

    private fun navigateSettingFragment() {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingFragment())
    }

    private fun navigateJoinRoomFragment() {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToJoinRoomFragment())
    }

    private fun startCreateRoomActivity() {
        requireActivity().run {
            startActivity(Intent(this, CreateRoomActivity::class.java))
        }
    }

    private fun initBackPressedCallback() {
        val onBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (System.currentTimeMillis() - backPressedTime >= BACK_PRESSED_INTERVAL) {
                        backPressedTime = System.currentTimeMillis()
                        Snackbar.make(
                            binding.root,
                            getString(R.string.main_back_pressed),
                            Snackbar.LENGTH_SHORT,
                        ).show()
                    } else {
                        requireActivity().finish()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback,
        )
    }

    companion object {
        const val BACK_PRESSED_INTERVAL = 2000
    }
}
