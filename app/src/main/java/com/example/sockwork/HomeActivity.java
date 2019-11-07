package com.example.sockwork;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


/**
 * @author:lixiaobiao
 * @date:On 2019/11/7
 * @Desriptiong: 23231
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private  ScreenListener screenListener;//绑定此页面与手机屏幕状态的监听
    private SharedPreferences sharedPreferences;//定义轻量级数据库
    private FragmentActivity fragmentActivity;//定义一个
    private androidx.fragment.app.FragmentTransaction transaction;//定义一个用于加载的复习与设置的界面
    private StudyFragment studyFragment;//绑定复习界面
    private SetFragment setFragment;//绑定设置界面
    private Button wrongBtn;//定义错词本按钮
    private Long exitTime;//单击一次推出的时间

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_layout);//绑定布局
        init();//初始化控件
    }

    /**
     * 初始化控件的方法
     */
    private  void init(){
        sharedPreferences=getSharedPreferences("share", Context.MODE_PRIVATE);//初始化数据库
        wrongBtn=findViewById(R.id.wrong_btn);//绑定id
        wrongBtn.setOnClickListener(this);//对按钮设置监听
        //设置editer用于向数据库中添加数据和修改数据
        final SharedPreferences.Editor editor=sharedPreferences.edit();
        screenListener=new ScreenListener(this);//屏幕状态监听
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {//手机屏幕已经点亮的操作
                //判断是否在设置界面开启了锁屏
                if(sharedPreferences.getBoolean("btnTf",false)){
                    Intent intent=new Intent(HomeActivity.this,MainActivity.class);//启动锁屏页
                    startActivity(intent);//开始跳转
                }
            }

            @Override
            public void onScreenOff() {//手机已经锁屏的操作
                /**
                 * 如果手机已经锁了
                 * 就把数据库中的tf字段改成true
                 */
                editor.putBoolean("tf",true);
                editor.commit();
                //销毁锁屏界面
                BaseApplication.destrouActivity("mainActivity");

            }

            @Override
            public void onUserpresent() {//手机已经解锁的操作
                /**
                 * 如果手机已经解锁了
                 * 就把数据库中的tf字段改成false
                 */
                editor.putBoolean("tf",false);
                editor.commit();

            }
        });
        //当此页面加载的时候显示复习界面的fragment
        studyFragment=new StudyFragment();
        setFragment(studyFragment);//设置显示不同的fragment
    }
    public void setFragment(Fragment fragment){

        transaction = getSupportFragmentManager().beginTransaction();
        //初始化transaction
         transaction.replace(R.id.frame_layout, fragment);
        //绑定id
         transaction.commit();
    }
    //单击进入设置界面
    public  void set(View v){
        if(setFragment==null){
            setFragment =new SetFragment();
        }
        setFragment(setFragment);
    }
    //点击复习进入复习界面
    public  void study(View v){
        if(studyFragment==null){
            studyFragment =new StudyFragment();
        }
        setFragment(studyFragment);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wrong_btn:
                Toast.makeText(this,"跳转到错题界面",Toast.LENGTH_LONG).show();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenListener.unregisterListener();//解除广播
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if(System.currentTimeMillis()-exitTime>2000){
                Toast.makeText(this,"再按一次，就退出了",Toast.LENGTH_LONG).show();
                exitTime=System.currentTimeMillis();
            }else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
