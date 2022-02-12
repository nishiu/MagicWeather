package com.frostsowner.magic.weather.widget;


import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fm.commons.http.ContextHolder;
import com.fm.commons.util.PixelUtils;

public class CityPickItemDecoration extends RecyclerView.ItemDecoration{

    private int offset;

    public CityPickItemDecoration(int value){
        offset = PixelUtils.dp2px(ContextHolder.get(),value);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = offset;
        outRect.bottom = offset;
    }
}