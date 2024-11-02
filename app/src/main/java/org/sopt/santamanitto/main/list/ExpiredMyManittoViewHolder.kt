package org.sopt.santamanitto.main.list

import android.view.ViewGroup
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.ItemMymanittoRemovedBinding
import org.sopt.santamanitto.room.data.TempMyManittoModel
import org.sopt.santamanitto.view.recyclerview.BaseViewHolder

class ExpiredMyManittoViewHolder(
    parent: ViewGroup,
    enterListener: ((roomId: String, isMatched: Boolean, isFinished: Boolean) -> Unit)?,
    removeListener: ((roomId: String) -> Unit)?
) : BaseViewHolder<TempMyManittoModel, ItemMymanittoRemovedBinding>(
    R.layout.item_mymanitto_removed,
    parent
) {

    private val removeButton = binding.textviewMymanittoremovedButton
    private val title = binding.textviewMymanittoremovedTitle
    private val content = binding.textviewMymanittoremovedContent

    private var roomId = ""

    init {
        removeListener?.let {
            removeButton.setOnClickListener {
                it(roomId)
            }
        }
        enterListener?.let {
            binding.root.setOnClickListener {
                it(roomId, false, true)
            }
        }
        content.text = itemView.context.getString(R.string.mymanitto_epired_content)
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