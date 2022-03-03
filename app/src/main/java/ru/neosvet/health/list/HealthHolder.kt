package ru.neosvet.health.list

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import ru.neosvet.health.R
import ru.neosvet.health.databinding.ItemHealthBinding

class HealthHolder(
    private val binding: ItemHealthBinding
) : BasicHolder(binding.root) {
    private val transparent = binding.root.context.getColor(android.R.color.transparent)

    override fun setData(item: DataItem) {
        val data = item as DataItem.Health
        with(binding) {
            tvTime.text = data.time
            tvPressure.text = String.format(
                root.context.getString(R.string.pressure_format),
                data.highPressure, data.lowPressure
            )
            tvPressure.background = getBackground(data.color)
            tvPulse.text = data.pulse.toString()
        }
    }

    private fun getBackground(color: Int): Drawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
            transparent, color, transparent
        )
    ).apply {
        gradientType = GradientDrawable.LINEAR_GRADIENT
    }
}