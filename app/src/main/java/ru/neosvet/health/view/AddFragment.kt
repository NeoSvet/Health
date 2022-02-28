package ru.neosvet.health.view

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import ru.neosvet.health.R
import ru.neosvet.health.databinding.FragmentAddBinding
import ru.neosvet.health.utils.isCorrect
import ru.neosvet.health.utils.viewBinding
import ru.neosvet.health.viewmodel.AddIntent
import ru.neosvet.health.viewmodel.AddState
import ru.neosvet.health.viewmodel.AddViewModel
import ru.neosvet.health.utils.toInt

class AddFragment : Fragment() {
    private val model: AddViewModel by lazy {
        ViewModelProvider(this).get(AddViewModel::class.java)
    }
    private val binding by viewBinding<FragmentAddBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            is AddState.Error -> TODO()
            AddState.Loading -> TODO()
            AddState.Success ->
                Snackbar.make(binding.btnAdd, getString(R.string.added), Snackbar.LENGTH_SHORT)
                    .show()
        }
    }

    private fun setTextWatchers() {
        binding.etPulse.doAfterTextChanged(this::textWatcher)
        binding.etHighPressure.doAfterTextChanged(this::textWatcher)
        binding.etLowPressure.doAfterTextChanged(this::textWatcher)
    }

    private fun textWatcher(text: Editable?) = with(binding) {
        btnAdd.isEnabled = etPulse.isCorrect() && etHighPressure.isCorrect() && etLowPressure.isCorrect()
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