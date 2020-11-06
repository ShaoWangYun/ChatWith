package com.dxxy.chatwith.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxxy.chatwith.Constant;
import com.dxxy.chatwith.bean.RequestFromClient;
import com.dxxy.chatwith.bean.ResponseFromServer;
import com.dxxy.chatwith.bean.User;
import com.dxxy.chatwith.utils.AppUtils;
import com.dxxy.chatwith.widget.ProgressBarDialog;
import com.dxxy.code.chatwith.R;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dxxy.chatwith.Constant.MYNAME;

//登录Activity
public class LoginActivity extends AppCompatActivity {

    private Button btn_login, btn_register;
    private TextView text_resetPassword;
    private TextView text_switch_to_server_mode;
    private EditText et_username, et_password;
    private Dialog progressDialog;
    private Gson gson = new Gson();
    private ResponseFromServer responseFromServer = new ResponseFromServer();
    private CheckBox cb_remember;
    private boolean saveUsername = false;
    private ImageView img_manage_password;
    private boolean passwordIsShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录
                String username = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                if (!username.isEmpty() && !password.isEmpty()) {
                    if (saveUsername) {
                        AppUtils.saveToSP("IsLoginPageCheckboxChecked", "true");
                        AppUtils.saveToSP("theSavingUsername", username);
                    } else {
                        AppUtils.saveToSP("IsLoginPageCheckboxChecked", "false");
                        AppUtils.saveToSP("theSavingUsername", "");
                    }
                    new LoginTask().execute();
                } else {
                    Toast.makeText(LoginActivity.this,"请输入完整的账户信息",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        text_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置密码
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        text_switch_to_server_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ServerActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

        cb_remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveUsername = true;
                } else {
                    saveUsername = false;
                }
            }
        });
    }

    private void initView() {
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        text_resetPassword = findViewById(R.id.text_resetPassword);
        text_switch_to_server_mode = findViewById(R.id.text_switch_to_server_mode);
        text_switch_to_server_mode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        text_switch_to_server_mode.getPaint().setAntiAlias(true);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        img_manage_password = findViewById(R.id.img_manage_password);
        cb_remember = findViewById(R.id.cb_remember);
        cb_remember.setChecked(false);
        if (AppUtils.getFromSP("IsLoginPageCheckboxChecked", "false").equals("true")) {
            cb_remember.setChecked(true);
            et_username.setText(AppUtils.getFromSP("theSavingUsername", ""));
        }
    }

    //用户登录的异步任务
    private class LoginTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressBarDialog.createLoadingDialog(LoginActivity.this, "正在登录。。。");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (doLogin()) {
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
                Intent intent = new Intent(LoginActivity.this, FriendActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
            } else {
                String LoginErrorDescription = responseFromServer.getErrorDescription();
                if (null!=LoginErrorDescription && LoginErrorDescription.equals("")) {
                    Toast.makeText(LoginActivity.this,LoginErrorDescription,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean doLogin() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            String username = et_username.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            final User user = new User();
            user.setUsername(username);
            user.setPassword(AppUtils.md5AddSalt(password, username));

            RequestFromClient requestFromClient = new RequestFromClient();
            requestFromClient.setResponseType("LOGIN");
            requestFromClient.setRequestJson(new Gson().toJson(user));

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(requestFromClient));
            final Request request = new Request.Builder().url(AppUtils.getIPAddress()).post(requestBody).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                responseFromServer = gson.fromJson(responseStr, ResponseFromServer.class);
                if (responseFromServer.getStatusCode() == Constant.SUCCESSFUL) {
                    MYNAME = user.getUsername();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
