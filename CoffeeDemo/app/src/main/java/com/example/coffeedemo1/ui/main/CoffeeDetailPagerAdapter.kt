package com.example.coffeedemo1.ui.main

import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.coffeedemo1.R
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.usecase.GetPrettyIngredientsTextUseCase
import com.squareup.picasso.Picasso

internal interface CoffeeDetailPagerAdapterDelegate {
    //TODO
//    fun onCoffeeItemClicked(id: String)
}

internal class CoffeeDetailPagerAdapter(
    private val delegate: CoffeeDetailPagerAdapterDelegate,
    private val getPrettyIngredientsTextUseCase: GetPrettyIngredientsTextUseCase
): RecyclerView.Adapter<CoffeeDetailPagerViewHolder>() {

    companion object {
        private const val TAB_COUNT_MAX = 3
    }

    enum class CoffeeDetailTab {
        DESCRIPTION, INGREDIENTS, REVIEW
    }

    private var data: Coffee? = null

    fun updateData(data: Coffee) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeDetailPagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val tabType = CoffeeDetailTab.values()[viewType]
        val view = inflater.inflate(
            when (tabType) {
                CoffeeDetailTab.DESCRIPTION -> R.layout.layout_coffee_detail_description
                CoffeeDetailTab.INGREDIENTS -> R.layout.layout_coffee_detail_ingredients
                CoffeeDetailTab.REVIEW -> R.layout.layout_coffee_detail_review
            },
            parent,
            false
        )
        Log.i("DEBUG", "onCreateViewHolder $viewType")

        return when (tabType) {
            CoffeeDetailTab.DESCRIPTION ->
                CoffeeDetailPagerDescriptionViewHolder(view)
            CoffeeDetailTab.INGREDIENTS ->
                CoffeeDetailPagerIngredientsViewHolder(view, getPrettyIngredientsTextUseCase)
            CoffeeDetailTab.REVIEW ->
                CoffeeDetailPagerReviewViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: CoffeeDetailPagerViewHolder, position: Int) {
        data?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = TAB_COUNT_MAX
}

internal abstract class CoffeeDetailPagerViewHolder(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(coffee: Coffee)
}

internal class CoffeeDetailPagerDescriptionViewHolder(view: View): CoffeeDetailPagerViewHolder(view) {
    private val label = itemView.findViewById<AppCompatTextView>(R.id.detail_pager_description_label)
    override fun bind(coffee: Coffee) {
        label.text = coffee.description
    }
}

internal class CoffeeDetailPagerIngredientsViewHolder(
    view: View,
    private val getPrettyIngredientsTextUseCase: GetPrettyIngredientsTextUseCase
): CoffeeDetailPagerViewHolder(view) {
    private val label = itemView.findViewById<AppCompatTextView>(R.id.detail_pager_ingredients_label)
    override fun bind(coffee: Coffee) {
        label.text = getPrettyIngredientsTextUseCase(coffee)
    }
}

internal class CoffeeDetailPagerReviewViewHolder(view: View): CoffeeDetailPagerViewHolder(view) {
    override fun bind(coffee: Coffee) {
        //TODO
    }
}