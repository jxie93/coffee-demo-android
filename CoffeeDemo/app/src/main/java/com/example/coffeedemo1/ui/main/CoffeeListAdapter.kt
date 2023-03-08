package com.example.coffeedemo1.ui.main

import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.coffeedemo1.R
import com.example.coffeedemo1.domain.Coffee
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.lang.Integer.max

internal interface CoffeeListAdapterDelegate {
    fun onCoffeeItemClicked(id: String)
    fun onCoffeeItemLikeClicked(id: String)
}

internal class CoffeeListAdapter(
    private val delegate: CoffeeListAdapterDelegate
): RecyclerView.Adapter<CoffeeListViewHolder>() {

    private var data: List<Coffee> = emptyList()

    enum class CoffeeItemViewType {
        NORMAL, PLACEHOLDER
    }

    init {
        setHasStableIds(true)
    }

    fun updateData(data: List<Coffee>, useDiff: Boolean = false) {
        val oldData = this.data
        this.data = data
        if (useDiff) {
            dispatchDiffUpdate(oldData)
        } else {
            notifyDataSetChanged()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(
            if (viewType == CoffeeItemViewType.PLACEHOLDER.ordinal)
                R.layout.item_view_coffee_list_placeholder else
                R.layout.item_view_coffee_list,
            parent,
            false
        )
        return CoffeeListViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoffeeListViewHolder, position: Int) {
        val coffee = data[position]
        if (holder.itemView.tag == coffee.id) return //already bound
        holder.bind(coffee)
    }

    override fun getItemViewType(position: Int): Int {
        val coffee = data[position]
        return if (coffee.isPlaceholder)
            CoffeeItemViewType.PLACEHOLDER.ordinal else
            CoffeeItemViewType.NORMAL.ordinal
    }

    override fun onBindViewHolder(
        holder: CoffeeListViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val coffee = data[position]
        holder.bind(coffee)
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onViewDetachedFromWindow(holder: CoffeeListViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.setOnClickListener(null)
    }

    override fun onViewAttachedToWindow(holder: CoffeeListViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener(
            CoffeeListViewHolder.onClick(holder) {
                delegate.onCoffeeItemClicked(it)
            }
        )
        holder.onLikeClicked = {
            delegate.onCoffeeItemLikeClicked(holder.itemView.tag.toString())
        }
    }

    override fun onViewRecycled(holder: CoffeeListViewHolder) {
        super.onViewRecycled(holder)
        holder.reset()
    }

    override fun getItemCount(): Int = data.size

    private fun dispatchDiffUpdate(oldData: List<Coffee>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldData.size

            override fun getNewListSize(): Int = data.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[newItemPosition].hashCode() == oldData[oldItemPosition].hashCode()
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[newItemPosition].hashCode() == oldData[oldItemPosition].hashCode()
            }
        })
        diffResult.dispatchUpdatesTo(this)
    }
}

internal class CoffeeListViewHolder(view: View): ViewHolder(view) {

    var onLikeClicked: ()->Unit = {}

    companion object {
        fun onClick(holder: ViewHolder, action: (String) -> Unit) = object : OnClickListener{
            val debounceThreshold = 1000L
            private var lastClickTime = 0L
            override fun onClick(p0: View?) {
                val elapsedTime = SystemClock.elapsedRealtime()
                if (elapsedTime - lastClickTime < debounceThreshold) return
                val itemId = holder.itemView.tag.toString()
                action(itemId)
            }
        }
    }

    private val thumbnail = itemView.findViewById<AppCompatImageView?>(R.id.list_item_imageView)
    private val label = itemView.findViewById<AppCompatTextView?>(R.id.list_item_title)
    private val likeBtn = itemView.findViewById<AppCompatImageButton?>(R.id.list_item_likeBtn)

    init {

    }

    fun reset() {
        itemView.tag = null
        label?.text = ""
        thumbnail?.let {
            it.setImageBitmap(null)
            Picasso.get().cancelTag(it)
        }
    }

    fun bind(coffee: Coffee) {
        itemView.tag = coffee.id
        label?.text = coffee.title
        thumbnail?.let {
//            Log.i("COFFEE!", "load ${coffee.image}")
            Picasso.get()
                .load(coffee.image)
                .noFade()
                .noPlaceholder()
                .centerCrop()
                .fit() //some images are unnecessarily big - performance impact
                .tag(it)
                .into(it)
        }
        likeBtn?.let {
            it.setOnClickListener { onLikeClicked() }
            it.setImageResource(
                if (coffee.isLiked)
                    R.drawable.ic_heart_solid else R.drawable.ic_heart
            )
        }
    }

}

internal class CoffeeListItemDecoration(
    @Dimension(unit = Dimension.PX)
    private val spacing: Int,
    private val columns: Int
): ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val items = parent.adapter?.itemCount ?: 0
        val lastRowIndex = max(items - 1, 0) / columns
        val thisRowIndex = position / columns

        if (position >= 0) {
            val column = position % columns

            outRect.left = column * spacing / columns
            outRect.right = spacing - (column + 1) * spacing / columns

            if (position >= columns) {
                outRect.top = spacing
            }
        } else {
            outRect.left = 0
            outRect.right = 0
            outRect.top = 0
            outRect.bottom = 0
        }
        if (thisRowIndex == 0) {
            outRect.top += spacing
        }
        if (thisRowIndex == lastRowIndex) {
            outRect.bottom += spacing
        }
    }
}