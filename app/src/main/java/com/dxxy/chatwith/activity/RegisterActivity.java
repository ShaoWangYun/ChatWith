package com.dxxy.chatwith.activity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dxxy.chatwith.Constant;
import com.dxxy.chatwith.bean.RequestFromClient;
import com.dxxy.chatwith.bean.ResponseFromServer;
import com.dxxy.chatwith.bean.User;
import com.dxxy.chatwith.utils.AppUtils;
import com.dxxy.chatwith.widget.ProgressBarDialog;
import com.dxxy.code.chatwith.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//注册Activity
public class RegisterActivity extends AppCompatActivity {

    private ImageView btn_back;
    private Dialog progressDialog;
    private ResponseFromServer responseFromServer = new ResponseFromServer();
    private EditText et_username, et_password, et_confirm_password;
    private Button btn_confirm_register;
    private ImageView img_manage_password, img_manage_confirm_password;
    private boolean passwordIsShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        btn_confirm_register = findViewById(R.id.btn_confirm_register);
        btn_confirm_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String confirmPassword = et_confirm_password.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                    if (password.equals(confirmPassword)) {
                        new RegisterTask().execute();
                    } else {
                        Toast.makeText(RegisterActivity.this,"两次密码输入不一致，请重试",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this,"请输入完整的账户信息",Toast.LENGTH_SHORT).show();
                }
            }
        });

        img_manage_password = findViewById(R.id.img_manage_password);
        img_manage_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordIsShown) {
                    //如果当前密码是显示状态，点击时将设置密码不可见，并且源图片变为“隐藏密码”状态图
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    img_manage_password.setImageDrawable(getResources().getDrawable(R.drawable.hidepassword));
                    passwordIsShown = false;
                } else {
                    //如果当前密码是隐藏状态，点击时将设置密码可见，并且原图片变为“显示密码”状态图
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    img_manage_password.setImageDrawable(getResources().getDrawable(R.drawable.showwpassword));
                    passwordIsShown = true;
                }
            }
        });

        img_manage_confirm_password = findViewById(R.id.img_manage_confirm_password);
        img_manage_confirm_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordIsShown) {
                    //如果当前密码是显示状态，点击时将设置密码不可见，并且源图片变为“隐藏密码”状态图
                    et_confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    img_manage_confirm_password.setImageDrawable(getResources().getDrawable(R.drawable.hidepassword));
                    passwordIsShown = false;
                } else {
                    //如果当前密码是隐藏状态，点击时将设置密码可见，并且原图片变为“显示密码”状态图
                    et_confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    img_manage_confirm_password.setImageDrawable(getResources().getDrawable(R.drawable.showwpassword));
                    passwordIsShown = true;
                }
            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //用户登录的异步任务
    private class RegisterTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressBarDialog.createLoadingDialog(RegisterActivity.this, "正在注册。。。");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (doRegister()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (result) {
                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String RegisterErrorDescription = responseFromServer.getErrorDescription();
                if (null!= RegisterErrorDescription && !RegisterErrorDescription.equals("")) {
                    Toast.makeText(RegisterActivity.this,RegisterErrorDescription,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean doRegister() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            String username = et_username.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            User user = new User();
            user.setUsername(username);
            user.setPassword(AppUtils.md5AddSalt(password, username));

            RequestFromClient requestFromClient = new RequestFromClient();
            requestFromClient.setResponseType("REGISTER");
            requestFromClient.setRequestJson(new Gson().toJson(user));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
