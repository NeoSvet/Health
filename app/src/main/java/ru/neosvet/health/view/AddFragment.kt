package ru.neosvet.health.view

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.neosvet.health.R
import ru.neosvet.health.databinding.FragmentAddBinding
import ru.neosvet.health.utils.*
import ru.neosvet.health.viewmodel.AddIntent
import ru.neosvet.health.viewmodel.AddState
import ru.neosvet.health.viewmodel.AddViewModel

class AddFragment : FragmentWithMessage() {
    private val model: AddViewModel by viewModel()
    private val binding by viewBinding<FragmentAddBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bar = (requireActivity() as AppCompatActivity).supportActionBar
        bar?.setDisplayHomeAsUpEnabled(true)

        setTimeValue()
        setDateValue()

        with(binding) {
            pTime.setIs24HourView(true)
            btnAdd.isEnabled = false
        }
        setClickListeners()
        setTextWatchers()

        model.state.observe(requireActivity(), this::changeModelState)
    }

    private fun changeModelState(state: AddState) {
        when (state) {
            is AddState.Error -> {
                val msg = String.format(getString(R.string.error_format), state.error)
                finishLoad(msg)
            }
            AddState.Loading -> {
                binding.pbLoad.isInvisible = false
                binding.btnAdd.isInvisible = true
            }
            AddState.Success -> {
                finishLoad(getString(R.string.added))
            }
        }
    }

    private fun finishLoad(msg: String) {
        binding.pbLoad.isInvisible = true
        binding.btnAdd.isInvisible = false
        binding.btnAdd.Snackbar(msg).also {
            message = it
        }.show()
    }

    private fun setTextWatchers() {
        binding.etPulse.doAfterTextChanged(this::textWatcher)
        binding.etHighPressure.doAfterTextChanged(this::textWatcher)
        binding.etLowPressure.doAfterTextChanged(this::textWatcher)
    }

    private fun textWatcher(text: Editable?) = with(binding) {
        btnAdd.isEnabled =
            etPulse.isCorrect() && etHighPressure.isCorrect() && etLowPressure.isCorrect()
    }

    private fun setClickListeners() = with(binding) {
        btnTime.setOnClickListener {
            switchTime()
        }
        btnDate.setOnClickListener {
            switchDate()
        }
        btnAdd.setOnClickListener {
            btnAdd.isEnabled = false
            val item = AddIntent.Add(
                time = btnTime.text.toString(),
                date = btnDate.text.toString(),
                highPressure = etHighPressure.toInt(),
                lowPressure = etLowPressure.toInt(),
                pulse = etPulse.toInt()
            )
            lifecycleScope.launch {
                model.userIntent.send(item)
            }
        }
    }

    private fun switchDate() = with(binding) {
        if (pDate.isInvisible) {
            tilHighPressure.isInvisible = true
            pDate.isInvisible = false
            btnAdd.isInvisible = true
            btnTime.isInvisible = true
            tvTime.isInvisible = true
            btnDate.text = getString(R.string.close)
            changeBtnDateTop(btnAdd.id)
        } else {
            tilHighPressure.isInvisible = false
            pDate.isInvisible = true
            btnAdd.isInvisible = false
            btnTime.isInvisible = false
            tvTime.isInvisible = false
            setDateValue()
            changeBtnDateTop(btnTime.id)
        }
    }

    private fun changeBtnDateTop(id: Int) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            return
        binding.btnDate.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = id
        }
    }

    private fun switchTime() = with(binding) {
        if (pTime.isInvisible) {
            tilHighPressure.isInvisible = true
            pTime.isInvisible = false
            btnAdd.isInvisible = true
            btnDate.isInvisible = true
            tvDate.isInvisible = true
            btnTime.text = getString(R.string.close)
        } else {
            tilHighPressure.isInvisible = false
            pTime.isInvisible = true
            btnAdd.isInvisible = false
            btnDate.isInvisible = false
            tvDate.isInvisible = false
            setTimeValue()
        }
    }

    private fun setTimeValue() = with(binding) {
        btnTime.text = String.format(
            getString(
                R.string.time_format,
                pTime.hour, pTime.minute
            )
        )
    }

    private fun setDateValue() = with(binding) {
        btnDate.text = String.format(
            getString(
                R.string.date_format,
                pDate.dayOfMonth, pDate.month + 1, pDate.year
            )
        )
    }
}