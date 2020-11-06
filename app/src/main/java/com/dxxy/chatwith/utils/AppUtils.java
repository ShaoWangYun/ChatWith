package com.dxxy.chatwith.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.dxxy.chatwith.ChatApplication;
import com.dxxy.chatwith.Constant;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.WIFI_SERVICE;
import static com.dxxy.chatwith.Constant.AGREEFRIENDAPPLY;
import static com.dxxy.chatwith.Constant.COMMITFRIENDAPPLY;
import static com.dxxy.chatwith.Constant.FAILED;
import static com.dxxy.chatwith.Constant.FRIENDEXIST;
import static com.dxxy.chatwith.Constant.PASSWORDWRONG;
import static com.dxxy.chatwith.Constant.USEREXIST;
import static com.dxxy.chatwith.Constant.USERNOTEXIT;

public class AppUtils {

    private static Context context = ChatApplication.getInstance();

    //保存数据到SharedPreference
    public static void saveToSP(String tag, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString(tag, value);
        editor.commit();
    }

    //从SharedPreference读取特定tag的值
    public static String getFromSP(String tag, String def) {
        String tempStr;
        SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        tempStr = sp.getString(tag, def);
        return tempStr;
    }

    //获取服务器的ip地址
    public static String getIPAddress() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String IPAddress = intToIp(wifiInfo.getIpAddress());
        DhcpInfo dhcpinfo = wifiManager.getDhcpInfo();
        String serverAddress = intToIp(dhcpinfo.serverAddress);
        return "http://" + serverAddress + ":" + Constant.DEFAULT_PORT;
    }

    private static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    public static String md5AddSalt(String string, String salt) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest((string + salt).getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getErrorDescriptionFromStatusCode(int statusCode) {
        String ErrorDescription = "请求失败";
        switch (statusCode) {
            case USERNOTEXIT:
                ErrorDescription = "用户不存在";
                break;
            case PASSWORDWRONG:
                ErrorDescription = "用户名或密码错误";
                break;
            case USEREXIST:
                ErrorDescription = "用户已存在";
                break;
            case FRIENDEXIST:
                ErrorDescription = "该用户已是您的好友";
                break;
            case COMMITFRIENDAPPLY:
                ErrorDescription = "不能重复发送好友请求";
            case AGREEFRIENDAPPLY:
                ErrorDescription = "该用户已是您的好友";
            case FAILED:
                break;
        }
        return ErrorDescription;
    }

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

}
