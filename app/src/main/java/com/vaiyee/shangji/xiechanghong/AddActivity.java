package com.vaiyee.shangji.xiechanghong;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vaiyee.shangji.xiechanghong.Bean.Diary;
import com.vaiyee.shangji.xiechanghong.Bean.PaixuDiary;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView close;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.content)
    EditText content;
    private String t,c;
    private Diary diary;
    private PaixuDiary paixuDiary;
    private  String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT>=21)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);              //实现沉浸式状态栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //使不自动弹出软键盘
        ButterKnife.bind(this); //利用开源的ButterKnife框架，便于一键查找控件，无需重复写findviewbyid和setOnclicklistener()方法
        time.setText(getTime());
        diary = (Diary) getIntent().getSerializableExtra("diary");
            if (diary!=null)  //如果点击的是修改按钮或者是查看日记
            {
                title.setText(diary.getTitle());
                content.setText(diary.getContent());
                id = String.valueOf(diary.getId());
            if (getIntent().getStringExtra("cd").equals("修改"))  //如果点击的修改按钮
            {
                save.setText("确认修改");
            } else   //否则仅仅是查看日记
            {
                title.setKeyListener(null);
                content.setKeyListener(null);
                content.setTextIsSelectable(true);
                save.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.imageView, R.id.save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageView:
                finish();
                break;
            case R.id.save:
                if (TextUtils.isEmpty(id)) {  //如果是新增日记
                    if (TextUtils.isEmpty(title.getText())) {
                        Toast.makeText(this, "请输入标题", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        t = title.getText().toString().trim();
                    }
                    if (TextUtils.isEmpty(content.getText())) {
                        Toast.makeText(this, "请输入日记内容", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        c = content.getText().toString();
                    }
                    Diary diary = new Diary();
                    diary.setTitle(t);
                    diary.setContent(c);
                    diary.setTime(getTime());
                    diary.setOne("第一张表的数据");
                    diary.save();   //这里是存到本地数据库的
                    MainActivity.clientThread.sendMessage("insert***"+t+"***"+getTime()+"***"+c);   //添加到服务器的数据库
                    finish(); //添加后直接关闭当前Activity回到MainActivity，此时MainActivity中的onResunm（）方法会得到执行，在这里向服务器发送显示所有数据的请求，将改变后的数据集重新显示
                    }
                else  //如果是修改日记
                {
                    if (TextUtils.isEmpty(title.getText())) {
                        Toast.makeText(this, "请输入标题", Toast.LENGTH_LONG).show();
                    } else {
                        t = title.getText().toString().trim();
                    }
                    if (TextUtils.isEmpty(content.getText())) {
                        Toast.makeText(this, "请输入日记内容", Toast.LENGTH_LONG).show();
                    } else {
                        c = content.getText().toString();
                    }
                    ContentValues values = new ContentValues();
                    values.put("title",t);
                    values.put("content",c);
                    values.put("time",getTime());
                    if (diary.getOne().equals("第一张表的数据"))  //如果是第一张表的数据       ，，，，这里是修改本地数据的数据
                    {
                        DataSupport.update(Diary.class,values, Long.parseLong(id));
                        Toast.makeText(this, "修改成功！", Toast.LENGTH_LONG).show();
                    }
                    else // 如果是第二张表的数据
                    {
                        DataSupport.update(PaixuDiary.class,values, Long.parseLong(id));
                        Toast.makeText(this, "修改成功！", Toast.LENGTH_LONG).show();
                    }
                    MainActivity.clientThread.sendMessage("update***"+id+"***"+t+"***"+getTime()+"***"+c);
                    finish();
                }
                break;
        }
    }

    public static String getTime()
    {
        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour; // 0-23
        int minute = t.minute;
        //int second = t.second;
        return String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(date)+"/  "+String.valueOf(hour)+":"+String.valueOf(minute);
    }
}
