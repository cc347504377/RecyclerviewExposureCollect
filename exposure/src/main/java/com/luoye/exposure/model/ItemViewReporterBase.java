/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * Copyright (c) 2019. WangHhhR
 */

package com.luoye.exposure.model;

import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by whr on 2018/12/25.
 * RecyclerView Item曝光数据统计
 * 内部实现
 */
public abstract class ItemViewReporterBase implements ItemViewReporterApi {

    private static final int WHAT_TOUCH = 0;
    protected static final int WHAT_RESUME = 1;

    private LinearLayoutManager mLayoutManager;
    protected RecyclerView mRecyclerView;
    protected MyScrollListener mScrollListener;
    protected SparseIntArray mReportData;
    protected MyHandler mHandler;
    protected long mIntervalResume = 500L;
    protected long mIntervalTouch = 500L;
    protected long mLastResumeTime;
    protected long mLastTouchTime;
    protected int mOldFirstComPt = -1;
    protected int mOldLastComPt = -1;
    protected boolean mIsRelease = false;
    protected HandlerThread mHandlerThread;
    protected OnExposeCallback mExposeCallback;

    private void recordResume() {
        Point rangePosition = findRangePosition();
        if (rangePosition == null) {
            return;
        }
        int firstComPosition = rangePosition.x;
        int lastComPosition = rangePosition.y;
        if (mExposeCallback != null) {
            List<Integer> positionList = new ArrayList<>();
            List<View> viewList = new ArrayList<>();
            for (int i = firstComPosition; i <= lastComPosition; i++) {
                templateAddData(i, positionList, viewList);
            }
            mExposeCallback.onExpose(positionList, viewList);
        } else {
            for (int i = firstComPosition; i <= lastComPosition; i++) {
                templateAddData(i, null, null);
            }
        }
    }

    private void recordTouch() {
        Point rangePosition = findRangePosition();
        if (rangePosition == null) {
            return;
        }
        int firstComPosition = rangePosition.x;
        int lastComPosition = rangePosition.y;
        if (firstComPosition == mOldFirstComPt && lastComPosition == mOldLastComPt) {
            return;
        }
        List<Integer> positionList = new ArrayList<>();
        List<View> viewList = new ArrayList<>();
        //首次&不包含相同项
        if (mOldLastComPt == -1 || firstComPosition > mOldLastComPt || lastComPosition < mOldFirstComPt) {
            for (int i = firstComPosition; i <= lastComPosition; i++) {
                templateAddData(i, positionList, viewList);
            }
        } else {
            //排除相同项
            if (firstComPosition < mOldFirstComPt) {
                for (int i = firstComPosition; i < mOldFirstComPt; i++) {
                    templateAddData(i, positionList, viewList);
                }
            }
            if (lastComPosition > mOldLastComPt) {
                for (int i = mOldLastComPt + 1; i <= lastComPosition; i++) {
                    templateAddData(i, positionList, viewList);
                }
            }
        }
        if (mExposeCallback != null) {
            mExposeCallback.onExpose(positionList, viewList);
        }
        mOldFirstComPt = firstComPosition;
        mOldLastComPt = lastComPosition;
    }

    private void templateAddData(int position, List<Integer> positionList, List<View> viewList) {
        View positionView = null;
        try {
            positionView = mLayoutManager.findViewByPosition(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == positionView) {
            return;
        }
        if (positionView.getVisibility() == View.GONE) {
            return;
        }
        int count = mReportData.get(position);
        mReportData.put(position, count + 1);
        if (null != positionList && null != viewList) {
            positionList.add(position);
            viewList.add(positionView);
        }
    }

    private Point findRangePosition() {
        int firstComPosition = -1;
        int lastComPosition = -1;
        try {
            firstComPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
            lastComPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstComPosition == -1) {
            return null;
        } else {
            return new Point(firstComPosition, lastComPosition);
        }
    }

    public ItemViewReporterBase(@NonNull RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        init();
    }

    private void init() {
        mScrollListener = new MyScrollListener();
        mRecyclerView.addOnScrollListener(mScrollListener);
        mReportData = new SparseIntArray();
        mHandlerThread = new HandlerThread("ItemViewReporterSub");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());
    }

    private void onView() {
        mLastTouchTime = templateTimeCtrl(mLastTouchTime, mIntervalTouch, WHAT_TOUCH);
    }

    /**
     * 模板代码
     * 控制曝光记录间隔
     *
     * @param lastTime 上次曝光时间
     * @param interval 间隔时间
     * @param what     对应事件
     * @return 此次曝光时间
     */
    protected long templateTimeCtrl(long lastTime, long interval, int what) {
        if (SystemClock.elapsedRealtime() - lastTime < interval) {
            mHandler.removeMessages(what);
        }
        mHandler.sendEmptyMessageDelayed(what, interval);
        return SystemClock.elapsedRealtime();
    }

    /**
     * 模板代码
     * 统一处理非法调用
     */
    protected void templateCheck() {
        if (mIsRelease) {
            throw new RuntimeException("this is released");
        }
    }

    protected class MyHandler extends Handler {

        private MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_TOUCH:
                    recordTouch();
                    break;
                case WHAT_RESUME:
                    recordResume();
                    break;
            }
        }
    }

    private class MyScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            /**
             * newState
             * 0：完全停止滚动
             * 1: 手指点击
             * 2：惯性滑动中
             */
            if (newState == 0) {
                onView();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }
}