package ru.neosvet.health.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import ru.neosvet.health.R
import ru.neosvet.health.databinding.FragmentListBinding
import ru.neosvet.health.list.DataItem
import ru.neosvet.health.list.HealthAdapter
import ru.neosvet.health.utils.viewBinding
import ru.neosvet.health.viewmodel.ListIntent
import ru.neosvet.health.viewmodel.ListState
import ru.neosvet.health.viewmodel.ListViewModel

class ListFragment : Fragment() {
    private val model: ListViewModel by lazy {
        ViewModelProvider(this).get(ListViewModel::class.java)
    }
    private val binding by viewBinding<FragmentListBinding>()
    private var isNeedUpdateList = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addDividerInList()
        binding.fabAdd.setOnClickListener {
            isNeedUpdateList = true
            val navController = this.findNavController()
            navController.navigate(R.id.action_nav_list_to_add)
        }
        model.state.observe(requireActivity(), this::changeModelState)
    }

    override fun onResume() {
        super.onResume()
        if (isNeedUpdateList.not())
            return
        isNeedUpdateList = false
        lifecycleScope.launch {
            model.userIntent.send(ListIntent.GetList)
        }
    }

    private fun addDividerInList() {
        ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let { divider ->
            val decoration = DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
            decoration.setDrawable(divider)
            binding.rvList.addItemDecoration(decoration)
        }
    }

    private fun changeModelState(state: ListState) {
        when (state) {
            is ListState.Error -> TODO()
            ListState.Loading -> TODO()
            is ListState.Success -> setList(state.list)
        }
    }

    private fun setList(list: List<DataItem>) {
        val adapter = HealthAdapter(list)
        binding.rvList.adapter = adapter
    }
}