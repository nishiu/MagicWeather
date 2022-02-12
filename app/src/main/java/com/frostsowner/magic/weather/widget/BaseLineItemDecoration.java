package com.frostsowner.magic.weather.widget;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseLineItemDecoration extends RecyclerView.ItemDecoration{

    private Rect mRect;
    private int mSpace;
    private int vSpace;
    private Paint paint;

    public BaseLineItemDecoration(int vSpace){
        this.vSpace = vSpace;
        mRect = new Rect();
        paint = new Paint();
        paint.setColor(Color.parseColor("#EFEFEF"));
    }
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + vSpace;
            mRect.set(left,top,right,bottom);
            c.drawRect(mRect,paint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);int position = parent.getChildAdapterPosition(view);
        if(position < parent.getAdapter().getItemCount()-1){
            outRect.bottom = vSpace;
        }
    }
}
