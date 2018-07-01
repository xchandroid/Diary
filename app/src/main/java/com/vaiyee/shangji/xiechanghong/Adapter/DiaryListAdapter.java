package com.vaiyee.shangji.xiechanghong.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.vaiyee.shangji.xiechanghong.AddActivity;
import com.vaiyee.shangji.xiechanghong.Bean.Diary;
import com.vaiyee.shangji.xiechanghong.Bean.PaixuDiary;
import com.vaiyee.shangji.xiechanghong.ClientThread;
import com.vaiyee.shangji.xiechanghong.R;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/8.
 */

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.ViewHolder> {
    private Context context;
    private List<Diary> diaryList;
    private UpDateListener listener;
    private ClientThread clientThread;
    public DiaryListAdapter(Context context, List<Diary> diaryList, ClientThread clientThread) {
        this.context = context;
        this.diaryList = diaryList;
        this.clientThread = clientThread;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.riji_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Diary diary = diaryList.get(position);
        holder.title.setText(diary.getTitle());
        holder.time.setText(diary.getTime());
        holder.content.setText(diary.getContent());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (diary.getOne().equals("第一张表的数据")) {       //这里是删除本地数据库的数据
                    DataSupport.delete(Diary.class, diary.getId());
                }
                else
                {
                    DataSupport.delete(PaixuDiary.class,diary.getId());
                }

                diaryList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,diaryList.size()-position);  //防止viewHolder复用position位置监听错乱
                clientThread.sendMessage("delete***delete from diary where id ="+diary.getId());
            }
        });
        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddActivity.class);
                intent.putExtra("diary",diary);
                intent.putExtra("cd","查看");
                context.startActivity(intent);
            }
        });
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddActivity.class);
                intent.putExtra("diary",diary);
                intent.putExtra("cd","修改");
                context.startActivity(intent);
            }
        });
        /*
        holder.toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaixuDiary paixuDiary = new PaixuDiary();
                paixuDiary.setContent(diary.getContent());
                paixuDiary.setTitle(diary.getTitle());
                paixuDiary.setTime(diary.getTime());
                paixuDiary.setOne("第二张表的数据");
                //paixuDiary.setId(diary.getId());   //向表中新增数据不用设置ID，默认Id列自增
                paixuDiary.save();
                if (diary.getOne().equals("第一张表的数据"))  //如果这条数据是第一张表的数据              //这段代码是实现单机版的置顶功能用的
                {
                    DataSupport.delete(Diary.class, diary.getId()); //删除第一张表中相应的行
                }
                else   //否则是第二张表的数据
                {
                    DataSupport.delete(PaixuDiary.class,diary.getId()); //删除第二张表的数据
                }
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,diaryList.size()-position);  //防止viewHolder复用position位置监听错乱
                listener.update();  //这里的置顶采用了两张表来实现
            }
        });

        */

    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    public void setListener(UpDateListener listener)
    {
        this.listener = listener;
    }
    public interface UpDateListener
    {
        void update();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.update)
        ImageView update;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.framelayout)
        FrameLayout frameLayout;
      public  ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
