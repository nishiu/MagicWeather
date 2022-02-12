package com.frostsowner.magic.weather.fragment;

import static com.frostsowner.magic.weather.Constant.DRAWING_UPDATE_OVERTIME;
import static com.frostsowner.magic.weather.event.EventType.WEATHER_REQUEST_SUCCESS;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.fm.commons.http.ContextHolder;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.activity.ConditionDetailsActivity;
import com.frostsowner.magic.weather.adapter.CityWeatherAdapter;
import com.frostsowner.magic.weather.base.BaseFragment;
import com.frostsowner.magic.weather.domain.AqiItem;
import com.frostsowner.magic.weather.domain.BaseWeather;
import com.frostsowner.magic.weather.domain.ConditionItem;
import com.frostsowner.magic.weather.domain.ForecastItem;
import com.frostsowner.magic.weather.domain.HourlyItem;
import com.frostsowner.magic.weather.domain.LiveIndexItem;
import com.frostsowner.magic.weather.domain.TopAlertItem;
import com.frostsowner.magic.weather.domain.TwoDaysItem;
import com.frostsowner.magic.weather.domain.weather.YunCondition;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.event.EventType;
import com.frostsowner.magic.weather.impl.AdapterNotifyListener;
import com.frostsowner.magic.weather.impl.RefreshStatusListener;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.logic.TTSManager;
import com.frostsowner.magic.weather.logic.WaveHelper;
import com.frostsowner.magic.weather.logic.WeatherItemExposure;
import com.frostsowner.magic.weather.logic.WeatherRefreshHandler;
import com.frostsowner.magic.weather.service.WeatherService;
import com.frostsowner.magic.weather.utils.ConnectionUtils;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.SubscribeUtils;
import com.frostsowner.magic.weather.utils.WeatherUtils;
import com.frostsowner.magic.weather.view.SpeakHolder;
import com.frostsowner.magic.weather.view.WaveView;
import com.frostsowner.magic.weather.widget.CustomRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import me.xfans.lib.voicewaveview.VoiceWaveView;
import me.xfans.lib.voicewaveview.WaveMode;
import rx.Subscription;
import rx.functions.Action0;

public class CityWeatherFragment extends BaseFragment implements OnRefreshListener, AdapterNotifyListener , RefreshStatusListener {

    public static CityWeatherFragment newInstance(String cityName){
        Bundle args = new Bundle();
        args.putString("cityName",cityName);
        CityWeatherFragment fragment = new CityWeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private final long UPDATE_DELAY = 1000*60*30;

    @BindView(R.id.swipe_container)
    SmartRefreshLayout swipeRefreshLayout;
    @BindView(R.id.weather_recyclerview)
    RecyclerView weatherRecyclerView;
    @BindView(R.id.background_view)
    View backgroundView;
//    @BindView(R.id.tv_alert)
//    TextView tvAlert;
    @BindView(R.id.condition_root_view)
    View conditionRootView;
    @BindView(R.id.weather_square_bg)
    WaveView weatherSquareBg;

    private String cityName;
    private CityManager cityManager;
    private SimpleDateManager simpleDateManager;
    private CityWeatherAdapter cityWeatherAdapter;
    private YunWeather cityWeather;
    private TTSManager ttsManager;
    private LocalStorage localStorage;
    private WeatherItemExposure weatherItemExposure;
    private long updateTime;
    private boolean isSpeaking;
    private boolean forceLocation;
    private TopAlertItem topAlertItem;
    private ConditionItem conditionItem;
    private SpeakHolder speakHolder;
    private WeatherRefreshHandler weatherRefreshHandler;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            cityName = bundle.getString("cityName");
            logError("bundle cityName : "+cityName);
        }
    }

    @Override
    protected void initWidget(View root){
        super.initWidget(root);
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
        cityManager = BeanFactory.getBean(CityManager.class);
        weatherItemExposure = BeanFactory.getBean(WeatherItemExposure.class);
        localStorage = BeanFactory.getBean(LocalStorage.class);
        weatherRefreshHandler = BeanFactory.getBean(WeatherRefreshHandler.class);
        weatherItemExposure.init();
        cityWeatherAdapter = new CityWeatherAdapter(getActivity());
        cityWeatherAdapter.setAdapterNotifyListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        weatherRecyclerView.setLayoutManager(layoutManager);
        weatherRecyclerView.setAdapter(cityWeatherAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext(),this));
        swipeRefreshLayout.setEnableLoadMore(false);
        initTTS();
    }

    private void updateUI(boolean force){
        if(force || cityWeather == null){
            cityWeather = cityManager.getTarget(cityName);
            if(cityWeather != null && cityWeather.getCondition()!=null&&cityWeather.getForecast()!=null){
                List<MultiItemEntity> data = new LinkedList<>();
                conditionItem = createConditionItem(cityWeather);
                topAlertItem = createTopAlertItem(cityWeather);
//                data.add(createTwoDayItem(cityWeather));
                data.add(createHourlyItem(cityWeather));
                data.add(createForecastItem(cityWeather));
                data.add(createAqiItem(cityWeather));
                data.add(createLiveIndexItem(cityWeather));
                cityWeatherAdapter.setNewData(data);
//                updateTopAlert(topAlertItem);
                updateCondition(conditionItem);
                updateSquare();
                updateTime = System.currentTimeMillis();
                logInfo("updateUI force");
            }
        }else{
            cityWeatherAdapter.notifyDataSetChanged();
//            updateTopAlert(topAlertItem);
            updateCondition(conditionItem);
            updateSquare();
            logInfo("updateUI not force");
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout){
        if(!ConnectionUtils.isConnectionAvailable(getContext())){
            showNotice("网络断开,请检查网络是否正确连接");
            return;
        }
        boolean showFakeData = weatherRefreshHandler.showFakeData(cityName);
        if(showFakeData){
            SubscribeUtils.doOnUIThreadDelayed(() -> {
                ActionEvent actionEvent = new ActionEvent(WEATHER_REQUEST_SUCCESS);
                actionEvent.setAttr("cityName",cityName);
                actionEvent.setAttr("fake","fake");
                fire(actionEvent);
            },500);
            logInfo("city weather fragment show fake data");
        }else{
            Intent intent = new Intent(getContext(), WeatherService.class);
            intent.putExtra("cityName",cityName);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                getContext().startForegroundService(intent);
            }else{
                getContext().startService(intent);
            }
        }
        fire(new ActionEvent(EventType.WEATHER_REFRESHING));
    }

    @Override
    public void onNotify(){
        logError("cityWeatherFragment notify , cityName : "+cityName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataUpdate(ActionEvent event){
        if(event.getType() == WEATHER_REQUEST_SUCCESS){
            String cityName = event.getAttrStr("cityName");
            String fake = event.getAttrStr("fake");
            String reset = event.getAttrStr("reset");
            logInfo("CityWeatherFragment, cityName : "+cityName+" , fake : "+fake+" , reset : "+reset);
            YunWeather target = cityManager.getTarget(cityName);
            if(cityWeather!=null && cityWeather.isLocate() && target != null && target.isLocate()){
                this.cityName = target.getCityName();
            }
            int currentPosition = cityManager.getTargetIndex(this.cityName);
            int targetPosition = cityManager.getCurrentCityIndex();
            if(currentPosition != targetPosition)return;
            updateUI(StringUtils.isEmpty(fake));
            swipeRefreshLayout.finishRefresh(true);
        }
        else if(event.getType() == EventType.CITY_WEATHER_SPEAK){
            String targetCityName = event.getAttrStr("cityName");
            int currentPosition = cityManager.getTargetIndex(this.cityName);
            int targetPosition = cityManager.getTargetIndex(targetCityName);
            if(currentPosition == targetPosition)return;
            logInfo("city weather speak , current cityName "+cityName+" , target cityName : "+targetCityName);
            isSpeaking = false;
            if(ttsManager != null && ttsManager.isInit()){
                ttsManager.stop();
            }
            speakStatus(false);
        }
        else if(event.getType() == EventType.LOCATION_START){
            int currentPosition = cityManager.getTargetIndex(this.cityName);
            if(currentPosition == 0){
                forceLocation = true;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if((cityWeather!= null && cityWeather.isLocate() && System.currentTimeMillis() - updateTime >= UPDATE_DELAY) ||
            updateTime == 0L || forceLocation){ // || !cityFakeRefresh
            swipeRefreshLayout.autoRefresh();
            addSubscription(SubscribeUtils.doOnUIThreadDelayed(new Action0(){
                @Override
                public void call(){
                    if(!swipeRefreshLayout.getState().isFinishing){
                        swipeRefreshLayout.finishRefresh(false);
                    }
                }
            }, DRAWING_UPDATE_OVERTIME));
            forceLocation = false;
        }
        initTTS();
        updateUI(false);
        logInfo("onResume , cityName : "+cityName);
    }

    @Override
    public void refresh(int status){

    }

    private void updateTopAlert(TopAlertItem topAlertItem){
//        if(topAlertItem.getYunAlert() != null && !StringUtils.isEmpty(topAlertItem.getYunAlert().getAlarm_content())){
//            tvAlert.setVisibility(View.VISIBLE);
//            tvAlert.setText(topAlertItem.getYunAlert().getAlarm_content());
//            tvAlert.setSelected(true);
//            tvAlert.setOnClickListener(v -> {
////                Intent intent = new Intent();
////                intent.setClass(activity, WeatherAlertActivity.class);
////                activity.startActivity(intent);
//            });
//        }else{
//            tvAlert.setVisibility(View.INVISIBLE);
//        }
    }

    private void updateCondition(ConditionItem conditionItem){
        YunCondition yunCondition = conditionItem.getYunCondition();
        conditionRootView.findViewById(R.id.btn_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSpeak();
            }
        });
        if(yunCondition != null){
            conditionRootView.setVisibility(View.VISIBLE);
        }else{
            conditionRootView.setVisibility(View.INVISIBLE);
            return;
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)conditionRootView.getLayoutParams();
        layoutParams.height = (int)getActivity().getResources().getDimension(R.dimen.qb_px_200);
        conditionRootView.setLayoutParams(layoutParams);
        conditionRootView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ConditionDetailsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        TextView tvWeather = conditionRootView.findViewById(R.id.tv_weather);
        tvWeather.setText(yunCondition.getWea());

        TextView tvTemperature = conditionRootView.findViewById(R.id.tv_temperature);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/pf_tc_tiny.ttf");
        tvTemperature.setTypeface(typeface);
        tvTemperature.setText(yunCondition.getTem()+"°");

        TextView tvWind = conditionRootView.findViewById(R.id.tv_wind);
        tvWind.setText(yunCondition.getWin()+yunCondition.getWin_speed());

        TextView tvHumidity = conditionRootView.findViewById(R.id.tv_humidity);
        tvHumidity.setText("湿度"+yunCondition.getHumidity());

        TextView tvTips = conditionRootView.findViewById(R.id.tv_tips);
        tvTips.setText(yunCondition.getAir_tips());
        tvTips.setSelected(true);

        TextView tvPreesure = conditionRootView.findViewById(R.id.tv_pressure);
        tvPreesure.setText("气压"+yunCondition.getPressure()+"hPa");

        View groupAqi = conditionRootView.findViewById(R.id.group_aqi);
        TextView tvAqi = conditionRootView.findViewById(R.id.tv_aqi);
        ImageView imgAqi = conditionRootView.findViewById(R.id.img_aqi);
        if(!StringUtils.isEmpty(yunCondition.getAir())){
            groupAqi.setVisibility(View.VISIBLE);
            String value = yunCondition.getAir();
            String quality = WeatherUtils.getAqiValue(yunCondition.getAir());
            tvAqi.setText(quality+" "+value);
            if(quality.equals("优")){
                imgAqi.setColorFilter(getActivity().getResources().getColor(R.color.color_quality_1));
            }
            else if(quality.equals("良")){
                imgAqi.setColorFilter(getActivity().getResources().getColor(R.color.color_quality_2));
            }
            else if(quality.equals("轻度")){
                imgAqi.setColorFilter(getActivity().getResources().getColor(R.color.color_quality_3));
            }
            else if(quality.equals("中度")){
                imgAqi.setColorFilter(getActivity().getResources().getColor(R.color.color_quality_4));
            }
            else if(quality.equals("重度") || quality.equals("严重")){
                imgAqi.setColorFilter(getActivity().getResources().getColor(R.color.color_quality_5));
            }
//            groupAqi.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(activity, AqiDetailsActivity.class);
//                    activity.startActivity(intent);
//                }
//            });
        }else{
            groupAqi.setVisibility(View.INVISIBLE);
        }
        VoiceWaveView voiceWaveView = conditionRootView.findViewById(R.id.voiceWaveView0);
        ImageView voiceClose = conditionRootView.findViewById(R.id.voice_close);
        setCurrentVoiceView(voiceWaveView,voiceClose);
    }

    private void updateSquare(){
        weatherSquareBg.setShapeType(WaveView.ShapeType.SQUARE);
        weatherSquareBg.setWaveColor(Color.parseColor("#00ffffff"),
                Color.parseColor("#40ffffff"),
                Color.parseColor("#ffffffff"));
        WaveHelper waveHelper = new WaveHelper(weatherSquareBg);
        waveHelper.start();
    }

    private void initTTS(){
        if(ttsManager == null){
            ttsManager = new TTSManager(ContextHolder.get());
            ttsManager.init();
            ttsManager.addListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    logInfo("tts speak start");
                    Subscription subscription = SubscribeUtils.doOnUIThread(new Action0() {
                        @Override
                        public void call() {
                            speakStatus(true);
                        }
                    });
                    addSubscription(subscription);
                }

                @Override
                public void onDone(String utteranceId){
                    logInfo("tts speak done");
                    Subscription subscription = SubscribeUtils.doOnUIThreadDelayed(new Action0() {
                        @Override
                        public void call() {
                            speakStatus(false);
                        }
                    },1000);
                    addSubscription(subscription);
                    ttsManager.stop();
                }

                @Override
                public void onError(String utteranceId) {
                    Subscription subscription = SubscribeUtils.doOnUIThreadDelayed(new Action0() {
                        @Override
                        public void call() {
                            speakStatus(false);
                        }
                    },1000);
                    addSubscription(subscription);
                    ttsManager.stop();
                    logInfo("tts speak error : "+utteranceId);
                }
            });
            speakStatus(false);
            isSpeaking = false;
        }
    }

    private void checkSpeak(){
        if(!ttsManager.isInit()){
            showNotice("语音播报初始化未成功");
            return;
        }
        if(cityManager.getCurrentCityWeather() == null){
            showNotice("等待当前天气数据加载...");
            return;
        }
        if(isSpeaking){
            ttsManager.stop();
            speakStatus(false);
        }else{
            speakWeather(cityManager.getCurrentCityWeather());
            speakStatus(true);
        }
        isSpeaking = !isSpeaking;
    }

    private void speakWeather(YunWeather cityWeather){
        if(cityWeather == null)return;
        StringBuffer buffer = new StringBuffer();
        buffer.append("天天提醒您,");
        buffer.append(cityWeather.getCityName());

        if(cityWeather.getCondition() != null){

            YunCondition baseCondition = cityWeather.getCondition();
            buffer.append("今日最高气温"+baseCondition.getTem1()+"度"+",最低气温"+baseCondition.getTem2()+"度,");
            buffer.append("当前天气"+baseCondition.getWea());
            buffer.append(",气温"+baseCondition.getTem()+"度,");
            buffer.append(baseCondition.getWin()+baseCondition.getWin_speed()+",");
            buffer.append(baseCondition.getAir_tips());
        }
        ActionEvent speakEvent = new ActionEvent(EventType.CITY_WEATHER_SPEAK);
        speakEvent.setAttr("cityName",cityWeather.getCityName());
        fire(speakEvent);

        ttsManager.speak(buffer.toString());
        logInfo("自动转换天气信息 : "+buffer.toString());
        //"天天提醒您,地址当前天气晴，气温29度，今日最高气温29度最低气温17度，西南风3级，略微偏热，注意衣物变化，紫外线强不适宜户外运动。车辆尾号限行0，5"
    }


    private void setCurrentVoiceView(VoiceWaveView voiceWaveView,ImageView voiceClose){
        if(speakHolder == null){
            speakHolder = new SpeakHolder();
            speakHolder.waveView = voiceWaveView;
            speakHolder.closeView = voiceClose;
            setVoiceWaveData(voiceWaveView);
            speakStatus(false);
        }
    }

    public void speakStatus(boolean isSpeak){
        if(speakHolder == null){
            logInfo("speakHolder null");
            return;
        }
        logInfo("speak status , isSpeak : "+isSpeak);
        if(speakHolder.waveView != null){
            if(isSpeak){
                speakHolder.waveView.setVisibility(View.VISIBLE);
                speakHolder.waveView.start();
            }else{
                speakHolder.waveView.stop();
                speakHolder.waveView.setVisibility(View.INVISIBLE);
            }
        }
        if(speakHolder.closeView != null){
            speakHolder.closeView.setVisibility(isSpeak?View.INVISIBLE:View.VISIBLE);
        }
    }

    private void setVoiceWaveData(VoiceWaveView voiceWaveView){
        voiceWaveView.setDuration(200);
        voiceWaveView.addHeader(2);
        voiceWaveView.addBody(8);
        voiceWaveView.addBody(17);
        voiceWaveView.addBody(38);
        voiceWaveView.addBody(24);
        voiceWaveView.addBody(8);
        voiceWaveView.addBody(38);
        voiceWaveView.addBody(14);
        voiceWaveView.addBody(27);
        voiceWaveView.addBody(8);
        voiceWaveView.addFooter(2);
        voiceWaveView.setWaveMode(WaveMode.LEFT_RIGHT);
        voiceWaveView.setLineWidth(5f);
        voiceWaveView.setLineSpace(2f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(ttsManager != null){
            ttsManager.shutdown();
            ttsManager = null;
        }
    }

    private TopAlertItem createTopAlertItem(YunWeather cityWeather){
        TopAlertItem item = new TopAlertItem();
        if(cityWeather.getCondition() != null && cityWeather.getCondition().getAlarm() != null){
            item.setYunAlert(cityWeather.getCondition().getAlarm());
        }
        return item;
    }

    private ConditionItem createConditionItem(YunWeather cityWeather){
        ConditionItem item = new ConditionItem();
        if(cityWeather.getCondition() != null){
            item.setYunCondition(cityWeather.getCondition());
        }
        return item;
    }

    private TwoDaysItem createTwoDayItem(YunWeather cityWeather){
        TwoDaysItem item = new TwoDaysItem();
        if(cityWeather.getForecast() != null &&
            cityWeather.getForecast().getData() != null &&
            cityWeather.getForecast().getData().size() > 0){
            item.setForecastItems(cityWeather.getForecast().getData());
        }
        return item;
    }

    private HourlyItem createHourlyItem(YunWeather cityWeather){
        HourlyItem item = new HourlyItem();
        if(cityWeather.getCondition() != null &&
            cityWeather.getCondition().getHours() != null &&
            cityWeather.getCondition().getHours().size() > 0){
            item.setHours(cityWeather.getCondition().getHours());
        }
        if(cityWeather.getCondition() != null){
            item.setSunrise(cityWeather.getCondition().getSunrise());
            item.setSunset(cityWeather.getCondition().getSunset());
        }
        return item;
    }

    private ForecastItem createForecastItem(YunWeather cityWeather){
        ForecastItem item = new ForecastItem();
        if(cityWeather.getForecast() != null &&
            cityWeather.getForecast().getData() != null &&
            cityWeather.getForecast().getData().size() > 0){
            item.setForecastItems(cityWeather.getForecast().getData());
        }
        return item;
    }

    private AqiItem createAqiItem(YunWeather cityWeather){
        AqiItem item = new AqiItem();
        if(cityWeather.getCondition() != null && cityWeather.getCondition().getAqi() != null){
            item.setYunAqi(cityWeather.getCondition().getAqi());
        }
        return item;
    }

    private LiveIndexItem createLiveIndexItem(YunWeather cityWeather){
        LiveIndexItem item = new LiveIndexItem();
        if(cityWeather.getCondition() != null && cityWeather.getCondition().getZhishu()!= null){
            item.setYunLiveIndex(cityWeather.getCondition().getZhishu());
        }
        return item;
    }

    @Override
    protected int getLayout(){
        return R.layout.fragment_city_weather;
    }
}
