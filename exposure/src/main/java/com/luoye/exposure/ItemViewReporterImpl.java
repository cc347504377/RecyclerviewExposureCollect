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
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseIntArray;

import com.luoye.exposure.model.ItemViewReporterBase;
import com.luoye.exposure.model.OnExposeCallback;

/**
 * Created by whr on 2018/12/27.
 * RecyclerView Item曝光数据统计
 * 外部实现
 */
class ItemViewReporterImpl extends ItemViewReporterBase {

    ItemViewReporterImpl(@NonNull RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public void reset() {
        templateCheck();
        mHandler.removeCallbacksAndMessages(null);
        mReportData.clear();
        mOldFirstComPt = -1;
        mOldLastComPt = -1;
        mLastResumeTime = 0;
        mLastTouchTime = 0;
    }

    @Override
    public void release() {
        templateCheck();
        mIsRelease = true;
        mRecyclerView.removeOnScrollListener(mScrollListener);
        mHandler.getLooper().quit();
        mHandlerThread.quit();
        mReportData.clear();
        mExposeCallback = null;
        mRecyclerView = null;
    }

    @Override
    public boolean isReleased() {
        return mIsRelease;
    }

    @Override
    public SparseIntArray getData() {
        templateCheck();
        return mReportData;
    }

    @Override
    public void setOnExposeCallback(OnExposeCallback exposeCallback) {
        this.mExposeCallback = exposeCallback;
    }

    @Override
    public void onResume() {
        templateCheck();
        mLastResumeTime = templateTimeCtrl(mLastResumeTime, mIntervalResume, WHAT_RESUME);
    }

    @Override
    public void setResumeInterval(long interval) {
        templateCheck();
        this.mIntervalResume = interval;
    }

    @Override
    public void setTouchInterval(long interval) {
        templateCheck();
        this.mIntervalTouch = interval;
    }
}
