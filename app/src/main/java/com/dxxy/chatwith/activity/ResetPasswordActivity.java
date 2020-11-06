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

//重置密码Activity
public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView btn_back;
    private EditText et_username;
    private EditText et_password, et_confirm_password;
    private ImageView img_manage_password, img_manage_confirm_password;
    private boolean passwordIsShown = false;
    private Button btn_confirm_reset;
    private Dialog progressDialog;
    private ResponseFromServer responseFromServer = new ResponseFromServer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
    }

    private void initView(){
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_confirm_reset = findViewById(R.id.btn_confirm_reset);
        btn_confirm_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String confirmPassword = et_confirm_password.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                    if (password.equals(confirmPassword)) {
                        new ResetPasswordTask().execute();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this,"两次密码输入不一致，请重试",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this,"请输入完整的账户信息",Toast.LENGTH_SHORT).show();
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
    }

    //用户登录的异步任务
    private class ResetPasswordTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressBarDialog.createLoadingDialog(ResetPasswordActivity.this, "正在重置密码。。。");
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
                Toast.makeText(ResetPasswordActivity.this,"密码重置成功",Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String ResetErrorDescription = responseFromServer.getErrorDescription();
                if (null!=ResetErrorDescription && !ResetErrorDescription.equals("")) {
                    Toast.makeText(ResetPasswordActivity.this,ResetErrorDescription,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResetPasswordActivity.this,"密码重置失败",Toast.LENGTH_SHORT).show();
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
            requestFromClient.setResponseType("RESETPASSWORD");
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
