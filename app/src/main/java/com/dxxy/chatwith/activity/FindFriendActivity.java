package com.dxxy.chatwith.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dxxy.chatwith.Constant;
import com.dxxy.chatwith.adapter.FriendApplyAdapter;
import com.dxxy.chatwith.bean.Friend;
import com.dxxy.chatwith.bean.RequestFromClient;
import com.dxxy.chatwith.bean.ResponseFromServer;
import com.dxxy.chatwith.utils.AppUtils;
import com.dxxy.chatwith.widget.ProgressBarDialog;
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

/**
 * 用于添加好友的Activity
 */

public class FindFriendActivity extends AppCompatActivity {

    private Button btn_confirm_add_friend;
    private EditText et_username;
    private ImageView btn_back;
    private ResponseFromServer responseFromServer = new ResponseFromServer();
    private List<Friend> friendapplies = new ArrayList<>();
    private RecyclerView recycler_friend_applies_list;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView img_refresh_friend_applies_list;
    private FriendApplyAdapter friendApplyAdapter;
    private String friendName_selected = "";
    private Dialog progressDialog_commit,progressDialog_agree,progressDialog_reject,progressDialog_getlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        initView();

        new GetFriendAppliesListTask().execute();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void initView(){
        et_username = findViewById(R.id.et_username);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_confirm_add_friend = findViewById(R.id.btn_confirm_add_friend);
        btn_confirm_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendName = et_username.getText().toString().trim();
                if(!friendName.isEmpty()){
                    if(!friendName.equals(Constant.MYNAME)){
                        new CommitFriendApplyTask().execute();
                    }else{
                        Toast.makeText(FindFriendActivity.this,"不能添加自己为好友",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(FindFriendActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });

        img_refresh_friend_applies_list = findViewById(R.id.img_refresh_friend_applies_list);
        img_refresh_friend_applies_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetFriendAppliesListTask();
            }
        });

        recycler_friend_applies_list = findViewById(R.id.recycler_friend_applies_list);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        recycler_friend_applies_list.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recycler_friend_applies_list.setHasFixedSize(true);

    }

    //提交好友请求
    class CommitFriendApplyTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog_commit = ProgressBarDialog.createLoadingDialog(FindFriendActivity.this, "");
            progressDialog_commit.setCancelable(false);
            progressDialog_commit.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(commitFriendApply()){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(null!=progressDialog_commit){
                progressDialog_commit.dismiss();
            }
            if (result) {
                Toast.makeText(FindFriendActivity.this,"已经发送申请，请耐心等待",Toast.LENGTH_SHORT).show();
            } else {
                String RegisterErrorDescription = responseFromServer.getErrorDescription();
                if (null!= RegisterErrorDescription && !RegisterErrorDescription.equals("")) {
                    Toast.makeText(FindFriendActivity.this,RegisterErrorDescription,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FindFriendActivity.this,"发送好友申请失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //同意好友请求
    class AgreeFriendApplyTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog_agree = ProgressBarDialog.createLoadingDialog(FindFriendActivity.this, "");
            progressDialog_agree.setCancelable(false);
            progressDialog_agree.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(agreeFriendApply()){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(null!=progressDialog_agree){
                progressDialog_agree.dismiss();
            }
            if (result) {
                Toast.makeText(FindFriendActivity.this,"添加好友成功",Toast.LENGTH_SHORT).show();
            } else {
                String RegisterErrorDescription = responseFromServer.getErrorDescription();
                if (null!= RegisterErrorDescription && !RegisterErrorDescription.equals("")) {
                    Toast.makeText(FindFriendActivity.this,RegisterErrorDescription,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FindFriendActivity.this,"添加好友失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //拒绝好友请求
    class RejectFriendApplyTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog_reject = ProgressBarDialog.createLoadingDialog(FindFriendActivity.this, "");
            progressDialog_reject.setCancelable(false);
            progressDialog_reject.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(rejectFriendApply()){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(null!=progressDialog_reject){
                progressDialog_reject.dismiss();
            }
            if (result) {
                Toast.makeText(FindFriendActivity.this,"已拒绝该好友请求",Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String RegisterErrorDescription = responseFromServer.getErrorDescription();
                if (null!= RegisterErrorDescription && !RegisterErrorDescription.equals("")) {
                    Toast.makeText(FindFriendActivity.this,RegisterErrorDescription,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FindFriendActivity.this,"通信失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //获取好友申请列表
    class GetFriendAppliesListTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog_getlist = ProgressBarDialog.createLoadingDialog(FindFriendActivity.this, "");
            progressDialog_getlist.setCancelable(false);
            progressDialog_getlist.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(getFriendAppliesList()){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(null!=progressDialog_getlist){
                progressDialog_getlist.dismiss();
            }
            if (result) {
                refreshRecyclerView(friendapplies);
            } else {
                String RegisterErrorDescription = responseFromServer.getErrorDescription();
                if (null!= RegisterErrorDescription && !RegisterErrorDescription.equals("")) {
                    Toast.makeText(FindFriendActivity.this,RegisterErrorDescription,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FindFriendActivity.this,"通信失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //提交好友申请
    private boolean commitFriendApply(){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            String friendName = et_username.getText().toString().trim();

            Friend friend = new Friend();
            friend.setMyName(Constant.MYNAME);
            friend.setFriendName(friendName);
            friend.setFriendStatus(Constant.COMMITFRIENDAPPLY);

            RequestFromClient requestFromClient = new RequestFromClient();
            requestFromClient.setResponseType("COMMITFRIENDAPPLY");
            requestFromClient.setRequestJson(new Gson().toJson(friend));

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(requestFromClient));
            final Request request = new Request.Builder().url(AppUtils.getIPAddress()).post(requestBody).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                responseFromServer = new Gson().fromJson(responseStr, ResponseFromServer.class);
                if (responseFromServer.getStatusCode() != Constant.FAILED) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //同意别人的好友请求
    private boolean agreeFriendApply(){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            Friend friend = new Friend();
            friend.setMyName(Constant.MYNAME);
            friend.setFriendName(friendName_selected);
            friend.setFriendStatus(Constant.AGREEFRIENDAPPLY);

            RequestFromClient requestFromClient = new RequestFromClient();
            requestFromClient.setResponseType("AGREEFRIENDAPPLY");
            requestFromClient.setRequestJson(new Gson().toJson(friend));

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(requestFromClient));
            final Request request = new Request.Builder().url(AppUtils.getIPAddress()).post(requestBody).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                responseFromServer = new Gson().fromJson(responseStr, ResponseFromServer.class);
                if (responseFromServer.getStatusCode() == Constant.SUCCESSFUL) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //拒绝别人的好友请求
    private boolean rejectFriendApply(){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            Friend friend = new Friend();
            friend.setMyName(Constant.MYNAME);
            friend.setFriendName(friendName_selected);
            friend.setFriendStatus(Constant.REJECTFRIENDAPPLY);

            RequestFromClient requestFromClient = new RequestFromClient();
            requestFromClient.setResponseType("REJECTFRIENDAPPLY");
            requestFromClient.setRequestJson(new Gson().toJson(friend));

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(requestFromClient));
            final Request request = new Request.Builder().url(AppUtils.getIPAddress()).post(requestBody).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                responseFromServer = new Gson().fromJson(responseStr, ResponseFromServer.class);
                if (responseFromServer.getStatusCode() == Constant.SUCCESSFUL) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //拉取好友请求列表
    private boolean getFriendAppliesList(){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            Friend friend = new Friend();
            friend.setMyName(Constant.MYNAME);

            RequestFromClient requestFromClient = new RequestFromClient();
            requestFromClient.setResponseType("GETALLFRIENDAPPLIESLIST");
            requestFromClient.setRequestJson(new Gson().toJson(friend));

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(requestFromClient));
            final Request request = new Request.Builder().url(AppUtils.getIPAddress()).post(requestBody).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                responseFromServer = new Gson().fromJson(responseStr, ResponseFromServer.class);
                if (responseFromServer.getStatusCode() == Constant.SUCCESSFUL) {
                    friendapplies = new Gson().fromJson(responseFromServer.getResponseJson(),new TypeToken<List<Friend>>() {}.getType());
                    if(null!=friendapplies && friendapplies.size()>0){
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //刷新recyclerview
    private void refreshRecyclerView(List<Friend> friendapplies_new) {
        friendapplies.clear();
        friendApplyAdapter = new FriendApplyAdapter(friendapplies);
        recycler_friend_applies_list.setAdapter(friendApplyAdapter);

        friendapplies = friendapplies_new;
        friendApplyAdapter = new FriendApplyAdapter(friendapplies);
        recycler_friend_applies_list.setAdapter(friendApplyAdapter);

        //定义recyclerview的子项点击事件
        friendApplyAdapter.setOnItemClickListener(new FriendApplyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                friendName_selected = friendApplyAdapter.getItem(position).getMyName().toString().trim();
                AlertDialog.Builder builder = new AlertDialog.Builder(FindFriendActivity.this);
                builder.setTitle("提示");
                builder.setMessage("同意添加 "+friendName_selected+" 为好友么？");
                builder.setCancelable(false);
                builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new AgreeFriendApplyTask().execute();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new RejectFriendApplyTask().execute();
                        dialog.dismiss();
                    }
                });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=progressDialog_commit){
            progressDialog_commit.dismiss();
        }
        if(null!=progressDialog_agree){
            progressDialog_agree.dismiss();
        }
        if(null!=progressDialog_reject){
            progressDialog_reject.dismiss();
        }
        if(null!=progressDialog_getlist){
            progressDialog_getlist.dismiss();
        }
    }
}
