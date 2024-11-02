package org.sopt.santamanitto.view.dialog.withdraw

import android.content.Context
import androidx.fragment.app.DialogFragment
import org.sopt.santamanitto.R
import org.sopt.santamanitto.view.dialog.RoundDialogBuilder

object WithdrawDialogCreator {
    fun create(context: Context, listener: () -> Unit): DialogFragment {
        val message = context.getString(R.string.withdraw_dialog_title)

        return RoundDialogBuilder()
            .setContentText(message)
            .addHorizontalButton(context.getString(R.string.withdraw_dialog_cancel)) {
                listener()
            }
            .addHorizontalButton(context.getString(R.string.withdraw_dialog_confirm), true)
            .build()
    }
}
