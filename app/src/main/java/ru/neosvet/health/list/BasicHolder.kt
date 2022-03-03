package ru.neosvet.health.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BasicHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun setData(item: DataItem)
}