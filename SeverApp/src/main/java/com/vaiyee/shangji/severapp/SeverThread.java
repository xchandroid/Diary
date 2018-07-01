package com.vaiyee.shangji.severapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.List;

import sun.nio.cs.ext.GBK;

/**
 * Created by Administrator on 2018/6/12.
 */

public class SeverThread extends Thread {
    private Socket socket;
    private List<Socket> socketList;
    private InputStream in = null;
    private OutputStream out =null;
    private String sss = "17722875627";
    public SeverThread(Socket socket, List<Socket>socketList)
    {
        this.socket = socket;
        this.socketList = socketList;
    }
    @Override
    public void run() {
        try {
            while (true) {   //死循环不断与客户端进行通讯（其实就是交换数据）
                //s = getString();
                //System.out.println("服务器接收到");
                in = socket.getInputStream();
                byte[] bytes = new byte[1024];
                int len = in.read(bytes);
                if (len<0)
                {
                    continue;
                }
                String s = new String(bytes,0,len);
                if (s.equals("退出群聊"))
                {
                    System.out.println(socket.getPort()+"退出群聊");
                    in.close();
                    socket.close();
                    break;
                }
                String name =socket.getPort()+"说："+s;
                /*
                File file = new File("E:/AAA/"+"客户端发来的数据.txt");
                if (!file.exists())
                {
                    File file1 = new File(file.getParent());
                    file1.mkdir();
                }

                long l = file.length();
                RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
                randomAccessFile.seek(l);
                randomAccessFile.write(s.getBytes());
                randomAccessFile.close();
                System.out.println("服务器接收到" + socket.getPort() + "发来的信息：" + s);
                */
                for (int i = 0;i<socketList.size();i++)
                {
                    if (!socketList.get(i).isClosed()) //判断如果socket没关闭（既没有退出群聊才回复），线程共享socketList这个集合
                    {
                        out = socketList.get(i).getOutputStream();
                        out.write(name.getBytes());
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getString()
    {
        String []strings = new String[]{"谢昌宏牛逼","跟你讲个笑话吧，爱笑的孩子运气都不会太差","知道你为什么单身吗？还不是因为你丑","过去已经回不去别再怀念","这是谢昌宏写的程序","学无止境，与时俱进才是王道，不断学习心得技术"};
        int i = (int)Math.random()*5;
        return strings[i];
    }
}
