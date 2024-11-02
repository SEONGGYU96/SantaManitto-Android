package org.sopt.santamanitto.main.list

import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import org.sopt.santamanitto.room.data.TempMyManittoModel
import org.sopt.santamanitto.room.data.getRoomState
import org.sopt.santamanitto.room.network.RoomRequest
import org.sopt.santamanitto.user.data.source.UserMetadataSource
import org.sopt.santamanitto.util.ItemDiffCallback
import org.sopt.santamanitto.view.recyclerview.BaseViewHolder

class MyManittoListAdapter(
    private val userMetadataSource: UserMetadataSource,
    private val roomRequest: RoomRequest
) : ListAdapter<TempMyManittoModel, BaseViewHolder<TempMyManittoModel, *>>(DiffUtil) {

    private var enterListener: ((roomId: String, isMatched: Boolean, isFinished: Boolean) -> Unit)? =
        null
    private var exitListener: ((roomId: String, roomName: String, isHost: Boolean) -> Unit)? = null
    private var removeListener: ((roomId: String) -> Unit)? = null

    private val cachedMyManittoInfoModel = HashMap<String, MyManittoInfoModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<TempMyManittoModel, *> {
        return when (viewType) {
            VIEW_TYPE_REMOVED -> RemovedMyManttioViewHolder(parent, removeListener)
            VIEW_TYPE_EXPIRED -> ExpiredMyManittoViewHolder(parent, enterListener, removeListener)
            else -> BasicMyManittoViewHolder(
                parent,
                roomRequest,
                userMetadataSource,
                cachedMyManittoInfoModel,
                enterListener,
                exitListener
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<TempMyManittoModel, *>, position: Int) {
        when (holder) {
            is BasicMyManittoViewHolder -> {
                holder.clear()
                holder.bind(getItem(position))
            }

            is RemovedMyManttioViewHolder -> {
                holder.clear()
                holder.bind(getItem(position))
            }

            is ExpiredMyManittoViewHolder -> {
                holder.clear()
                holder.bind(getItem(position))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getItemViewType(position: Int): Int {
        val item = currentList[position]

        return when (item.getRoomState()) {
            RoomState.DELETED -> VIEW_TYPE_REMOVED
            RoomState.EXPIRED -> VIEW_TYPE_EXPIRED
            else -> VIEW_TYPE_DEFAULT
        }

    }

    fun clear() {
        cachedMyManittoInfoModel.clear()
        submitList(emptyList())
    }

    fun setOnItemClickListener(
        listener: (roomId: String, isMatched: Boolean, isFinished: Boolean) -> Unit
    ) {
        this.enterListener = listener
    }

    fun setOnExitClickListener(
        listener: (roomId: String, roomName: String, isHost: Boolean) -> Unit
    ) {
        this.exitListener = listener
    }

    fun setOnRemoveClickListener(listener: (roomId: String) -> Unit) {
        this.removeListener = listener
    }

    companion object {
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_REMOVED = 1
        private const val VIEW_TYPE_EXPIRED = 2

        private val DiffUtil = ItemDiffCallback<TempMyManittoModel>(
            onContentsTheSame = { oldItem, newItem -> oldItem.roomId == newItem.roomId },
            onItemsTheSame = { oldItem, newItem -> oldItem == newItem }
        )
    }
}
