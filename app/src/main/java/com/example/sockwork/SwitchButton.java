package com.example.sockwork;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
/**
 * author:lixiaobiao
 * date:On2019/11/6
 */
public class SwitchButton extends FrameLayout {
   private ImageView openImage;
   private  ImageView closeImage;
   public SwitchButton(Context context){
       this(context,null);
   }//联系上下文
    /**
     * 构造方法
     */
    public SwitchButton(Context context,AttributeSet attrs,int defStyleAttr){
        this(context,attrs);
    }
    public SwitchButton(Context context,AttributeSet attrs){
        super(context,attrs);
        /**
         * context通过调用obtainStyledAttributes方法获取一个TypeArray，然后有TypeArray对属性进行设置
         */
        TypedArray typedArray =context.obtainStyledAttributes(attrs,R.styleable.SwitchButton);
        //画出开关为打开的状态
        Drawable openDrawable=typedArray.getDrawable(R.styleable.SwitchButton_switchCloseImage);
        //画出开关状态为关闭的状态
        Drawable closeDrawable=typedArray.getDrawable(R.styleable.SwitchButton_switchCloseImage);
        int switchStatus=typedArray.getInt(R.styleable.SwitchButton_switchStatus,0);
        //调用结束后无比调用recycle（）方法，否则这次的设定会对下次的使用造成影响
        typedArray.recycle();;
        LayoutInflater.from(context).inflate(R.layout.switch_button,this);//绑定布局文件
        openImage=findViewById(R.id.iv_switch_open);
        closeImage=findViewById(R.id.iv_switch_close);
        if(openDrawable!=null){//如果时打开的状态
            openImage.setImageDrawable(openDrawable);//设置显示的图片
        }
        if(closeDrawable!=null){//如果时关闭的状态
            closeImage.setImageDrawable(closeDrawable);
        }if(switchStatus==1){//如果是关闭的方法
            closeSwitch();//执行关闭的方法
        }
    }
   public boolean isSwitchOpen(){//判断开关的状态
        return openImage.getVisibility()== View.VISIBLE;
   }
    public void openSwitch(){
        openImage.setVisibility(View.VISIBLE);//显示打开图片
        closeImage.setVisibility(View.INVISIBLE);//隐藏关闭图片
    }

    public void closeSwitch(){
        openImage.setVisibility(View.INVISIBLE);//隐藏打开图片
        closeImage.setVisibility(View.VISIBLE);//显示打开图片
    }
}
