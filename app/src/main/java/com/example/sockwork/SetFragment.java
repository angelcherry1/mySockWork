package com.example.sockwork;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * author:lixiaobiao
 * date:On2019/11/6
 */
public class SetFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences sharedPreferences;//定义轻量级数据库
    private SwitchButton switchButton;  //定义开关按钮
    private Spinner spinnerDifficulty;      //难度等级下拉框
    private Spinner spinnerAllNum;//解锁题目数目下拉框
    private Spinner spinnerNewNum;//定义新体目的下拉框
    private Spinner spinnerRevieIewNum;//定义复习题的下拉框
    private ArrayAdapter<String> adapterDifficulty,adapterAllNum,adapterNewNum,adapterReviewNum;//定义下拉框的适配器
    //选择难度下拉框里面的选项内容
    String[] difficulty=new String[]{"小学","初中","高中","四级","六级"};
    //解锁题目下拉框的选项卡的内容
    String[] allNum=new String[]{"2道","4道","6道","8道"};
    //复习题目下拉框的选项内容
    String[] newNum=new String[]{"10","30","50","100"};
    //复习题目下拉框的选项内容
    String[] revicwNum=new String[]{"10","30","50","100"};
    SharedPreferences.Editor editor=null;  //定义数据库编辑器

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //绑定布局文件
        View view =inflater.inflate(R.layout.set_fragment_layout,null);
        init(view);
        return view;
    }
    /**
     * 初始化控件
     */

    private  void init(View view){
        sharedPreferences=getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);//初始化数据库
        editor=sharedPreferences.edit();//初始化编辑器
        //开关按钮的绑定
        switchButton=view.findViewById(R.id.switch_btn);
        switchButton.setOnClickListener(this);//开关按钮监听事件
        //选择难度下拉框绑定
        spinnerDifficulty=view.findViewById(R.id.spinner_difficulty);
        //解锁题目下拉框绑定
        spinnerAllNum=view.findViewById(R.id.spinner_all_number);
        //新题目下拉框绑定
        spinnerNewNum=view.findViewById(R.id.spinner_new_number);
        //复习题目下拉框绑定
        spinnerRevieIewNum=view.findViewById(R.id.spinner_revise_number);

        //初始化选择难度下拉框的适配器
        adapterDifficulty=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,difficulty);
        //给选择难度下拉框设置适配器
        spinnerDifficulty.setAdapter(adapterDifficulty);
        //给定义选择难度下拉框设置默认选项
        setSpinnerItemSelectedByValue(spinnerDifficulty,sharedPreferences.getString("difficulty","四级"));
        //设置选择难度的下拉框的监听事件
        this.spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             //获取到选择的内容
             String msg=parent.getItemAtPosition(position).toString();
             editor.putString("difficulty",msg);//写道数据库中
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //初始化解锁题目下拉框的适配器
        adapterAllNum=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,allNum);
        //给选解锁题目下拉框设置适配器
        spinnerAllNum.setAdapter(adapterAllNum);
        //给定义解锁题目下拉框设置默认选项
        setSpinnerItemSelectedByValue(spinnerAllNum,sharedPreferences.getInt("allNum",2)+"道");
        //设置解锁题目的下拉框的监听事件
        this.spinnerAllNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取到选择的内容
                String msg=parent.getItemAtPosition(position).toString();
                int i=Integer.parseInt(msg.substring(0,1));
                editor.putInt("allNum",i);//写道数据库中
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //初始化新题个数下拉框的适配器
        adapterNewNum=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,newNum);
        //给新题个数下拉框设置适配器
        spinnerNewNum.setAdapter(adapterNewNum);
        //给定义新题个数下拉框设置默认选项
        setSpinnerItemSelectedByValue(spinnerNewNum,sharedPreferences.getString("newNum","10"));
        //设置新题个数的下拉框的监听事件
        this.spinnerNewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取到选择的内容
                String msg=parent.getItemAtPosition(position).toString();
                editor.putString("newNum",msg);//写道数据库中
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //初始化新题个数下拉框的适配器
        adapterReviewNum=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,revicwNum);
        //给新题个数下拉框设置适配器
        spinnerRevieIewNum.setAdapter(adapterReviewNum);
        //给定义新题个数下拉框设置默认选项
        setSpinnerItemSelectedByValue(spinnerRevieIewNum,sharedPreferences.getString("revicwNum","10"));
        //设置新题个数的下拉框的监听事件
        this.spinnerRevieIewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取到选择的内容
                String msg=parent.getItemAtPosition(position).toString();
                editor.putString("revicwNum",msg);//写道数据库中
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    /**
     * 设置下拉框默认选项的方法
     */
    private  void setSpinnerItemSelectedByValue(Spinner spinner, String value){
        SpinnerAdapter apsAdapter=spinner.getAdapter();//得到SpinnerAdapter对象
        int k=apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
                spinner.setSelection(i,true);  //默认选中项
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /**
         * 从数据库中获取开关的状态
         */
        if(sharedPreferences.getBoolean("btnTf",false)){
            switchButton.openSwitch();
        }else {
            switchButton.closeSwitch();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_btn:
                if(switchButton.isSwitchOpen()){
                    switchButton.closeSwitch();
                    editor.putBoolean("btnTf",false);
                }else {
                    switchButton.openSwitch();
                    editor.putBoolean("btnTf",true);
                }
                editor.commit();
                break;
        }
    }
}
