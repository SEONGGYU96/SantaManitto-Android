package org.sopt.santamanitto.main.list

import android.view.ViewGroup
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.ItemMymanittoRemovedBinding
import org.sopt.santamanitto.room.data.MyManittoModel
import org.sopt.santamanitto.view.recyclerview.BaseViewHolder

class RemovedMyManittoViewHolder(
    parent: ViewGroup,
    removeListener: ((roomId: String) -> Unit)?
) : BaseViewHolder<MyManittoModel, ItemMymanittoRemovedBinding>(
    R.layout.item_mymanitto_removed,
    parent
) {

    private val removeButton = binding.textviewMymanittoremovedButton

    private var roomId = ""

    init {
        removeListener?.let {
            removeButton.setOnClickListener {
                it(roomId)
            }
        }
    }

    override fun bind(data: MyManittoModel) {
        roomId = data.roomId
    }

    override fun clear() {
        roomId = ""
    }
}