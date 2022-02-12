package com.frostsowner.magic.weather;

import static com.frostsowner.magic.weather.Constant.BUGLY_APP_ID;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.android.bugfix.logic.BugFixManager;
import com.evernote.android.job.JobConfig;
import com.evernote.android.job.JobManager;
import com.fm.commons.http.ContextHolder;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.thread.UIThread;
import com.frostsowner.magic.weather.activity.HomeTabActivity;
import com.frostsowner.magic.weather.base.BaseApplication;
import com.frostsowner.magic.weather.logic.CrashHandler;
import com.frostsowner.magic.weather.logic.LogicModule;
import com.frostsowner.magic.weather.work.DemonsCreator;
import com.tencent.bugly.beta.Beta;

public class AppApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        UIThread.init();
        ContextHolder.set(this);
        if (isProperty("privacy")) {
            CrashHandler.getInstance().init(this);
            LogicModule.init();
            UMConfig.preInit(this);
            initActivityLife();
            initBugly();
            JobManager.create(this).addJobCreator(new DemonsCreator());
            JobConfig.setLogcatEnabled(true);
        }
    }

    private void initBugly(){
//        BugFixManager bugFixManager = BeanFactory.getBean(BugFixManager.class);
//        Beta.canShowUpgradeActs.add(HomeTabActivity.class);
//        Beta.appChannel = Constant.CHANNEL;
//        Beta.autoCheckUpgrade = false;
//        Beta.autoCheckAppUpgrade = false;
//        Beta.showInterruptedStrategy = true;
//        Beta.upgradeCheckPeriod = 10*1000;
////        Beta.upgradeDialogLayoutId = R.layout.dialog_upgrade_custom;
//        bugFixManager.init(this,BUGLY_APP_ID);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
