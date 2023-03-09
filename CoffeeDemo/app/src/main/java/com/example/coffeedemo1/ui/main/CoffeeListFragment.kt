package com.example.coffeedemo1.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeedemo1.R
import com.example.coffeedemo1.databinding.FragmentCoffeeListBinding
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.services.api.CoffeeError
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CoffeeListFragment : Fragment(), CoffeeListAdapterDelegate {

    companion object {
        fun newInstance() = CoffeeListFragment()

        private const val GRID_COLUMN_MAX = 2
    }

    private lateinit var viewModel: CoffeeListViewModel
    private lateinit var binding: FragmentCoffeeListBinding
    private val coffeeListAdapter by lazy { CoffeeListAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CoffeeListViewModel::class.java)
        subscribeFlows()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoffeeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listRefreshLayout.setOnRefreshListener { reloadCoffeeList() }

        setupLists()
    }

    private fun updateBanner(data: List<Coffee>) {
        if (data.isEmpty()) return
        val banner = binding.adBannerImageView
        Picasso.get()
            .load(data.random().image)
            .fit()
            .centerCrop()
            .tag(banner)
            .into(banner)
    }

    private fun reloadCoffeeList() {
        val refreshLayout = binding.listRefreshLayout
        lifecycleScope.launch {
            refreshLayout.isRefreshing = true
            delay(100) //prevent stuck in forever loading
            refreshLayout.isRefreshing = false
        }
        viewModel.reloadForCurrentCategory()
    }

    override fun onCoffeeItemClicked(id: String) {
        Log.i("COFFEE!", "onCoffeeItemClicked -> $id")
        val item = viewModel.displayDataFlow.value.firstOrNull { it.id == id }
        if (item == null || item.isPlaceholder) return
        parentFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                com.google.android.material.R.anim.m3_bottom_sheet_slide_in,
                com.google.android.material.R.anim.m3_bottom_sheet_slide_in,
                com.google.android.material.R.anim.m3_bottom_sheet_slide_out,
                com.google.android.material.R.anim.m3_bottom_sheet_slide_out,
            )
            .addToBackStack(CoffeeDetailFragment::class.java.name)
            .add(R.id.container, CoffeeDetailFragment.newInstance(id))
            .commit()
    }

    override fun onCoffeeItemLikeClicked(id: String) {
        Log.i("COFFEE!", "onCoffeeItemLikeClicked -> $id")
        viewModel.likeOrUnlikeCoffee(id)
    }

    private fun setupLists() {
        //segmented control
        val btnGroup = binding.listSegmentedControl.listSegmentedControlGroup
        btnGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            viewModel.changeDisplayCategory(
                when(checkedId) {
                    R.id.list_segmentedControl_hot_btn -> CoffeeListViewModel.DisplayCategory.HOT
                    R.id.list_segmentedControl_liked_btn -> CoffeeListViewModel.DisplayCategory.LIKED
                    else -> throw CoffeeError.InvalidCoffeeCategory()
                }
            )
        }

        //recyclerview
        binding.listRecyclerView.apply {
            layoutManager = GridLayoutManager(requireActivity(), GRID_COLUMN_MAX)
            adapter = coffeeListAdapter
            updateItemDecoration()
        }
        coffeeListAdapter.updateData(Coffee.placeholders)
    }

    private fun RecyclerView.updateItemDecoration() {
        if (itemDecorationCount > 0) {
            (0 until itemDecorationCount).forEach { index ->
                removeItemDecoration(getItemDecorationAt(index))
            }
        }
        addItemDecoration(
            CoffeeListItemDecoration(
                resources.getDimensionPixelSize(R.dimen.item_card_spacing),
                GRID_COLUMN_MAX
            )
        )
    }

    private fun updateSegmentedControl(category: CoffeeListViewModel.DisplayCategory) {
        val hotBtn = binding.listSegmentedControl.listSegmentedControlHotBtn
        val likedBtn = binding.listSegmentedControl.listSegmentedControlLikedBtn
        hotBtn.isChecked = false
        likedBtn.isChecked = false
        when(category) {
            CoffeeListViewModel.DisplayCategory.HOT -> hotBtn.isChecked = true
            CoffeeListViewModel.DisplayCategory.LIKED -> likedBtn.isChecked = true
        }
    }

    private fun onDisplayDataUpdate(data: List<Coffee>) {
        coffeeListAdapter.updateData(data.ifEmpty { Coffee.placeholders }, true)
    }

    private fun subscribeFlows() {
        lifecycleScope.launchWhenResumed {
            viewModel.displayDataFlow.onEach {
                Log.i("COFFEE!", "coffeeDataFlow received -> $it")
                onDisplayDataUpdate(it)
            }.launchIn(this)
            viewModel.displayCategory.onEach {
                Log.i("COFFEE!", "displayCategory -> $it")
                updateBanner(viewModel.displayDataFlow.value)
                updateSegmentedControl(it)
            }.launchIn(this)
            viewModel.isLoading.onEach {
                binding.listRefreshLayout.isRefreshing = it
            }.launchIn(this)
        }
    }

}