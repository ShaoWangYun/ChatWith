package com.dxxy.chatwith.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dxxy.chatwith.Constant;
import com.dxxy.chatwith.adapter.ChatAdapter;
import com.dxxy.chatwith.bean.Friend;
import com.dxxy.chatwith.bean.Message;
import com.dxxy.chatwith.bean.RequestFromClient;
import com.dxxy.chatwith.bean.ResponseFromServer;
import com.dxxy.chatwith.sqlite.local.LitepalOpreator;
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

import static com.dxxy.chatwith.utils.AppUtils.getCurrentDate;

/*
 *  聊天Activity
 */
public class ChattingActivity extends AppCompatActivity {

    private TextView text_chatting_friendName;
    private ResponseFromServer responseFromServer = new ResponseFromServer();
    private String friendName;
    private int lastMessageID = 0;
    private List<Message> newMessages = new ArrayList<>();
    private LitepalOpreator litepalOpreator = new LitepalOpreator();
    private ChatAdapter chatAdapter;
    private RecyclerView recycler_chatting;
    private Button btn_send_message;
    private EditText et_message;
    private String message_send;
    private String Flag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        text_chatting_friendName = findViewById(R.id.text_chatting_friendName);
        Intent intent = getIntent();
        friendName = intent.getStringExtra("FriendName");
        if(!friendName.isEmpty()){
            text_chatting_friendName.setText(friendName);
        }

        recycler_chatting = findViewById(R.id.recycler_chatting);
        et_message = findViewById(R.id.et_message);
        btn_send_message = findViewById(R.id.btn_send_message);
        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message_send = et_message.getText().toString().trim();
                if(!message_send.isEmpty()){
                    new SendMessageTask().execute();
                }else{
                    Toast.makeText(ChattingActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });

        initMessageList();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void initMessageList(){
        //首先从本地回复聊天记录
        Flag = Constant.MYNAME+friendName;
        newMessages = litepalOpreator.getMessageRecordFromLocal(Flag);
        lastMessageID = newMessages.get(newMessages.size()).getId();
        refreshRecyclerView(newMessages);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mRunnable_receiveMessage);
    }

    private Handler mHandler = new Handler(){
        //子线程发送消息触发的函数:
        public void handleMessage(android.os.Message msg) {
            switch(String.valueOf(msg.obj)){
                case "message1":
                    //更新messageid
                    lastMessageID = newMessages.get(newMessages.size()).getId();
                    //保存到
                    saveToLocalLitepal(newMessages);
                    //更新主线程
                    refreshRecyclerView(newMessages);
                    break;
            }
        }
    };
    private Runnable mRunnable_receiveMessage = new Runnable() {
        @Override
        public void run() {
            getReceiveMessage();
            mHandler.postDelayed(this, 500);
        }
    };

    private void getReceiveMessage(){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            RequestFromClient requestFromClient = new RequestFromClient();
            requestFromClient.setResponseType("QUERYNEWMESSAGE");
            requestFromClient.setRequestJson(friendName+"&"+Constant.MYNAME+";"+lastMessageID);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(requestFromClient));
            final Request request = new Request.Builder().url(AppUtils.getIPAddress()).post(requestBody).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                responseFromServer = new Gson().fromJson(responseStr, ResponseFromServer.class);
                newMessages = new Gson().fromJson(responseFromServer.getResponseJson(),new TypeToken<List<Friend>>() {}.getType());
                if(newMessages != null){
                    android.os.Message msg = new android.os.Message();//创建message对象。
                    msg.obj = "message1"; //为消息设置值
                    mHandler.sendMessage(msg);//使用handler在子线程中发送消息
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SendMessageTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(send()){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
            } else {
            }
        }
    }

    private boolean send(){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();


            Message message = new Message();
            message.setFlag(Flag);
            message.setSource(Constant.MYNAME);
            message.setDestination(friendName);
            message.setMessage(message_send);
            message.setDate(getCurrentDate());

            RequestFromClient requestFromClient = new RequestFromClient();
            requestFromClient.setResponseType("SENDMESSAGE");
            requestFromClient.setRequestJson(new Gson().toJson(message));

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(requestFromClient));
            final Request request = new Request.Builder().url(AppUtils.getIPAddress()).post(requestBody).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveToLocalLitepal(List<Message> messages){
        for(int i =0;i<messages.size();i++){
            litepalOpreator.saveMessage(messages.get(i));
        }
    }

    private void refreshRecyclerView(List<Message> messages){
        chatAdapter = new ChatAdapter(messages);
        recycler_chatting.setAdapter(chatAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearRecyclerView();
        if(null!=mRunnable_receiveMessage){
            mHandler.removeCallbacks(mRunnable_receiveMessage);
        }
    }

    //为了避免数据显示重复，每次初始化时都对RecyclerView进行一次初始化
    private void clearRecyclerView(){
        newMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(newMessages);
        recycler_chatting.setAdapter(chatAdapter);
    }

}
