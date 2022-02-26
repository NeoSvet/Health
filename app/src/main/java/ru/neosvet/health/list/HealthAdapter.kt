package ru.neosvet.health.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.neosvet.health.databinding.ItemHealthBinding
import ru.neosvet.health.databinding.ItemTitleBinding

class HealthAdapter(
    private val list: List<DataItem>
) : RecyclerView.Adapter<BasicHolder>() {
    companion object {
        private const val TITLE = 0
        private const val HEALTH = 1

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