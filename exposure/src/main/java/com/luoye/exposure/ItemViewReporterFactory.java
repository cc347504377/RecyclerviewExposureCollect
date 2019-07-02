/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * Copyright (c) 2019. WangHhhR
 */

package com.luoye.exposure;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luoye.exposure.model.ItemViewReporterApi;

/**
 * Created by whr on 2018/12/26.
 * RecyclerView Item曝光数据统计
 * 工厂类
 */
public class ItemViewReporterFactory {

    private ItemViewReporterFactory() {
    }

    @NonNull
    public static ItemViewReporterApi getItemReporter(RecyclerView recyclerView) throws IllegalArgumentException {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            return new ItemViewReporterImpl(recyclerView);
        }
        throw new IllegalArgumentException("LayoutManager must be LinearLayoutManager");
    }
}