//package com.example.coffeedemo1.ui.main
//
//import android.content.Context
//import android.util.AttributeSet
//import android.util.Size
//import android.view.View
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.math.MathUtils
//import com.example.coffeedemo1.R
//
//internal open class CoffeeItemConstraintLayout @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null
//) : ConstraintLayout(context, attrs) {
//
//    protected var preferredHeight = -1f
//    private var dimensionRatio = -1f
//
//    override fun onViewAdded(view: View?) {
//        super.onViewAdded(view)
//    }
//
//    init {
//        context.theme.obtainStyledAttributes(
//            attrs,
//            R.styleable.StorytellerStoryItemConstraintLayout,
//            0, 0
//        ).apply {
//            try {
//                dimensionRatio =
//                    getFloat(R.styleable.StorytellerStoryItemConstraintLayout_natural_ratio, -1f)
//                preferredHeight = getDimension(
//                    R.styleable.StorytellerStoryItemConstraintLayout_preferred_height,
//                    resources.getDimension(R.dimen.storyteller_storiesList_preferred_height)
//                )
//            } finally {
//                recycle()
//            }
//        }
//    }
//
//    @Suppress("NAME_SHADOWING")
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val spec = calculateMeasureSpecs(widthMeasureSpec, heightMeasureSpec)
//        super.onMeasure(spec.width, spec.height)
//    }
//
//    protected open fun calculateMeasureSpecs(widthMeasureSpec: Int, heightMeasureSpec: Int): Size {
//        val effectiveHeightMeasureSpec =
//            if (widthMeasureSpec.measureSpecIsUnspecified && heightMeasureSpec.measureSpecIsUnspecified) {
//                MeasureSpec.makeMeasureSpec(preferredHeight.toInt(), MeasureSpec.EXACTLY)
//            } else {
//                heightMeasureSpec
//            }
//
//        var finalWidthSpec = widthMeasureSpec.measureSpecAtMostToExact(minWidth, maxWidth)
//        var finalHeightSpec = effectiveHeightMeasureSpec.measureSpecAtMostToExact(minHeight, maxHeight)
//
//        if (dimensionRatio > -1) { // constrain min/max then dimension ratio
//            if (finalWidthSpec.measureSpecIsUnspecified && finalHeightSpec.measureSpecIsExact) { // calculate width by ratio
//                val finalHeight =
//                    MathUtils.clamp(effectiveHeightMeasureSpec.measureSpecSize, minHeight, maxHeight)
//                finalWidthSpec = makeExactSpec(finalHeight * dimensionRatio)
//            }
//            if (finalHeightSpec.measureSpecIsUnspecified && finalWidthSpec.measureSpecIsExact) { // calculate height by ratio
//                val finalWidth = MathUtils.clamp(widthMeasureSpec.measureSpecSize, minWidth, maxWidth)
//                finalHeightSpec = makeExactSpec(finalWidth / dimensionRatio)
//            }
//        }
//
//        return Size(finalWidthSpec, finalHeightSpec)
//    }
//}
