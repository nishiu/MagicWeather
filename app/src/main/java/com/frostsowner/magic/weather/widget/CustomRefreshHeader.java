package com.frostsowner.magic.weather.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.frostsowner.magic.weather.impl.RefreshStatusListener;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

public class CustomRefreshHeader extends LinearLayout implements RefreshHeader {

    private ProgressBar progressBar;
    private TextView textView;
    private RefreshStatusListener statusListener;

    public CustomRefreshHeader(Context context,RefreshStatusListener statusListener){
        super(context);
        this.statusListener = statusListener;
        initViews();
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews(){
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_refresh_header,null);
//        progressBar = view.findViewById(R.id.progress_bar);
//        textView = view.findViewById(R.id.tv_progress);
//        addView(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
//        setMinimumHeight(PixelUtils.dp2px(getContext(),60));
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//        if(progressBar != null)progressBar.setVisibility(VISIBLE);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
//        if(success){
//            textView.setText("刷新成功");
//        }else{
//            textView.setText("刷新失败");
//        }
        if(success){
            statusListener.refresh(RefreshStatusListener.REFRESH_SUCCESS);
        }else{
            statusListener.refresh(RefreshStatusListener.REFRESH_FAILED);
        }
        return 500;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState){
            case None:
            case PullDownToRefresh:
//                progressBar.setVisibility(INVISIBLE);
//                textView.setText("下拉刷新");
                break;
            case Refreshing:
//                progressBar.setVisibility(VISIBLE);
//                textView.setText("正在刷新中...");
                statusListener.refresh(RefreshStatusListener.REFRESHING);
                break;
        }
    }
}
