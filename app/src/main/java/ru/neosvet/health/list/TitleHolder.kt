package ru.neosvet.health.list

import ru.neosvet.health.databinding.ItemTitleBinding

class TitleHolder(
    private val binding: ItemTitleBinding
) : BasicHolder(binding.root) {
    override fun setData(item: DataItem) {
        val data = item as DataItem.Title
        binding.tvTitle.text = data.title
    }
}