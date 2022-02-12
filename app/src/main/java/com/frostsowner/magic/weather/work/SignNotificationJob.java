package com.frostsowner.magic.weather.work;

import static com.frostsowner.magic.weather.service.BaseWeatherService.PRIMARY_CHANNEL;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.fm.commons.http.ContextHolder;
import com.frostsowner.magic.weather.Constant;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.activity.SplashActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SignNotificationJob extends DailyJob {

    private static Logger logger = LoggerFactory.getLogger(SignNotificationJob.class);

    public static final String TAG = "SignNotificationJob";

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(@NonNull Params params) {
        NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL, Constant.CHANNEL, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setShowBadge(true);
            notificationChannel.shouldShowLights();
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableLights(false);
            notificationChannel.setSound(null,null);
            notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent();
        intent.setClass(getContext(), SplashActivity.class);
        PendingIntent homeIntent = PendingIntent.getActivity(getContext(),1,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ContextHolder.get(),PRIMARY_CHANNEL);
        builder.setChannelId(PRIMARY_CHANNEL);
        builder.setContentIntent(homeIntent);
        builder.setOngoing(true);
        builder.setColorized(true);
        builder.setOnlyAlertOnce(true);
        builder.setColor(ContextHolder.get().getResources().getColor(android.R.color.transparent));
        builder.setContentTitle("天天看天气");
        builder.setContentText("起床签到赚取积分了");
        builder.setAutoCancel(true);
        builder.setContentIntent(homeIntent);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setShowWhen(true);
        builder.setColor(Color.RED);
        builder.setLocalOnly(true);
        Notification notification = builder.build();

        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        nm.notify(new Random().nextInt(),notification);
//        NotificationManagerCompat.from(getContext())
//                .notify(new Random().nextInt(), notification);
        logger.info("signNotification shows");
        return DailyJobResult.SUCCESS;
    }

    public static void schedule() {
        if (!JobManager.instance().getAllJobRequestsForTag(TAG).isEmpty()) {
            // job already scheduled, nothing to do
            logger.info("sign notification alert already doing job");
            return;
        }

        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putString("ijimu", "extras : sign notification alert start job");

        logger.info("sign notification alert start job");
        JobRequest.Builder builder = new JobRequest.Builder(TAG)
                .setRequiredNetworkType(JobRequest.NetworkType.ANY);

        // run job between 8:00 to 8:15
        Long startTime = TimeUnit.HOURS.toMillis(8);
        Long endTime = startTime+TimeUnit.MINUTES.toMillis(15);
        DailyJob.schedule(builder, startTime, endTime);
    }

    public static void canceled(){
        if(!JobManager.instance().getAllJobRequestsForTag(TAG).isEmpty()){
            JobManager.instance().cancelAllForTag(TAG);
        }
    }
}
