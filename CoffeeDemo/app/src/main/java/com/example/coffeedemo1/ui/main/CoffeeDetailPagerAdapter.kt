package com.example.coffeedemo1.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeedemo1.R
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.domain.Review
import com.example.coffeedemo1.usecase.GetPrettyIngredientsTextUseCase
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.util.*

internal interface CoffeeDetailPagerAdapterDelegate {
    fun onSubmitReview(review: Review)
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

    override fun onViewDetachedFromWindow(holder: CoffeeDetailPagerViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is CoffeeDetailPagerReviewViewHolder) {
            holder.onSubmitClicked = {}
        }
    }

    override fun onViewAttachedToWindow(holder: CoffeeDetailPagerViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is CoffeeDetailPagerReviewViewHolder) {
            holder.onSubmitClicked = { review -> delegate.onSubmitReview(review) }
        }
    }
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

    var onSubmitClicked: (Review)->Unit = {}

    private val nameEditText = itemView.findViewById<AppCompatEditText>(R.id.detail_review_editText_name)
    private val commentEditText = itemView.findViewById<AppCompatEditText>(R.id.detail_review_editText_comment)
    private val datePicker = itemView.findViewById<DatePicker>(R.id.detail_review_datePicker)
    private val ratingLabel = itemView.findViewById<AppCompatTextView>(R.id.detail_review_rating_label)
    private val ratingSlider = itemView.findViewById<Slider>(R.id.detail_review_rating_slider)
    private val submitBtn = itemView.findViewById<AppCompatButton>(R.id.detail_review_submit_btn)

    override fun bind(coffee: Coffee) {
        ratingSlider.updateRatingLabel()
        ratingSlider.addOnChangeListener { slider, value, fromUser ->
            slider.updateRatingLabel()
        }
        datePicker.maxDate = Date().time //max date today
        submitBtn.setOnClickListener {
            onSubmitClicked(createReview())
        }
    }

    private fun DatePicker.getSerializableDate(): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        return dateFormat.format(calendar.time)
    }

    private fun createReview(): Review =
        Review(
            name = nameEditText.text.toString(),
            date = datePicker.getSerializableDate(),
            comment = commentEditText.text.toString(),
            rating = ratingSlider.value.toInt()
        )

    private fun Slider.updateRatingLabel() {
        ratingLabel.text = "${value.toInt()}/${valueTo.toInt()}"
    }
}