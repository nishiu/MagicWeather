package com.frostsowner.magic.weather.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frostsowner.magic.weather.R;

public class SignItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;

    public SignItemDecoration(Context context){
        this.context = context;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemWidth = (int)context.getResources().getDimension(R.dimen.qb_px_42);
        int parentWidth = parent.getWidth();
        int dsw = (parentWidth - itemWidth*7)/6;
        if(position < parent.getAdapter().getItemCount()-1){
            outRect.right = dsw;
        }
    }
}
