package ru.neosvet.health.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.neosvet.health.R
import ru.neosvet.health.databinding.FragmentAddBinding
import ru.neosvet.health.utils.viewBinding

class AddFragment : Fragment() {
    private val binding by viewBinding<FragmentAddBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }
}