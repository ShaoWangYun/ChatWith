package com.dxxy.chatwith.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dxxy.chatwith.Constant;
import com.dxxy.chatwith.adapter.FriendAdapter;
import com.dxxy.chatwith.bean.Friend;
import com.dxxy.chatwith.bean.RequestFromClient;
import com.dxxy.chatwith.bean.ResponseFromServer;
import com.dxxy.chatwith.utils.AppUtils;
import com.dxxy.code.chatwith.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//用于显示已添加的好友以及最近一条消息的列表的Activity
public class FriendActivity extends AppCompatActivity {

    private RecyclerView recycler_friend;
    private RecyclerView.LayoutManager mLayoutManager;
    private ResponseFromServer responseFromServer = new ResponseFromServer();
    private List<Friend> friends = new ArrayList<>();
    private FriendAdapter friendAdapter;
    private ImageView img_add_friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        img_add_friend = findViewById(R.id.img_add_friend);
        img_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendActivity.this, FindFriendActivity.class));
            }
        });
        recycler_friend = findViewById(R.id.recycler_friend);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        recycler_friend.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recycler_friend.setHasFixedSize(true);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mRunnable_getFriendList);
    }

    private Handler mHandler = new Handler(){
        //子线程发送消息触发的函数:
        public void handleMessage(Message msg) {
            switch(String.valueOf(msg.obj)){
                case "message1":
                    refreshRecyclerView();
                    break;
                case "message2":
                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("无法获得好友列表信息，请检查与服务器的网络连接");
                    builder.setCancelable(false);
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog alert = builder.create();
                    alert.show();
                    break;
            }
        }
    };

    private Runnable mRunnable_getFriendList = new Runnable() {
        @Override
        public void run() {
            getFriendList();
            mHandler.postDelayed(this, 1000);
        }
    };

    private void getFriendList() {
        try {
            Log.i("debugInfo","发起请求，获取好友列表");
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            Friend friend = new Friend();
            friend.setMyName(Constant.MYNAME);

            RequestFromClient requestFromClient = new RequestFromClient();
            requestFromClient.setResponseType("GETFRIENDLIST");
            requestFromClient.setRequestJson(new Gson().toJson(friend));

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(requestFromClient));
            final Request request = new Request.Builder().url(AppUtils.getIPAddress()).post(requestBody).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                responseFromServer = new Gson().fromJson(responseStr, ResponseFromServer.class);
                if (responseFromServer.getStatusCode() == Constant.SUCCESSFUL) {
                    friends = new Gson().fromJson(responseFromServer.getResponseJson(), new TypeToken<List<Friend>>() {}.getType());
                    if (null != friends && friends.size()>0) {

                        for(int i=0;i<friends.size();i++){
                            Log.i("debugInfo","请求成功，friends "+i+" 为 ： "+friends.get(i).toString());
                        }

                        Message msg = new Message();//创建message对象。
                        msg.obj = "message1"; //为消息设置值
                        mHandler.sendMessage(msg);//使用handler在子线程中发送消息
                    }
                }else{
                    Message msg = new Message();//创建message对象。
                    msg.obj = "message2"; //为消息设置值
                    mHandler.sendMessage(msg);//使用handler在子线程中发送消息
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //刷新recyclerview
    private void refreshRecyclerView() {
        recycler_friend.setVisibility(View.VISIBLE);
        friendAdapter = new FriendAdapter(friends);
        recycler_friend.setAdapter(friendAdapter);
        //定义recyclerview的子项点击事件
        friendAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(FriendActivity.this, ChattingActivity.class);
                intent.putExtra("FriendName", friendAdapter.getItem(position).getFriendName());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mRunnable_getFriendList) {
            mHandler.removeCallbacks(mRunnable_getFriendList);
        }
    }

}
