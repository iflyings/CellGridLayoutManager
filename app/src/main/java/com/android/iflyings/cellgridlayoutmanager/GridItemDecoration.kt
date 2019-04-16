package com.android.iflyings.cellgridlayoutmanager

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class GridItemDecoration: RecyclerView.ItemDecoration {
    private var mItemWidthSpan = 0
    private var mItemHeightSpan = 0

    constructor(widthSpan: Int,heightSpan: Int) {
        mItemWidthSpan = widthSpan
        mItemHeightSpan = heightSpan
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        //这里是关键，需要根据你有几列来判断
        if (parent.layoutManager is CellGridLayoutManager) {
            val layoutManager = parent.layoutManager as CellGridLayoutManager
            val numColumns = layoutManager.getItemColumn()
            val numRows = layoutManager.getItemRow()
            var position = parent.getChildAdapterPosition(view)

            val columnIndex = position % numColumns
            val widthSpan = mItemWidthSpan
            outRect.left =
                widthSpan - columnIndex * widthSpan / numColumns // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (columnIndex + 1) * widthSpan / numColumns // (column + 1) * ((1f / spanCount) * spacing)

            position %= (numColumns * numRows)
            val rowIndex = position / numColumns
            val heightSpan = mItemHeightSpan
            outRect.top =
                heightSpan - rowIndex * heightSpan / numRows // spacing - column * ((1f / spanCount) * spacing)
            outRect.bottom = (rowIndex + 1) * heightSpan / numRows // (column + 1) * ((1f / spanCount) * spacing)
        }
    }
}