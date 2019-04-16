package com.android.iflyings.cellgridlayoutmanager

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.text.TextUtils
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout


class CellView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    private val cellImageView: ImageView
    private val cellTextView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.cell_layout, this, true)
        cellImageView = findViewById(R.id.cell_iv)
        cellTextView = findViewById(R.id.cell_tv)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CellView)
        if (attributes != null) {
            val imageViewSrc = attributes.getResourceId(R.styleable.CellView_src, -1)
            if (imageViewSrc > 0) {
                cellImageView.setImageResource(imageViewSrc)
            }

            val imageViewSize = attributes.getDimensionPixelSize(R.styleable.CellView_imageSize, -1)
            if (imageViewSize > 0) {
                val layoutParams = cellImageView.layoutParams
                layoutParams.width = imageViewSize
                layoutParams.height = imageViewSize
                cellImageView.layoutParams = layoutParams
            }

            val textViewString = attributes.getString(R.styleable.CellView_text)
            if (!TextUtils.isEmpty(textViewString)) {
                cellTextView.text = textViewString
            } else {
                cellTextView.visibility = View.GONE
            }

            val textViewSize = attributes.getDimension(R.styleable.CellView_textSize, -1f)
            if (textViewSize > 0f) {
                cellTextView.textSize = textViewSize
            }
        }
        attributes.recycle()
        isFocusable = true
        isClickable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        var childWidth = 0
        var childHeight = 0
        if (MeasureSpec.AT_MOST == widthSpecMode ||
                MeasureSpec.AT_MOST == heightSpecMode) {
            val child = getChildAt(0) as? ConstraintLayout ?: throw IllegalStateException("CellView child 0 is not ConstraintLayout")
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            childWidth = child.measuredWidth
            childHeight = child.measuredHeight
        }

        val width = measureWidth(suggestedMinimumWidth, childWidth, widthMeasureSpec)
        val height = measureHeight(suggestedMinimumHeight, childHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun measureWidth(minWidth: Int, childWidth: Int, measureSpec: Int): Int {
        var defaultWidth = minWidth
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        when (specMode) {
            MeasureSpec.AT_MOST -> {
                defaultWidth = Math.min(childWidth, specSize)
            }
            MeasureSpec.EXACTLY -> {
                defaultWidth = specSize
            }
            MeasureSpec.UNSPECIFIED -> {
                defaultWidth = Math.max(minWidth, specSize)
            }
        }
        return defaultWidth
    }

    private fun measureHeight(miniHeight: Int, childHeight: Int, measureSpec: Int): Int {
        var defaultHeight = miniHeight

        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        when (specMode) {
            MeasureSpec.AT_MOST -> {
                defaultHeight = Math.min(childHeight, specSize)
            }
            MeasureSpec.EXACTLY -> {
                defaultHeight = specSize
            }
            MeasureSpec.UNSPECIFIED -> {
                defaultHeight = 120//Math.max(defaultHeight, specSize)
            }
        }
        return defaultHeight
    }

    fun setImageDrawable(drawable: Drawable) {
        cellImageView.setImageDrawable(drawable)
    }

    fun setImageSize(width: Int, height: Int) {
        val layoutParams = cellImageView.layoutParams
        layoutParams.width = width
        layoutParams.height = height
        cellImageView.layoutParams = layoutParams
    }

    fun setText(text: CharSequence) {
        if (cellTextView.visibility != View.VISIBLE) {
            cellTextView.visibility = View.VISIBLE
        }
        cellTextView.text = text
    }

    fun setTextSize(size: Int) {
        cellTextView.textSize = size.toFloat()
    }
    /*
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.i("zw", "isClickable = $isClickable,isFocusable = $isFocusable")
        return true
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
            MotionEvent.ACTION_MOVE -> {}
            MotionEvent.ACTION_CANCEL -> { }
            else -> {}
        }

        return super.onTouchEvent(event)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (KeyEvent.KEYCODE_DPAD_CENTER == keyCode) {
            performClick()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    */
}