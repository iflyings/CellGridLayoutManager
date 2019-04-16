package com.android.iflyings.cellgridlayoutmanager

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DefaultItemAnimator
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val mAllAppsList by lazy { mutableListOf<ResolveInfo>() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
    }

    private fun initData() {
        mAllAppsList.clear()
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val mainList = packageManager.queryIntentActivities(mainIntent, 0) ?: mutableListOf()
        mAllAppsList.addAll(mainList)

        val adapter = CellAdapter(this)
        adapter.setData(mAllAppsList)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = CellGridLayoutManager(3, 4)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(GridItemDecoration(10, 10))
    }

}
