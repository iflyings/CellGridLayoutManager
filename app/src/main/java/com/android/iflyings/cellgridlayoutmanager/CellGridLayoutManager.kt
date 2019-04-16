package com.android.iflyings.cellgridlayoutmanager

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class CellGridLayoutManager(itemColumn: Int, itemRow: Int) :
    RecyclerView.LayoutManager() {

    private var mItemColumn = itemColumn
    private var mItemRow = itemRow

    private var mHorizontalScrollOffset = 0
    private var mCurrentPageItem = 0

    private lateinit var mRecyclerView: RecyclerView
    private val mOnPageChangeListeners by lazy { mutableListOf<OnPageChangeListener>() }

    fun getItemColumn(): Int {
        return mItemColumn
    }
    fun getItemRow(): Int {
        return mItemRow
    }
    fun getHorizontalScrollOffset(): Int {
        return mHorizontalScrollOffset
    }
    // 一个页面水平方向显示长度
    private val horizontalSpace
        get() = width - paddingLeft - paddingRight

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(width / mItemColumn, height / mItemRow)
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        mRecyclerView = view
        mRecyclerView.onFlingListener = object: RecyclerView.OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                fixOffsetWhenFinishScroll()
                return true
            }
        }
        //mRecyclerView.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            return
        }
        if (childCount == 0 && state.isPreLayout) {
            return
        }

        layoutItem(recycler)
    }

    private fun getViewFromColumnAndRow(recycler: RecyclerView.Recycler, column: Int, row: Int): View? {
        if (column < 0) return null
        val pageIndex = column / mItemColumn
        val position = pageIndex * mItemColumn * mItemRow + row * mItemColumn + column
        if (position >= itemCount) {
            return null
        }
        return recycler.getViewForPosition(position)
    }

    private fun layoutItem(recycler: RecyclerView.Recycler) {
        //先把所有item从RecyclerView中detach
        detachAndScrapAttachedViews(recycler)

        val drawColumn = Math.round(mHorizontalScrollOffset / (1f * horizontalSpace / mItemColumn))
        for (x in -1 until mItemColumn + 1) {// 添加两边使其能够捕获按键焦点
            for (y in 0 until mItemRow) {
                val scrap = getViewFromColumnAndRow(recycler, drawColumn + x, y) ?: continue
                addView(scrap) // 因为detach过,所以重新添加

                val widthUsed = paddingLeft + 0
                val heightUsed = paddingTop + 0
                measureChildWithMargins(scrap, widthUsed, heightUsed)
                val scrapWidth = scrap.measuredWidth
                val scrapHeight = scrap.measuredHeight

                val leftPos = (drawColumn + x) * scrapWidth - mHorizontalScrollOffset
                val topPos = y * scrapHeight
                val rightPos = leftPos + scrapWidth
                val bottomPos = topPos + scrapHeight
                layoutDecoratedWithMargins(scrap, leftPos, topPos, rightPos, bottomPos)
            }
        }
        mCurrentPageItem = mHorizontalScrollOffset / horizontalSpace

        dispatchOnPageScrolled(mCurrentPageItem, (mHorizontalScrollOffset % horizontalSpace) / 1f * horizontalSpace, mHorizontalScrollOffset)
    }
    // 允许横向滑动
    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val horizontalSpace = width - paddingLeft - paddingRight
        val maxHorizontalSize = ((itemCount - 1) / (mItemColumn * mItemRow) + 1) * horizontalSpace
        //实际要滑动的距离
        var travel = dx
        //如果滑动到最顶部
        if (mHorizontalScrollOffset + dx < 0) {
            travel = -mHorizontalScrollOffset
        } else if (mHorizontalScrollOffset + dx > maxHorizontalSize - horizontalSpace) {//如果滑动到最底部
            travel = maxHorizontalSize - horizontalSpace - mHorizontalScrollOffset
        }
        mHorizontalScrollOffset += travel
        // Log.i("zw","horizontalScrollOffset = $horizontalScrollOffset")
        // 调用该方法通知view在y方向上移动指定距离
        layoutItem(recycler)

        return travel
    }

    // 遥控焦点处理
    override fun requestChildRectangleOnScreen(
        recycler: RecyclerView,
        child: View, rect: Rect, immediate: Boolean,
        focusedChildVisible: Boolean
    ): Boolean {
        val position = recycler.getChildAdapterPosition(child)
        val pageIndex = position / (mItemColumn * mItemRow)
        if (pageIndex == mCurrentPageItem) {
            return true
        }
        val dx = if (pageIndex > mCurrentPageItem) {
            horizontalSpace
        } else {
            -horizontalSpace
        }
        if (immediate) {
            recycler.scrollBy(dx, 0)
        } else {
            recycler.smoothScrollBy(dx, 0)
        }
        return true
    }
    // 滑动状态监听
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                if (mHorizontalScrollOffset % horizontalSpace == 0) {
                    dispatchOnScrollStateChanged(RecyclerView.SCROLL_STATE_IDLE)
                    dispatchOnPageSelected(mCurrentPageItem)
                    return
                }
                fixOffsetWhenFinishScroll()
            }
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                dispatchOnScrollStateChanged(RecyclerView.SCROLL_STATE_DRAGGING)
            }
            RecyclerView.SCROLL_STATE_SETTLING -> {
                dispatchOnScrollStateChanged(RecyclerView.SCROLL_STATE_SETTLING)
            }
        }
    }

    private fun fixOffsetWhenFinishScroll() {
        val endX = if (mHorizontalScrollOffset % horizontalSpace >= horizontalSpace / 2) {
            (mHorizontalScrollOffset / horizontalSpace + 1) * horizontalSpace
        } else {
            (mHorizontalScrollOffset / horizontalSpace) * horizontalSpace
        }
        /*
        mScroller.startScroll(layoutManager.getHorizontalScrollOffset(), 0,
            endX - layoutManager.getHorizontalScrollOffset(), 0 , 2000)
        GlobalScope.launch(Dispatchers.Default) {
            while (mScroller.computeScrollOffset()) {
                GlobalScope.launch(Dispatchers.Main) {
                    //layoutManager.scrollTo(mScroller.currX, mScroller.currY)
                    Log.i("zw","mScroller.currX = ${mScroller.currX}")
                }
                delay(20)
            }
        }
        */
        mRecyclerView.smoothScrollBy(endX - mHorizontalScrollOffset,0)
    }

    private fun dispatchOnPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
        for (listener in mOnPageChangeListeners) {
            listener.onPageScrolled(position, offset, offsetPixels)
        }
    }
    private fun dispatchOnPageSelected(position: Int) {
        for (listener in mOnPageChangeListeners) {
            listener.onPageSelected(position)
        }
    }
    private fun dispatchOnScrollStateChanged(state: Int) {
        for (listener in mOnPageChangeListeners) {
            listener.onPageScrollStateChanged(state)
        }
    }

    interface OnPageChangeListener {

        fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

        fun onPageSelected(position: Int)

        fun onPageScrollStateChanged(state: Int)
    }

}