package com.frostsowner.magic.weather.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fm.commons.util.PixelUtils;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.domain.AdLiveItem;

public class LiveIndexAdapter extends BaseQuickAdapter<AdLiveItem, BaseViewHolder> {

    public static final int FRAGMENT = 111;
    public static final int ACTIVITY = 222;

    private Context context;
    private int type;
    private String targetDay;

    public LiveIndexAdapter(Context context, int type, int layout_id) {
        super(layout_id);
        this.context = context;
        this.type = type;
    }

    public void setTargetDay(String targetDay){
        this.targetDay = targetDay;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, AdLiveItem adLiveItem){
        updateLayoutWidth(helper);
        helper.setText(R.id.item_content,adLiveItem.getContent());
        helper.setTextColor(R.id.item_content,adLiveItem.getContentColor());
        helper.setText(R.id.item_bottom_label,adLiveItem.getLabel());
    }

    private void updateLayoutWidth(BaseViewHolder helper){
        RelativeLayout rootView = (RelativeLayout)helper.itemView;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)rootView.getLayoutParams();
        layoutParams.width = getItemWidth();
        layoutParams.height = getItemWidth();
        rootView.setLayoutParams(layoutParams);
    }

    private int getItemWidth(){
        int screenW = getWidthHeight()[0] - PixelUtils.dip2px(context,20);
        return (int)(screenW/4f);
    }

    private int[] getWidthHeight(){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return new int[]{width,height};
    }
}
