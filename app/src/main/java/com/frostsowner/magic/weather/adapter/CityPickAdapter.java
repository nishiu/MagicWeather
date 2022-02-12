package com.frostsowner.magic.weather.adapter;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fm.commons.http.ContextHolder;
import com.frostsowner.magic.weather.R;

public class CityPickAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public static final int MODEL_FULL = 111;
    public static final int MODEL_SHORT = 110;

    private int model;
    private int size;

    public CityPickAdapter(int model,int size){
        super(R.layout.item_city_pick_1);
        this.model = model;
        this.size = size;
    }

    public CityPickAdapter(int size){
        this(MODEL_SHORT,size);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if(model == MODEL_SHORT){
            item = item.replace("市","");
            item = item.replace("省","");
        }
        TextView tvCity = helper.getView(R.id.c_item_name_tv);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)tvCity.getLayoutParams();
        layoutParams.width = getWidth();
        tvCity.setLayoutParams(layoutParams);
        helper.setText(R.id.c_item_name_tv,item);
        helper.addOnClickListener(R.id.item_root);
    }

    private int getWidth(){
        int screenWidth = ContextHolder.get().getResources().getDisplayMetrics().widthPixels;
        int margin = (int)ContextHolder.get().getResources().getDimension(R.dimen.qb_px_16);
        int drive = (int)ContextHolder.get().getResources().getDimension(R.dimen.qb_px_10);
        screenWidth -= (margin*2+drive*3);
        return screenWidth/size;
    }
}
