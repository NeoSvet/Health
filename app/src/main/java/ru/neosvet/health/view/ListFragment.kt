package ru.neosvet.health.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.neosvet.health.R
import ru.neosvet.health.databinding.FragmentListBinding
import ru.neosvet.health.utils.viewBinding

class ListFragment : Fragment() {
    private val binding by viewBinding<FragmentListBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAdd.setOnClickListener {
            val navController = this.findNavController()
            navController.navigate(R.id.action_nav_list_to_add)
        }
    }
}