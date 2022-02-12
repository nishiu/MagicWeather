package com.frostsowner.magic.weather.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.widget.SimpleNotice;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.adapter.drag.ItemTouchHelperAdapter;
import com.frostsowner.magic.weather.adapter.drag.OnStartDragListener;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.utils.AddressUtils;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.WeatherResUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class CityMgrAdapter extends BaseQuickAdapter<YunWeather,BaseViewHolder> implements ItemTouchHelperAdapter {

    private boolean isManagerStatus;
    private OnStartDragListener onStartDragListener;
    private List<YunWeather> cacheItems;
    private CompositeSubscription compositeSubscription;
    private SimpleDateManager simpleDateManager;
    private Date today;
    private SimpleDateFormat nightFormat;

    public CityMgrAdapter(Context context,OnStartDragListener onStartDragListener) {
        super(R.layout.item_city_mgr_layout);
        this.onStartDragListener = onStartDragListener;
        compositeSubscription = new CompositeSubscription();
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
        nightFormat = simpleDateManager.getFormatMap("yyyy-MM-dd HH:mm:ss");
        today = simpleDateManager.getDate();
        setManagerStatus(false);
    }

    public void setManagerStatus(boolean isManagerStatus){
        this.isManagerStatus = isManagerStatus;
        if(isManagerStatus){
            cacheItems = new LinkedList<>(getData());
        }
        notifyDataSetChanged();
    }

    public boolean getManagerStatus(){
        return this.isManagerStatus;
    }

    public void resetFromCache(){
        if(this.cacheItems == null)return;
        setNewData(cacheItems);
    }

    public void updateItem(YunWeather cityWeather){
        if(cityWeather == null)return;
        for(int i = 0;i < getData().size();i++){
            YunWeather temp = getItem(i);
            if(temp != null && temp.getCityName().equals(cityWeather.getCityName())){
                notifyItemChanged(i,cityWeather);
                return;
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, YunWeather item) {
        helper.setVisible(R.id.status_a,!isManagerStatus);
        helper.setVisible(R.id.status_b,isManagerStatus);
        helper.addOnClickListener(R.id.status_a);
        updateStatusA(helper,item);
        updateStatusB(helper,item);
    }

    private void updateStatusA(BaseViewHolder holder,YunWeather item){
        if(isManagerStatus)return;
        String detailsAddress = AddressUtils.detailsAddress(item.getAddress(),item.getCity());
        holder.setText(R.id.tv_city_name_a,item.isLocate()?detailsAddress:item.getCityName());
        holder.setVisible(R.id.icon_locate_a,item.isLocate());
        updateWeatherIcon(holder,item);
        updateMaxMinTemperature(holder,item);
    }

    private void updateMaxMinTemperature(BaseViewHolder holder, YunWeather item){
        StringBuilder tempStr = new StringBuilder();
        if(item.getCondition() != null){
            String tempDay = StringUtils.isEmpty(item.getCondition().getTem1())?"--":item.getCondition().getTem1()+"°";
            String tempNight = StringUtils.isEmpty(item.getCondition().getTem2())?"--":item.getCondition().getTem2()+"°";
            tempStr.append(tempDay);
            tempStr.append("/");
            tempStr.append(tempNight);
        }
        holder.setText(R.id.tv_temperature_min_max,tempStr.toString());
    }

    private void updateWeatherIcon(BaseViewHolder holder,YunWeather item){
        ImageView icon = holder.getView(R.id.icon_weather);
        if(item.getCondition() != null){
            String time = nightFormat.format(today);
            if(StringUtils.isNight(time)){
                icon.setBackgroundResource(WeatherResUtils.getYunWeatherNightIcon(item.getCondition().getWea_img()));
            }else{
                icon.setBackgroundResource(WeatherResUtils.getYunWeatherDayIcon(item.getCondition().getWea_img()));
            }
        }
    }

    private void updateStatusB(BaseViewHolder holder,YunWeather item){
        if(!isManagerStatus)return;
        String detailsAddress = AddressUtils.detailsAddress(item.getAddress(),item.getCity());
        holder.setText(R.id.tv_city_name_b,item.isLocate()?detailsAddress:item.getCityName());
        holder.setVisible(R.id.icon_locate_b,item.isLocate());
        holder.getView(R.id.btn_move).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    onStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });
        holder.getView(R.id.btn_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getData().size() == 1){
                    SimpleNotice.show("最少保留一个城市信息");
                    return;
                }
                onItemDismiss(holder.getLayoutPosition());
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(toPosition != 0 && fromPosition != 0){
            Collections.swap(getData(),fromPosition,toPosition);
            notifyItemMoved(fromPosition,toPosition);
        }
//        Subscription subscription = SubscribeUtils.doOnUIThreadDelayed(new Action0() {
//            @Override
//            public void call() {
//                notifyItemRangeChanged(0,getData().size());
//            }
//        },500);
//        compositeSubscription.add(subscription);
        return false;
    }

    @Override
    public void onItemDismiss(int position){
        if(position == 0){
            SimpleNotice.show("本地城市无法删除");
            return;
        }
        getData().remove(position);
        notifyItemRemoved(position);
//        Subscription subscription = SubscribeUtils.doOnUIThreadDelayed(new Action0() {
//            @Override
//            public void call() {
//                notifyDataSetChanged();
//            }
//        },500);
//        compositeSubscription.add(subscription);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(compositeSubscription != null){
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }

    }
}
