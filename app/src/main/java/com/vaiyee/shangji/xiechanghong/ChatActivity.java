package com.vaiyee.shangji.xiechanghong;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.jilu)
    TextView jilu;
    private ClientThread clientThread =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        clientThread =new ClientThread(handler);
        clientThread.start();
    }

   Handler handler = new Handler()
   {
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what) {
               case 1:
                   Bundle bundle = msg.getData();
                   String s = bundle.getString("response");
                   jilu.append(s+"\n");
                   break;
               case 2:
                   finish();
                   break;
           }
       }
   };

    @OnClick(R.id.send)
    public void onViewClicked() {
        if (!TextUtils.isEmpty(content.getText())) {
            clientThread.sendMessage(content.getText().toString());
            content.setText("");
        }
        else
        {
            Toast.makeText(this,"请输入内容",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        clientThread.close();
        super.onDestroy();
    }
}
