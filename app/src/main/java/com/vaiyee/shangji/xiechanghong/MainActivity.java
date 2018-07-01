package com.vaiyee.shangji.xiechanghong;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.vaiyee.shangji.xiechanghong.Adapter.DiaryListAdapter;
import com.vaiyee.shangji.xiechanghong.Bean.Diary;
import com.vaiyee.shangji.xiechanghong.Bean.PaixuDiary;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements DiaryListAdapter.UpDateListener {

    @BindView(R.id.add)
    ImageView add;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.riji_list)
    RecyclerView rijiList;
    @BindView(R.id.show_all)
    TextView showAll;
    private static List<Diary> diaryList = new ArrayList<>();
    private List<Diary> diaryList2 = new ArrayList<>();  //用来中转倒序排序
    private List<PaixuDiary> paixuDiaries = new ArrayList<>();
    private List<PaixuDiary> paixuDiaries2 = new ArrayList<>(); //用来将查询结果中转
    private static DiaryListAdapter adapter=null;
    public static ClientThread clientThread = null;
    public static boolean isConnect = false; //socket是否连接的标志位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //addDate();
        clientThread = new ClientThread(handler);
        clientThread.start();
        adapter = new DiaryListAdapter(this, diaryList,clientThread);
        adapter.setListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rijiList.setLayoutManager(manager);
        rijiList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnect)
        {
            Log.d("执行了","resume");
            clientThread.sendMessage("selectall***获取服务器数据");  //首次打开在子线程里面获取服务器数据,添加或修改或删除数据时socket已建立连接，此时可以直接在这里获取服务器数据刷新
        }
        // Totop();   //  这里为了方便，当修改数据后返回主界面，刷新数据
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    String content =bundle.getString("response");
                    Log.d("服务器返回的数据", content);
                    String[] lines = content.split("\\*\\*\\*\\*");  //每一行就是一个日记对象
                    switch (lines[0])
                    {
                        case "insert success":
                            Toast.makeText(MainActivity.this,"保存日记成功！",Toast.LENGTH_LONG).show();
                            break;
                        case "delete success":
                            Toast.makeText(MainActivity.this,"删除成功！",Toast.LENGTH_LONG).show();
                            break;
                        case "update success":
                            Toast.makeText(MainActivity.this,"修改成功！",Toast.LENGTH_LONG).show();
                            break;
                        case "null":
                            Toast.makeText(MainActivity.this,"查无结果！",Toast.LENGTH_LONG).show();
                            break;
                        case "##end":
                            Toast.makeText(MainActivity.this,"查无结果！",Toast.LENGTH_LONG).show();
                            break;
                            default:
                                diaryList.clear(); // 每次获取最新的服务器数据都要把diaryList清空，防止数据重复
                                for (int i = 0; i < lines.length-1; i++) {
                                    Log.d("第"+i,"次循环");
                                    String[] riji = lines[i].split("\\*\\*\\*");
                                    Diary diary = new Diary();
                                    diary.setId(Long.parseLong(riji[0]));
                                    diary.setTitle(riji[1]);
                                    diary.setTime(riji[2]);
                                    diary.setContent(riji[3]);
                                    diary.setOne(riji[4]);
                                    diaryList.add(diary);
                                }
                                Collections.reverse(diaryList);
                                adapter.notifyDataSetChanged();
                                break;
                    }
            }
        }
    };

    //默认向b本地数据库diary表中添加20条记录
    private void addDate() {
        String s = getSharedPreferences("s", 0).getString("s", "no"); //默认只添加20条记录
        if (!s.equals("youle")) {
            for (int i = 20; i > 0; i--) {
                Diary diary = new Diary();
                diary.setTitle("第" + i + "条日记");
                diary.setContent("简单点说话的方式简单点，递进的情绪请省略，你又不是个演员，别设计那些情节，没意见我只想看看你怎么圆，你难过的太表面 像没天赋的演员，观众一眼能看见，该配合你演出的我演视而不见，在逼一个最爱你的人即兴表演，什么时候我们开始收起了底线，顺应时代的改变看那些拙劣的表演，可你曾经那么爱我干嘛演出细节，我该变成什么样子才能延缓厌倦，原来当爱放下防备后的这些那些\n" +
                        "才是考验，没意见你想怎样我都随便，你演技也有限，又不用说感言，分开就平淡些，该配合你演出的我演视而不见，别逼一个最爱你的人即兴表演，什么时候我们开始没有了底线，顺着别人的谎言被动就不显得可怜，可你曾经那么爱我干嘛演出细节，我该变成什么样子才能配合出演，原来当爱放下防备后的这些那些，都有个期限，其实台下的观众就我一个，其实我也看出你有点不舍，场景也习惯我们来回拉扯，还计较着什么，其实说分不开的也不见得，其实感情最怕的就是拖着，越演到重场戏越哭不出了，是否还值得，该配合你演出的我尽力在表演，像情感节目里的嘉宾任人挑选，如果还能看出我有爱你的那面，请剪掉那些情节让我看上去体面，可你曾经那么爱我干嘛演出细节，不在意的样子是我最后的表演，是因为爱你我才选择表演这种成全");
                diary.setTime(AddActivity.getTime());
                diary.setOne("第一张表的数据");
                diary.save();
            }

            getSharedPreferences("s", 0).edit().putString("s", "youle").apply();
        }
    }

    public void Update() {
        diaryList.clear();
        diaryList.addAll(DataSupport.findAll(Diary.class));     //这里只能用addAll()方法使list数据源对象始终保持一致，否则 adapter.notifyDataSetChanged()无效
        Collections.reverse(diaryList);
        adapter.notifyDataSetChanged();
    }

    public void Totop() {
        diaryList.clear();
        diaryList2.clear();
        paixuDiaries.clear();
        paixuDiaries.addAll(DataSupport.findAll(PaixuDiary.class));
        for (int i = paixuDiaries.size() - 1; i >= 0; i--) {
            Diary diary = new Diary();
            diary.setTime(paixuDiaries.get(i).getTime());
            diary.setTitle(paixuDiaries.get(i).getTitle());
            diary.setContent(paixuDiaries.get(i).getContent());
            diary.setId(paixuDiaries.get(i).getId());   //这里如果不设置这个对象的ID,那么添加到diaryList中的对象的ID默认都是0
            diary.setOne("第二张表的数据");
            diaryList.add(diary);
        }
        diaryList2.addAll(DataSupport.findAll(Diary.class));
        Collections.reverse(diaryList2);
        diaryList.addAll(diaryList2);
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.add, R.id.search, R.id.show_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add:
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                break;
            case R.id.search:
                View contentview = LayoutInflater.from(MainActivity.this).inflate(R.layout.search, null);
                final PopupWindow popupWindow = new PopupWindow(contentview, dpToPx(MainActivity.this, 350), ViewGroup.LayoutParams.WRAP_CONTENT);
                final EditText editText = contentview.findViewById(R.id.key);
                Button search = contentview.findViewById(R.id.sousuo);
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String key = editText.getText().toString().trim();
                        if (!TextUtils.isEmpty(key)) {

                            /*
                            if (DataSupport.where("title like ?", "%" + key + "%").find(Diary.class).size() > 0) {
                                diaryList.clear();
                                diaryList.addAll(DataSupport.where("title like ?", "%" + key + "%").find(Diary.class));
                                adapter.notifyDataSetChanged();
                                popupWindow.dismiss();
                                showAll.setVisibility(View.VISIBLE);
                            } else if (DataSupport.where("title like ?", "%" + key + "%").find(PaixuDiary.class).size() > 0) {
                                diaryList.clear();
                                paixuDiaries2.clear();
                                paixuDiaries2.addAll(DataSupport.where("title like ?", "%" + key + "%").find(PaixuDiary.class));      //这部分是本地查询的代码
                                for (int i = 0; i < paixuDiaries2.size(); i++) {
                                    Diary diary = new Diary();
                                    diary.setContent(paixuDiaries2.get(i).getContent());
                                    diary.setTitle(paixuDiaries2.get(i).getTitle());
                                    diary.setTime(paixuDiaries2.get(i).getTime());
                                    diary.setOne("第二张表的数据");
                                    diary.setId(paixuDiaries2.get(i).getId());
                                    diaryList.add(diary);
                                    adapter.notifyDataSetChanged();
                                    popupWindow.dismiss();
                                    showAll.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "查无结果", Toast.LENGTH_LONG).show();
                            }
                            */
                            clientThread.sendMessage("select***select * from diary where title like '%"+key+"%'");
                            popupWindow.dismiss();
                            showAll.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "请输入查询内容", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                popupWindow.setAnimationStyle(R.style.shangxia);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);
                final WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.alpha = 0.4f;
                getWindow().setAttributes(layoutParams);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        layoutParams.alpha = 1.0f;
                        getWindow().setAttributes(layoutParams);
                    }
                });
                popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;
            case R.id.show_all:
                //Totop();
                clientThread.sendMessage("selectall***获取服务器数据");
                showAll.setVisibility(View.GONE);
                break;
        }
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void update() {
        Totop();
    }
}
