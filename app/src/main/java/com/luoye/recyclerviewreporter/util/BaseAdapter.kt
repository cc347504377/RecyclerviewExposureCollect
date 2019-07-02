/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * Copyright (c) 2019. WangHhhR
 */

package com.luoye.recyclerviewreporter.util

import android.content.Context
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by whr on 11/2/17.
 * RecyclerView 适配器
 */
class BaseAdapter<T>(
        /**
         * 传入createViewHolder的代码块，需要返回对应的ViewHolder对象
         */
        private inline val createViewHolder: (parent: ViewGroup, viewType: Int) -> androidx.recyclerview.widget.RecyclerView.ViewHolder,
        /**
         * 传入bindView方法的代码块，包含对view具体的数据填充等操作
         */
        private inline val bindView: (data: MutableList<T>,
                                      holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                                      position: Int) -> Unit,
        /**
         * 内存回收等操作
         */
        private inline val recycledView: ((androidx.recyclerview.widget.RecyclerView.ViewHolder) -> Unit)? = null,
        /**
         * 判断itemType
         */
        private inline val getItemType: ((position: Int) -> Int)? = null) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    /**
     * 数据集合，默认为List，类型为Adapter泛型为准
     */
    var data: ArrayList<T> = ArrayList()
    /**
     * holder集合，用于手动释放资源
     */
    val currentHolderList = ArrayList<androidx.recyclerview.widget.RecyclerView.ViewHolder>()

    /**
     * 对外提供简易的view解析方法
     */
    companion object {
        fun getView(parent: ViewGroup, context: Context, @LayoutRes layoutRes: Int) = LayoutInflater.from(context).inflate(layoutRes, parent, false)!!
    }

    override fun getItemViewType(position: Int): Int {
        return getItemType?.invoke(position) ?: super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder =
            createViewHolder.invoke(parent, viewType)

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        currentHolderList.add(holder)
        bindView.invoke(data, holder, position)
    }

    override fun onViewRecycled(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        currentHolderList.remove(holder)
        recycledView?.invoke(holder)
    }

    override fun getItemCount() = data.size

}