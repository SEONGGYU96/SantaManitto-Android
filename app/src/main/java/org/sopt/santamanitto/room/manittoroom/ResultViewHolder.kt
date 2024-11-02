package org.sopt.santamanitto.room.manittoroom

import android.view.ViewGroup
import org.sopt.santamanitto.R
import org.sopt.santamanitto.databinding.ItemResultBinding
import org.sopt.santamanitto.room.manittoroom.network.ManittoRoomMember
import org.sopt.santamanitto.view.recyclerview.BaseViewHolder

class ResultViewHolder(
    parent: ViewGroup,
) : BaseViewHolder<ManittoRoomMember, ItemResultBinding>(R.layout.item_result, parent) {

    override fun bind(data: ManittoRoomMember) {
        binding.run {
            textviewItemresultSanta.text = data.santa.userName
            textviewItemresultMinitto.text = data.manitto.userName
        }
    }

    override fun clear() {
        binding.run {
            textviewItemresultSanta.text = ""
            textviewItemresultMinitto.text = ""
        }
    }
}