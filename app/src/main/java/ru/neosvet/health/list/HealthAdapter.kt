package ru.neosvet.health.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.neosvet.health.databinding.ItemHealthBinding
import ru.neosvet.health.databinding.ItemTitleBinding

class HealthAdapter : RecyclerView.Adapter<BasicHolder>() {
    companion object {
        private const val TITLE = 0
        private const val HEALTH = 1
    }

    private val list = mutableListOf<DataItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: List<DataItem>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun getIdBy(position: Int): String {
        if (list[position] is DataItem.Title)
            return ""
        val item = list[position] as DataItem.Health
        return item.id
    }

    fun remove(index: Int) {
        list.removeAt(index)
        notifyItemRemoved(index)
        val isLastItem = index == list.size || list[index] is DataItem.Title
        val preIndex = index - 1
        if (isLastItem && list[preIndex] is DataItem.Title) {
            list.removeAt(preIndex)
            notifyItemRemoved(preIndex)
        }
    }

    override fun getItemViewType(position: Int) =
        if (list[position] is DataItem.Health)
            HEALTH
        else
            TITLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicHolder =
        when (viewType) {
            HEALTH -> HealthHolder(
                ItemHealthBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> TitleHolder( //TITLE
                ItemTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }


    override fun onBindViewHolder(holder: BasicHolder, position: Int) {
        holder.setData(list[position])
    }

    override fun getItemCount() = list.size
}