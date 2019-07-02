/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * Copyright (c) 2019. WangHhhR
 */

package com.luoye.recyclerviewreporter

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.luoye.exposure.ItemViewReporterFactory
import com.luoye.exposure.model.ItemViewReporterApi
import com.luoye.recyclerviewreporter.util.BaseAdapter
import com.luoye.recyclerviewreporter.util.bindView
import com.luoye.recyclerviewreporter.util.log
import com.luoye.recyclerviewreporter.util.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    private lateinit var adapter: BaseAdapter<String>
    private lateinit var layoutManager: LinearLayoutManager
    private var itemViewReporter: ItemViewReporterApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initList()
    }

    private fun initView() {
        bt_button.setOnClickListener {
            val sendMsg = itemViewReporter?.data?.toString()
            log(sendMsg)
            toast(sendMsg)
        }
        bt_reset.setOnClickListener {
            itemViewReporter?.reset()
        }
        bt_add.setOnClickListener {
            adapter.data.add("this is item")
            adapter.notifyDataSetChanged()
        }
        bt_remove.setOnClickListener {
            adapter.data.apply {
                if (size > 0) {
                    removeAt(size - 1)
                    adapter.notifyItemRemoved(size)
                }
            }
        }
    }

    private fun initList() {
        adapter = BaseAdapter({ parent, viewType ->
            val view = BaseAdapter.getView(parent, this, R.layout.item_test)
            ViewHolder(view)
        }, { data, holder, position ->
            holder as ViewHolder
            holder.tvItemContent.text = data[position]
        })
        layoutManager = LinearLayoutManager(this)
        rv_main.layoutManager = layoutManager
        rv_main.adapter = adapter
        initListener()
    }

    private fun initListener() {
        itemViewReporter = ItemViewReporterFactory.getItemReporter(rv_main)
        itemViewReporter?.setOnExposeCallback { exposePosition, exposeView ->
            toast(exposePosition.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        itemViewReporter?.onResume()
    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemContent: TextView by bindView(R.id.tv_item_content)
    }
}
