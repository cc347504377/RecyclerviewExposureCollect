/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * Copyright (c) 2019. WangHhhR
 */

package com.luoye.exposure.model;

import android.view.View;

import java.util.List;

/**
 * Created by whr on 2018/12/27.
 */

public interface OnExposeCallback {

    void onExpose(List<Integer> exposePosition, List<View> exposeView);

}
