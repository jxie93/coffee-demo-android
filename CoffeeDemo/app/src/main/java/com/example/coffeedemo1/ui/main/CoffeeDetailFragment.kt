package com.example.coffeedemo1.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.coffeedemo1.databinding.FragmentCoffeeDetailBinding
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.usecase.GetPrettyIngredientsTextUseCase
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CoffeeDetailFragment : Fragment(), CoffeeDetailPagerAdapterDelegate {

    companion object {
        private const val ARG_COFFEE_ID = "ARG_COFFEE_ID"
        fun newInstance(id: String) = CoffeeDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_COFFEE_ID, id)
            }
        }
    }

    private lateinit var viewModel: CoffeeDetailViewModel
    private lateinit var binding: FragmentCoffeeDetailBinding

    private lateinit var viewPager: ViewPager2
    private val pagerAdapter by lazy {
        CoffeeDetailPagerAdapter(
            this,
            GetPrettyIngredientsTextUseCase()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CoffeeDetailViewModel::class.java)
        subscribeFlows()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoffeeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.detailViewpager
        setupToolbar()
        setupViewPager()
        viewModel.loadCoffee(arguments?.getString(ARG_COFFEE_ID) ?: "")
    }

    private fun setupToolbar() {
        binding.detailToolbar.apply {
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupViewPager() {
        viewPager.adapter = pagerAdapter

        //tab <-> pager
        binding.detailTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    viewPager.currentItem = it
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit //do nothing
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit //do nothing
        })
    }

    private fun updateCoffeeDetails(coffee: Coffee) {
        binding.detailToolbar.title = coffee.title
        val imageView = binding.detailHeaderImageView
        Picasso.get()
            .load(coffee.image)
            .noFade()
            .noPlaceholder()
            .centerCrop()
            .fit() //some images are unnecessarily big - performance impact
            .tag(imageView)
            .into(imageView)

        //TODO viewpager setup
        pagerAdapter.updateData(coffee)
    }

    private fun subscribeFlows() {
        lifecycleScope.launchWhenResumed {
            viewModel.coffeeEntityFlow.filterNotNull().onEach {
                updateCoffeeDetails(it)
            }.launchIn(this)
        }
    }


}