package com.dxxy.chatwith.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dxxy.chatwith.Constant;
import com.dxxy.chatwith.androidserver.MessageServer;
import com.dxxy.code.chatwith.R;

//Server模式Activity
public class ServerActivity extends AppCompatActivity {

    private TextView text_switch_to_client_mode;
    private MessageServer messageServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        initView();

        text_switch_to_client_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServerActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        startAndroidWebServer();
    }

    private void initView() {
        text_switch_to_client_mode = findViewById(R.id.text_switch_to_client_mode);
        text_switch_to_client_mode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        text_switch_to_client_mode.getPaint().setAntiAlias(true);
    }

    //开启Android服务器
    private boolean startAndroidWebServer() {
        try {
            messageServer = new MessageServer(Constant.DEFAULT_HOST,Constant.DEFAULT_PORT);
            messageServer.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //关闭Android服务器
    private boolean stopAndroidWebServer() {
        if (messageServer != null) {
            messageServer.stop();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAndroidWebServer();
    }
}
