package org.sopt.santamanitto.view.recyclerview

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("replaceAll")
fun <T> RecyclerView.replaceAll(list: List<T>?) {
    (this.adapter as? ListAdapter<T, *>)?.let { adapter ->
        adapter.submitList(list?.toList())
        this.visibility = if (list.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
    } ?: (this.adapter as? BaseAdapter<T>)?.run {
        if (list != null) {
            setList(list)
            // 모든 마진이 15dp로 설정되어있어서 일단 하드코딩, 추후 바인딩 어댑터 해제시 참고
            this@replaceAll.setItemMargin(15f)
            this@replaceAll.visibility = if (list.isEmpty()) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        } else {
            clear()
            this@replaceAll.visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("setItemMargin")
fun RecyclerView.setItemMargin(dimen: Float) {
    val orientation = (layoutManager as LinearLayoutManager?)?.orientation ?: return
    removeItemDecoration(MarginDecoration(dimen, orientation))
    addItemDecoration(MarginDecoration(dimen, orientation))
}