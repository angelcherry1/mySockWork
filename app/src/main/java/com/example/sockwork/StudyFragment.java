package com.example.sockwork;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.example.greendao.entity.greendao.DaoMaster;
import com.example.greendao.entity.greendao.DaoSession;
import com.example.greendao.entity.greendao.WisdomEntity;
import com.example.greendao.entity.greendao.WisdomEntityDao;

import java.util.List;
import java.util.Random;

/**
 * author:lixiaobiao
 * date:On2019/11/5
 */
public class StudyFragment extends Fragment {
    private TextView difficultyTv,          //学习的难易程度
                    wisdomEnglish,          //名人名句的英文意义
                    wisdomChina,            //名人名句的汉语意思
                    alreadyStudyText,       //已经学习的题目数
                    alreadyMasteredText,      //已经掌握的题数
                    wrongText;              //答错的题数
    private SharedPreferences sharedPreferences;//定义轻量级数据库
    private DaoMaster mDaoMaster;       //数据库管理者
    private DaoSession mDaoSession;//与数据库对话
    //对应的表，由Java代码生成的，对应的数据库内相应的表格操作使用此对象
    private WisdomEntityDao questionDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.study_fragment_layout,null);//绑定布局文件
        sharedPreferences=getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        difficultyTv=view.findViewById(R.id.difficulty_text);//学习难易度绑定
        wisdomEnglish=view.findViewById(R.id.wisdom_english);//
        wisdomChina=view.findViewById(R.id.wisdom_china);
        //已掌握题数的绑定
        alreadyMasteredText=view.findViewById(R.id.already_mastered);
        alreadyStudyText=view.findViewById(R.id.already_study);
        wrongText=view.findViewById(R.id.wrong_text);

        AssetsDatabaseManager.initManager(getActivity());//数据库初始化，只需要调用一次
        //获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg=AssetsDatabaseManager.getManager();
        SQLiteDatabase db1=mg.getDatabase("wisdom.db");
        mDaoMaster=new DaoMaster(db1);
        mDaoSession=mDaoMaster.newSession();
        questionDao=mDaoSession.getWisdomEntityDao();//获取数据

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        difficultyTv.setText(sharedPreferences.getString("difficulty","四级")+"英语");//设置默认显示的难度级别四级
        List<WisdomEntity>datas=questionDao.queryBuilder().list();//获取数据集合
        Random random=new Random();
        int i=random.nextInt(10);
        wisdomEnglish.setText(datas.get(i).getEnglish());//从数据库中获取这个数据的英文数据
        wisdomChina.setText(datas.get(i).getChina());//从数据库中获取一条中文数据
        setText();//设置文字的
    }

    private  void setText(){
        alreadyStudyText.setText(sharedPreferences.getInt("alreadyStudy",0)+"");//设置已经学习的题数
        alreadyMasteredText.setText(sharedPreferences.getInt("alreadyMastered",0)+"");//设置已经复习的题数
        //设置错题题数
        wrongText.setText(sharedPreferences.getInt("wrong",0)+"");
    }
}
