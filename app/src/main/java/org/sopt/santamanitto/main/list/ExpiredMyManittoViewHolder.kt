package org.sopt.santamanitto.main.list

import android.view.ViewGroup
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.ItemMymanittoExpiredBinding
import org.sopt.santamanitto.room.data.TempMyManittoModel
import org.sopt.santamanitto.view.recyclerview.BaseViewHolder

class ExpiredMyManittoViewHolder(
    parent: ViewGroup,
    enterListener: ((roomId: String, isMatched: Boolean, isFinished: Boolean) -> Unit)?,
    removeListener: ((roomId: String) -> Unit)?
) : BaseViewHolder<TempMyManittoModel, ItemMymanittoExpiredBinding>(
    R.layout.item_mymanitto_expired,
    parent
) {

    private val exitButton = binding.textviewMymanittoexpiredButton
    private val title = binding.textviewMymanittoexpriedTitle

    private var roomId = ""

    init {
        removeListener?.let {
            exitButton.setOnClickListener {
                it(roomId)
            }
        }
        enterListener?.let {
            binding.root.setOnClickListener {
                it(roomId, false, true)
            }
        }
    }

    override fun bind(data: TempMyManittoModel) {
        title.text = data.roomName
        roomId = data.roomId
    }

    override fun clear() {
        title.text = ""
        roomId = ""
    }
}