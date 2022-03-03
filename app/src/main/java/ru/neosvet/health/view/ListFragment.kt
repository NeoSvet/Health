package ru.neosvet.health.view

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.neosvet.health.R
import ru.neosvet.health.databinding.FragmentListBinding
import ru.neosvet.health.list.HealthAdapter
import ru.neosvet.health.utils.FragmentWithMessage
import ru.neosvet.health.utils.Snackbar
import ru.neosvet.health.utils.SwipeHelper
import ru.neosvet.health.utils.viewBinding
import ru.neosvet.health.viewmodel.ListIntent
import ru.neosvet.health.viewmodel.ListState
import ru.neosvet.health.viewmodel.ListViewModel

class ListFragment : FragmentWithMessage() {
    private val model: ListViewModel by viewModel()
    private val binding by viewBinding<FragmentListBinding>()
    private var isNeedUpdateList = true
    private lateinit var anMin: Animation
    private lateinit var anMax: Animation
    private var isHide = false
    private val adapter = HealthAdapter()
    private lateinit var swipeHelper: SwipeHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAnimation()
        setList()
        binding.fabAdd.setOnClickListener {
            isNeedUpdateList = true
            val navController = this.findNavController()
            navController.navigate(R.id.action_nav_list_to_add)
        }
        model.state.observe(requireActivity(), this::changeModelState)
    }

    private fun setList() {
        addDividerInList()
        addMenuInList()
        addHiderFab()
        binding.rvList.adapter = adapter
    }

    private fun addMenuInList() {
        val btnDelete = SwipeHelper.UnderlayButton(
            image = BitmapFactory.decodeResource(resources, R.mipmap.delete),
            color = MaterialColors.getColor(
                binding.rvList,
                com.google.android.material.R.attr.colorSecondaryVariant
            ),
            onClick = this@ListFragment::deleteItem
        )
        swipeHelper = SwipeHelper(
            context = requireContext(),
            recyclerView = binding.rvList,
            buttons = listOf(btnDelete)
        )
    }

    private fun deleteItem(index: Int) {
        val deleteIntent = ListIntent.Delete(
            id = adapter.getIdBy(index)
        )
        adapter.remove(index)
        lifecycleScope.launch {
            model.userIntent.send(deleteIntent)
        }
    }

    private fun initAnimation() {
        anMin = AnimationUtils.loadAnimation(requireContext(), R.anim.minimize)
        anMin.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (isHide)
                    binding.fabAdd.isVisible = false
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        anMax = AnimationUtils.loadAnimation(requireContext(), R.anim.maximize)
    }

    private fun addHiderFab() {
        swipeHelper.touchEvent = { action ->
            if (action == MotionEvent.ACTION_DOWN) {
                binding.fabAdd.clearAnimation()
                isHide = true
                binding.fabAdd.startAnimation(anMin)
            } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL
            ) {
                binding.fabAdd.clearAnimation()
                isHide = false
                binding.fabAdd.isVisible = true
                binding.fabAdd.startAnimation(anMax)
            }
        }
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
            is ListState.Error -> {
                binding.pbLoad.isVisible = false
                binding.fabAdd.Snackbar(
                    String.format(
                        getString(R.string.error_format),
                        state.error
                    )
                ).also {
                    message = it
                }.show()
            }
            ListState.Loading -> {
                binding.rvList.isVisible = false
                binding.pbLoad.isVisible = true
            }
            is ListState.Success -> {
                binding.rvList.isVisible = true
                binding.pbLoad.isVisible = false
                adapter.addItems(state.list)
            }
        }
    }
}