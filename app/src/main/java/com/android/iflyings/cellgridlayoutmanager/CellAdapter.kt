package com.android.iflyings.cellgridlayoutmanager

import android.content.Context
import android.content.pm.ResolveInfo
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

class CellAdapter(context: Context): RecyclerView.Adapter<CellAdapter.ViewHolder>() {

    private val mContext = context
    private val mAppInfoList = mutableListOf<ResolveInfo>()
    private var mOnAppClickListener: OnAppClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = CellView(mContext)

        view.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    Log.i("zw","view.width = " + view.width + ",view.height = " + view.height)
                }
            })

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resolveInfo = mAppInfoList[position]
        /*
        val layoutParams = holder.cellView.layoutParams
        layoutParams.width = viewWidth
        layoutParams.height = viewHeight
        holder.cellView.layoutParams = layoutParams
        */
        holder.cellView.setImageDrawable(resolveInfo.activityInfo.loadIcon(mContext.packageManager))
        holder.cellView.setText(resolveInfo.activityInfo.applicationInfo.loadLabel(mContext.packageManager).toString())

        holder.cellView.setOnClickListener {
            mOnAppClickListener?.onClick(it, resolveInfo)
        }

        holder.cellView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            //获取焦点时变化
            if (hasFocus) {
                ViewCompat.animate(v).scaleX(1.17f).scaleY(1.17f).translationZ(1f).start()
            } else {
                ViewCompat.animate(v).scaleX(1f).scaleY(1f).translationZ(0f).start()
            }
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mAppInfoList.size
    }

    fun setData(list: List<ResolveInfo>) {
        mAppInfoList.clear()
        mAppInfoList.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val cellView: CellView = v as CellView
    }

    interface OnAppClickListener {

        fun onClick(view: View, appInfo: ResolveInfo)

    }
}