package ru.neosvet.health.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import ru.neosvet.health.R
import ru.neosvet.health.databinding.FragmentListBinding
import ru.neosvet.health.list.DataItem
import ru.neosvet.health.list.HealthAdapter
import ru.neosvet.health.utils.FragmentWithMessage
import ru.neosvet.health.utils.Snackbar
import ru.neosvet.health.utils.viewBinding
import ru.neosvet.health.viewmodel.ListIntent
import ru.neosvet.health.viewmodel.ListState
import ru.neosvet.health.viewmodel.ListViewModel

class ListFragment : FragmentWithMessage() {
    private val model: ListViewModel by lazy {
        ViewModelProvider(this).get(ListViewModel::class.java)
    }
    private val binding by viewBinding<FragmentListBinding>()
    private var isNeedUpdateList = true
    private lateinit var anMin: Animation
    private lateinit var anMax: Animation
    private var isHide = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAnimation()
        addDividerInList()
        addHiderFab()
        binding.fabAdd.setOnClickListener {
            isNeedUpdateList = true
            val navController = this.findNavController()
            navController.navigate(R.id.action_nav_list_to_add)
        }
        model.state.observe(requireActivity(), this::changeModelState)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun addHiderFab() {
        binding.rvList.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                binding.fabAdd.clearAnimation()
                isHide = true
                binding.fabAdd.startAnimation(anMin)
            } else if (event.action == MotionEvent.ACTION_UP
                || event.action == MotionEvent.ACTION_CANCEL
            ) {
                binding.fabAdd.clearAnimation()
                isHide = false
                binding.fabAdd.isVisible = true
                binding.fabAdd.startAnimation(anMax)
            }
            return@setOnTouchListener false
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
                setList(state.list)
            }
        }
    }

    private fun setList(list: List<DataItem>) {
        val adapter = HealthAdapter(list)
        binding.rvList.adapter = adapter
    }
}