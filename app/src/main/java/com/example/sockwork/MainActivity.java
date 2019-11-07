package com.example.sockwork;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.example.greendao.entity.greendao.CET4Entity;
import com.example.greendao.entity.greendao.CET4EntityDao;
import com.example.greendao.entity.greendao.DaoMaster;
import com.example.greendao.entity.greendao.DaoSession;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SynthesizerListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, SynthesizerListener {
//用于显示单词和音标
    private TextView timeText,dateText,wordText,englishText;
    private ImageView playVioce;         //播放声音
    private String mMonth,mDay,mWay,mHours,mMinute;     //用来显示时间
    private SpeechSynthesizer speechSynthesizer; //用来合成对象

    //锁屏
    private KeyguardManager km;
    private  KeyguardManager.KeyguardLock kl;
    private RadioGroup radioGroup;          //加载单词的三个选项
    private RadioButton radioOne,radioTwo,radioThree;       //单词意思的三个选项
    private SharedPreferences sharedPreferences;        //d定义轻量级数据库
    SharedPreferences.Editor editor=null;       //编辑数据库
    int j=0;//用于记录答了几道题
    List<Integer> list;//判断题目的数目
    List<CET4Entity>datas;//用于从数据库读取相应的数据
    int k;

    /**
     * 手指按下时坐标（x1，y1）
     * 手指离开时的坐标（x2，y2）
     *
     *
     */
    float x1=0;
    float y1=0;
    float x2=0;
    float y2=0;

    private SQLiteDatabase da;          //创建数据库
    private DaoMaster mDaoMaster ,dbMaster;//管理者
    private DaoSession mDaoSession ,dbSession;      //和数据库进行会话
    //对应的表，由java代码生成的，对数据库内相应表操作使用这个独享
    private CET4EntityDao questionDao,dbDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将锁屏界面显示到手机屏幕的最上层
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        init();
    }
    /**
     * 初始化控件
     */
    public void init(){
        //初始化轻量级数据库
        sharedPreferences=getSharedPreferences("share", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();//初始化轻量级数据库编译器
        //给播放单词语音的设置appid（这个需要在讯飞平台申请的，详情参考讯飞官网）
        list=new ArrayList<Integer>();//初始化list
        /**
         * 添加十个20以内的是随机数
         */
        Random r=new Random();
        int i;
        while(list.size()<10){
            i=r.nextInt(20);
            if(!list.contains(i)){
                list.add(i);
            }
        }
        /**
         * 得到锁盘管理对象
         */
        km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl=km.newKeyguardLock("unLock");
        //初始化，只需要使用一次
        AssetsDatabaseManager.initManager(this);
        //获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg=AssetsDatabaseManager.getManager();
        //通过管理对象获取数据库
        SQLiteDatabase db1=mg.getDatabase("word.db");
        //对数据库进行操作
        mDaoMaster=new DaoMaster(db1);
        mDaoSession=mDaoMaster.newSession();
        questionDao=mDaoSession.getCET4EntityDao();
        /**
         * 此DevOpenHelper类继承自SQLiteOpenHelper
         * 第一个参数Context，第二个参数数据库名称，第三个参数CursorFactoty
         */
        DaoMaster.DevOpenHelper helper=new DaoMaster.DevOpenHelper(this,"wrong.db",null);
        /**
         * 初始化数据库
         */
       SQLiteDatabase db=helper.getWritableDatabase();
       dbMaster=new DaoMaster(db);
       dbSession=dbMaster.newSession();
       dbDao=dbSession.getCET4EntityDao();
        /**
         * 控件初始化
         */
        //用于显示分钟绑定id
        timeText=findViewById(R.id.time_text);
        //用于显示日期绑定id
        dateText=findViewById(R.id.date_text);
        //用于显示单词绑定id
        wordText=findViewById(R.id.word_text);
        //用于显示音标绑定id
        englishText=findViewById(R.id.english_text);
        //用于播放单词的按钮绑定
        playVioce=findViewById(R.id.play_vioce);
        //给播放单词单词的按钮进行监听id
        playVioce.setOnClickListener(this);
        //给加载单词的三个选项绑定id
        radioGroup=findViewById(R.id.choose_group);
        //给第一个绑定id
        radioOne=findViewById(R.id.choose_btn_one);
        //给第二个绑定id
        radioTwo=findViewById(R.id.choose_btn_two);
        //给第三个绑定id
        radioThree=findViewById(R.id.choose_btn_three);
        //给加载三个单词三个选线设置监听事件
        radioGroup.setOnCheckedChangeListener(this);
        setParam();
        //appid
        SpeechUser.getUser().login(MainActivity.this,null,null,"appdi=5dc02f0a",listener);
    }

//同步手机时间系统

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 获取系统时间，并设置将其显示出来
         */
        Calendar calendar=Calendar.getInstance();
        mMonth=String.valueOf(calendar.get(Calendar.MONTH)+1);
        mDay=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        mWay=String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));//获得星期

        /**
         * 如果小时是个位就在前面加一个“0”
         */
        if(calendar.get(Calendar.HOUR)<10){
            mHours="0"+calendar.get(Calendar.HOUR);

        }else {
            mHours=String.valueOf(calendar.get(Calendar.HOUR));
        }
        /**
         * 如果分钟是个位
         * 则在前面加一个“0”
         */
        if(calendar.get(Calendar.MINUTE)<10){
            mMinute="0"+calendar.get(Calendar.MINUTE);
        }else {
            mMinute=String.valueOf(calendar.get(Calendar.MINUTE));
        }
        /**
         * 获得星期并设置出来
         */
        if("1".equals(mWay)){
            mWay="天";
        }else if("2".equals(mWay)){
            mWay="一";
        }else if("3".equals(mWay)){
            mWay="二";
        }else if("4".equals(mWay)){
            mWay="三";
        }else if("5".equals(mWay)){
            mWay="四";
        }else if("6".equals(mWay)){
            mWay="五";
        }else if("7".equals(mWay)){
            mWay="六";
        }
        timeText.setText(mHours+":"+mMinute);
        dateText.setText(mMonth+"月"+mDay+"日"+"    "+"星期"+mWay);
        //把mainActivity添加到销毁集合中
        BaseApplication.addDestroyActivity(this,"mainActivity");

        getDBData();//获取数据文件的方法
    }
        //词义操作的方法
        private void saveWrongData(){
        String word=datas.get(k).getWord();//获得答错的这道题的单词
            String english=datas.get(k).getEnglish();//获得答错题的音标
            String china=datas.get(k).getChina();//获得答错题的汉语
            String sign=datas.get(k).getSign();//获得答错题的标记
            CET4Entity data=new CET4Entity(Long.valueOf(dbDao.count()),word,english,china,sign);
            dbDao.insertOrReplace(data);//将这些字段存入数据库
        }

    /**
     *设置选项的不同颜色
     *
     */
    private void btnGetText(String msg,RadioButton btn){
        /**
         * 答对设置绿色，答错设置红色
         */
        if(msg.equals(datas.get(k).getChina())){
            wordText.setTextColor(Color.GREEN);
            englishText.setTextColor(Color.GREEN);
            btn.setTextColor(Color.GREEN);
        }else {
            wordText.setTextColor(Color.RED);
            englishText.setTextColor(Color.RED);
            btn.setTextColor(Color.RED);
            saveWrongData();//存入错题库
            //保存到数据库
            int wrong=sharedPreferences.getInt("wrong",0);//从数据库中取出数据
            editor.putInt("wrong",wrong+1);//写入数据库
            editor.putString("wrongId",","+datas.get(j).getId());//写入数据库
            editor.commit();//保存
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_vioce:
                String text=wordText.getText().toString();      //把单词提取出来
                speechSynthesizer.startSpeaking(text,this);//播放声音
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        /**
         * 选项的单击事件
         */
        radioGroup.setClickable(false);//默认选项没有被选中
        switch (checkedId){
            case R.id.choose_btn_one:       //选项A被选中
                //截取字符串
                String msg=radioOne.getText().toString().substring(3);
                btnGetText(msg,radioOne);//将参数传入对应的方法之中
                break;
            case R.id.choose_btn_two:       //选项B被选中
                //截取字符串
                String msg2=radioTwo.getText().toString().substring(3);
                btnGetText(msg2,radioTwo);//将参数传入对应的方法之中
                break;
            case R.id.choose_btn_three:       //选项C被选中
                //截取字符串
                String msg3=radioThree.getText().toString().substring(3);
                btnGetText(msg3,radioThree);//将参数传入对应的方法之中
                break;
        }
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {

    }
    /**
     * 还原单词和选项的颜色
     */
    private void setTextColor(){
        //还原单词选项的颜色
        radioOne.setChecked(false);
        radioTwo.setChecked(false);
        radioThree.setChecked(false);
        //将选项的按钮设置成白色
        radioOne.setTextColor(Color.parseColor("#FFFFFF"));
        radioTwo.setTextColor(Color.parseColor("#FFFFFF"));
        radioThree.setTextColor(Color.parseColor("#FFFFFF"));
        wordText.setTextColor(Color.parseColor("#FFFFFF"));
        englishText.setTextColor(Color.parseColor("#FFFFFF"));
    }
    /**
     * 设置选项
     */
    private void setChina(List<CET4Entity>datas,int j){
        /**
         * 随机产生几个随机数，是用于解锁单词
         * 因为数据库只录入20个单词，所以产生的随机数是20以内的数
         */
        Random r=new Random();
        List<Integer>listInt=new ArrayList<>();
        int i;
        while(listInt.size()<4){
            i=r.nextInt(20);
            if(!listInt.contains(i)){
                listInt.add(i);
            }
        }
        /**
         * 一下的判断是为了这个单词设置三个选项，设置单词选项是有规律的
         * 三个选项。分别是正确的，正确的前一个，正确的后一个
         * 将这三个解释设置到单词的选项上，一下是实现逻辑
         */
        if(listInt.get(0)<7){
           radioOne.setText("A: "+datas.get(k).getChina());
           if(k-1>0){
               radioTwo.setText("B: "+datas.get(k-1).getChina());
           }else {
               radioTwo.setText("B: "+datas.get(k+2).getChina());
           }
           if(k+1<20){
               radioThree.setText("C: "+datas.get(k+1).getChina());
           }else {
               radioThree.setText("C: "+datas.get(k-1).getChina());
           }
        }else  if(listInt.get(0)<14){
            radioTwo.setText("B: "+datas.get(k).getChina());
            if(k-1>0){
                radioOne.setText("A: "+datas.get(k-1).getChina());
            }else {
                radioOne.setText("B: "+datas.get(k+2).getChina());
            }
            if(k+1<20){
                radioThree.setText("C: "+datas.get(k+1).getChina());
            }else {
                radioThree.setText("C: "+datas.get(k-1).getChina());
            }
        }else {
            radioThree.setText("C: "+datas.get(k).getChina());
            if(k-1>0){
                radioTwo.setText("B: "+datas.get(k-1).getChina());
            }else {
                radioTwo.setText("B: "+datas.get(k+2).getChina());
            }
            if(k+1<20){
                radioOne.setText("A: "+datas.get(k+1).getChina());
            }else {
                radioOne.setText("A: "+datas.get(k-1).getChina());
            }
        }
    }
    /**
     * 获得数据库数据
     */
    private void getDBData(){
        datas=questionDao.queryBuilder().list();//把词库里面的单词读出来
        k=list.get(j);
        wordText.setText(datas.get(k).getWord());
        englishText.setText(datas.get(k).getEnglish());
        setChina(datas,k);      //设置三个词义选项
    }
//手势滑动事件的监听
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            //当手指按下的坐标
            x1=event.getX();
            y1=event.getY();
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            x2 = event.getX();
            y2 = event.getY();
            //向上划的
            if(y1-y2>200){
                //已掌握的单词数量加1int
                int num=sharedPreferences.getInt("alreadyMastered",0)+1;
                editor.putInt("alreadyMastered",num);//存入到数据库
                editor.commit();
                Toast.makeText(this,"已掌握",Toast.LENGTH_LONG).show();
                getNextData();
            }else  if(y2-y1>200){
                Toast.makeText(this,"带价共功能",Toast.LENGTH_LONG).show();
            }else if(x1-x2>200){//向左划
                getNextData();//获取下一条数据
            }else if(x2-x1>200){
                unlocked();//解锁
            }
        }
        return super.onTouchEvent(event);

    }

    /**
 * 获取下一题
 */
private void getNextData(){
    j++;//当前已做题目的数目
    int i=sharedPreferences.getInt("allNum",2);//默认解锁题目数为2题
    if(i>j){
        getDBData();       //获得数据
        setTextColor();         //设置颜色
        //已经学习的单词数量加1
        int num=sharedPreferences.getInt("alreadyStudy",0)+1;
        editor.putInt("alreadyStudy",num);
        editor.commit();//提交到数据库
    }else {
        unlocked();//解锁
    }
}
    /**
     * 解锁的方法
     */
    private void unlocked(){
        Intent intent1=new Intent(Intent.ACTION_MAIN);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addCategory(Intent.CATEGORY_HOME);//进入手机桌面
        startActivity(intent1);
        kl.disableKeyguard();   //销毁当前activity
        finish();
    }
    /**
     * 通用回调接口
     *
     */
    private SpeechListener listener= new SpeechListener() {
        //消息回调
        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    //数据回调
        @Override
        public void onData(byte[] bytes) {

        }
//结束回调
        @Override
        public void onCompleted(SpeechError speechError) {

        }
    };
    /**
     * 初始化语音播报
     */
    public void setParam(){
        speechSynthesizer=SpeechSynthesizer.createSynthesizer(this);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
        speechSynthesizer.setParameter(SpeechConstant.SPEED,"50");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME,"50");
        speechSynthesizer.setParameter(SpeechConstant.PITCH,"50");
    }
}
