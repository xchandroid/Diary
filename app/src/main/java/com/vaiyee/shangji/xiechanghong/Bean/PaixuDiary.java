package com.vaiyee.shangji.xiechanghong.Bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/6/9.
 */

public class PaixuDiary extends DataSupport{
    private String content,title,time;
    private long id;

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    private String one;   //标志是哪张表的数据，用于解决置顶出现的问题
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
