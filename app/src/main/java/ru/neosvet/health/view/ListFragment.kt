package ru.neosvet.health.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.neosvet.health.R
import ru.neosvet.health.databinding.FragmentListBinding
import ru.neosvet.health.list.Health
import ru.neosvet.health.utils.viewBinding
import ru.neosvet.health.viewmodel.ListIntent
import ru.neosvet.health.viewmodel.ListState
import ru.neosvet.health.viewmodel.ListViewModel

class ListFragment : Fragment() {
    private val model: ListViewModel by lazy {
        ViewModelProvider(this).get(ListViewModel::class.java)
    }
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
        model.state.observe(requireActivity(), this::changeModelState)
        lifecycleScope.launch {
            model.userIntent.send(ListIntent.GetList)
        }
    }

    private fun changeModelState(state: ListState) {
        when(state) {
            is ListState.Error -> TODO()
            ListState.Loading -> TODO()
            is ListState.Success -> printForTest(state.list)
        }
    }

    private fun printForTest(list: List<Health>) {
        println("Health list:")
        list.forEach {
            println(it)
        }
    }
}