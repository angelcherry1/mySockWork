package com.example.sockwork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

/**
 * @author:lixiaobiao
 * @date:On 2019/11/7
 * @Desriptiong: 23231
 */
public class ScreenListener {
    private Context context;        //联系上下文
    private ScreenBroadcastReceiver mScreenReceiver;//定义一个广播
    private ScreenStateListener mScreenStateListenner;//定义内部接口
    /**
     * 初始化
     */
    public ScreenListener(Context context){
        this.context=context;
        mScreenReceiver=new ScreenBroadcastReceiver();//初始化广播
    }
    /**
     * 自定义接口
     */
    public interface ScreenStateListener{
        void onScreenOn();//手机屏幕点亮
        void onScreenOff();//手机屏幕关闭
        void onUserpresent();//手机屏幕解锁
    }
    /**
     * 获取screen的状态
     */
    private void getScreenState(){
        //初始化powerManager
        PowerManager manager=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if(manager.isScreenOn()){//如果监听已经开启
            if(mScreenStateListenner!=null){
                mScreenStateListenner.onScreenOn();
            }

        }else {
            if (mScreenStateListenner!=null){//如果监听没有开启
                mScreenStateListenner.onScreenOff();
            }
        }
    }

    /**
     * 写一个内部的广播
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver{
        private String action=null;
        @Override
        public void onReceive(Context context, Intent intent) {
            action=intent.getAction();
            if(Intent.ACTION_SCREEN_ON.equals(action)){//屏幕亮时的操作
                mScreenStateListenner.onScreenOn();
            }else if(Intent.ACTION_SCREEN_OFF.equals(action)){//屏幕关闭时的操作
                mScreenStateListenner.onScreenOff();

            }else if (Intent.ACTION_USER_PRESENT.equals(action)){//解锁时的操作
                mScreenStateListenner.onUserpresent();
            }
        }
    }
    /**
     * 开始监听广播操作
     */
    public void begin(ScreenStateListener listener){
    mScreenStateListenner=listener;
    registerListerner();//注监听
        getScreenState();//获取监听

    }
    /**
     * 启动广播监听
     */
    private void registerListerner(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);//屏幕亮的时候开启广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);//屏幕关闭的时候开启的广播
        filter.addAction(Intent.ACTION_USER_PRESENT);//屏幕锁屏的时候开启的广播
        context.registerReceiver(mScreenReceiver,filter);//发送广播
    }

    /**
     * 解除广播
     */
    public void unregisterListener(){
        context.unregisterReceiver(mScreenReceiver);//注销广播
    }
}
