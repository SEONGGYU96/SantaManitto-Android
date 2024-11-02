package org.sopt.santamanitto.room.manittoroom

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.sopt.santamanitto.room.manittoroom.network.ManittoRoomMember
import org.sopt.santamanitto.util.ItemDiffCallback

class ResultAdapter() : ListAdapter<ManittoRoomMember, ResultViewHolder>(DiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResultViewHolder = ResultViewHolder(parent)

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffUtil = ItemDiffCallback<ManittoRoomMember>(
            onContentsTheSame = { oldItem, newItem -> oldItem.manitto.userId == newItem.manitto.userId },
            onItemsTheSame = { oldItem, newItem -> oldItem == newItem }
        )
    }
}