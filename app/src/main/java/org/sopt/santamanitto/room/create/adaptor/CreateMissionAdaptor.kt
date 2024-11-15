package org.sopt.santamanitto.room.create.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.sopt.santamanitto.databinding.ItemAddMissionBinding
import org.sopt.santamanitto.databinding.ItemCreateMissionBinding

open class CreateMissionAdaptor(
    private val createMissionCallback: CreateMissionCallback,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val missions = mutableListOf<String>()
    private var currentMissionText: String? = null


    private var textChangeListener: OnMissionTextChangeListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemCreateMissionBinding.inflate(inflater, parent, false)
            CreateMissionViewHolder(createMissionCallback, binding) { text ->
                currentMissionText = text
                textChangeListener?.onTextChanged(text)
                (parent as? RecyclerView)?.post {
                    notifyItemChanged(missions.size + 1, PAYLOAD_TEXT_CHANGED)
                }
            }
        } else {
            val binding = ItemAddMissionBinding.inflate(inflater, parent, false)
            AddMissionViewHolder(createMissionCallback, binding)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isNotEmpty()) {
            if (holder is AddMissionViewHolder && payloads.contains(PAYLOAD_TEXT_CHANGED)) {
                holder.updateText(currentMissionText)
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is CreateMissionViewHolder) {
            holder.bind(missions.getOrNull(position))
        } else if (holder is AddMissionViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int = missions.size + 2

    override fun getItemViewType(position: Int): Int =
        if (position == missions.size + 1) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM

    fun setList(data: List<String>) {
        missions.clear()
        missions.addAll(data)
        notifyDataSetChanged()
    }

    interface OnMissionTextChangeListener {
        fun onTextChanged(newText: String?)
    }

    fun setTextChangeListener(listener: OnMissionTextChangeListener) {
        this.textChangeListener = listener
    }

    interface CreateMissionCallback {
        fun onMissionInserted(mission: String)

        fun onMissionDeleted(mission: String)
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_FOOTER = 1

        private const val PAYLOAD_TEXT_CHANGED = "PAYLOAD_TEXT_CHANGED"
    }
}
