package org.sopt.santamanitto.user.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.FragmentEditNameBinding
import org.sopt.santamanitto.util.FragmentUtil.hideKeyboardOnOutsideEditText
import org.sopt.santamanitto.util.base.BaseFragment
import org.sopt.santamanitto.view.dialog.withdraw.WithdrawDialogCreator

@AndroidEntryPoint
class EditNameFragment : BaseFragment<FragmentEditNameBinding>(R.layout.fragment_edit_name, true) {
    private val viewModel: EditNameViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        setOnClickListener()
        observeChangeRequest()
        observeWithdrawEvent()
        setupNameInputObserver()
        hideKeyboardOnOutsideEditText()
    }

    private fun setOnClickListener() {
        binding.run {
            santabackgroundEditname.setOnBackKeyClickListener {
                findNavController().navigateUp()
            }

            textviewWithdraw.setOnClickListener {
                showWithDrawDialog()
            }
        }
    }

    private fun observeChangeRequest() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.requestDone.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).collectLatest { isSuccess ->
                if (isSuccess) {
                    findNavController().popBackStack(R.id.mainFragment, false)
                    Toast.makeText(context, R.string.editname_finish, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeWithdrawEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.withdrawEvent.collect {
                    requireActivity().finishAffinity()
                }
            }
        }
    }

    private fun setupNameInputObserver() {
        binding.nameinputEditname.observeTextChanges { newName ->
            viewModel.setNewName(newName)
        }
    }

    private fun showWithDrawDialog() {
        WithdrawDialogCreator.create(requireContext()) {
            viewModel.withdraw()
        }.show(childFragmentManager, "withdraw")
    }
}
