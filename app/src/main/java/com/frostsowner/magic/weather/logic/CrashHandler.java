package com.frostsowner.magic.weather.logic;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.fm.commons.http.ContextHolder;
import com.fm.commons.util.ApkResources;
import com.umeng.analytics.MobclickAgent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Properties;


/** 
 *  
 *  
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。  
 *                           如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框 
 *                           实现该接口并注册为程序中的默认未捕获异常处理  
 *                           这样当未捕获异常发生时，就可以做些异常处理操作 
 *                           例如：收集异常信息，发送错误报告 等。 
 *  
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告. 
 */  
public class CrashHandler implements UncaughtExceptionHandler {
    /** Debug Log Tag */  
    public static final String TAG = "CrashHandler";
    /** CrashHandler实例 */  
    private static CrashHandler INSTANCE;  
    /** 程序的Context对象 */  
    private Context mContext;
    /** 系统默认的UncaughtException处理类 */  
    private UncaughtExceptionHandler mDefaultHandler;
          
    /** 使用Properties来保存设备的信息和错误堆栈信息 */  
    private Properties mDeviceCrashInfo = new Properties();
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";
      
    /** 保证只有一个CrashHandler实例 */  
    private CrashHandler() {  
    }  
  
    /** 获取CrashHandler实例 ,单例模式 */  
    public static CrashHandler getInstance() {  
        if (INSTANCE == null)  
            INSTANCE = new CrashHandler();  
        return INSTANCE;  
    }  
      
    /** 
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器 
     *  
     * @param ctx 
     */  
    public void init(Context ctx) {
        mContext = ctx;  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }  
      
    /** 
     * 当UncaughtException发生时会转入该函数来处理 
     */  
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {  
            // 如果用户没有处理则让系统默认的异常处理器来处理  
            mDefaultHandler.uncaughtException(thread, ex);
        } else {  
            // Sleep一会后结束程序  
            // 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序  
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            	Log.e(TAG, "Error : ", e);
            }
            exit();
        }  
    }  

	/**
	 * 退出程序的方法
	 */
	private void exit(){
//        MobclickAgent.onKillProcess(mContext);
//        StatService.
		System.exit(0);
	}	

	public boolean handleException(final Throwable ex){
	    return handleException(ex,true);
    }

    /** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑 
     *  
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false 
     */  
    public boolean handleException(final Throwable ex,boolean showNotice) {
        if (ex == null) {  
            return true;  
        }  
        final String msg = ex.getLocalizedMessage();
        // 使用Toast来显示异常信息  
        new Thread() {
            @Override
            public void run() {  
                // Toast 显示需要出现在一个线程的消息队列中  
                Looper.prepare();
                if(showNotice && ApkResources.isDebug(ContextHolder.get())) {
//                    SimpleNotice.show(ContextHolder.get().getString(R.string.app_error_alert)+msg);
//                    Toast.makeText(ContextHolder.get(),ContextHolder.get().getString(R.string.app_error_alert)+msg,Toast.LENGTH_SHORT)
//                    .show();
                }
                Looper.loop();
            }  
        }.start();

//        StatService.reportException(mContext, ex);
//        StatService.reportError(mContext,msg);
        MobclickAgent.reportError(mContext,ex);
        MobclickAgent.reportError(mContext,msg);

        // 收集设备信息  
        collectCrashDeviceInfo(mContext);  
        // 使用友盟SDK将错误报告保存到文件中，待下次应用程序重启时上传log
        saveCrashInfoToFile(ex);
        // 发送错误报告到服务器
        //已经使用了友盟SDK上传，故此处注掉
//        sendCrashReportsToServer();  
        
        return true;  
    }  
  
    /** 
     * 收集程序崩溃的设备信息 
     *  
     * @param ctx 
     */  
    public void collectCrashDeviceInfo(Context ctx) {
        try {  
            // Class for retrieving various kinds of information related to the  
            // application packages that are currently installed on the device.  
            // You can find this class through getPackageManager().  
            PackageManager pm = ctx.getPackageManager();
            // getPackageInfo(String packageName, int flags)  
            // Retrieve overall information about an application package that is installed on the system.  
            // public static final int GET_ACTIVITIES  
            // Since: API Level 1 PackageInfo flag: return information about activities in the package in activities.  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {  
                // public String versionName The version name of this package,  
                // as specified by the <manifest> tag's versionName attribute.  
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);  
                // public int versionCode The version number of this package,   
                // as specified by the <manifest> tag's versionCode attribute.  
                mDeviceCrashInfo.put(VERSION_CODE, String.valueOf(pi.versionCode));
            } 
            //添加渠道号
            //mDeviceCrashInfo.put("ctype", Setting.getInstance().getCType());
            
        } catch (NameNotFoundException e) {
        	Log.e(TAG, "Error while collect package info", e);
        }  
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,  
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息  
        // 返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段  
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {  
                // setAccessible(boolean flag)  
                // 将此对象的 accessible 标志设置为指示的布尔值。  
                // 通过设置Accessible属性为true,才能对私有变量进行访问，不然会得到一个IllegalAccessException的异常  
                field.setAccessible(true);  
                mDeviceCrashInfo.put(field.getName(), String.valueOf(field.get(null)));
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
            	Log.e(TAG, "Error while collect crash info", e);
            }  
        }  
    }  
      
    /** 
     * 保存错误信息到文件中 
     *  
     * @param ex 
     * @return 
     */  
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        // printStackTrace(PrintWriter s)  
        // 将此 throwable 及其追踪输出到指定的 PrintWriter  
        ex.printStackTrace(printWriter);  
  
        // getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。  
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
  
        // toString() 以字符串的形式返回该缓冲区的当前值。  
        String result = info.toString();
        printWriter.close();  
        mDeviceCrashInfo.put(STACK_TRACE, result);  
        Log.e(TAG, result);
        //友盟是现将错误信息保存在com_umeng__crash.cache文件中，然后在应用程序启动时调用MobclickAgent.onError(context);来启动一线程上传log
        String log=mDeviceCrashInfo.toString();
		// 保存日志文件
		/*String str = saveCrashInfo2File(log,ex);
			Log.e(TAG, str);
		 */
        return null;  
    }

    public void report(String msg){
//        StatService.reportError(mContext,msg);
        MobclickAgent.reportError(mContext,msg);
    }
}  