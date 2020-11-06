package com.dxxy.chatwith;

public class Constant {

    //设置默认的模式为客户端模式，可切换为ServerMode
    public static String CurrentMode = "ClientMode";
    //定义一个全局变量，存储当前登录的用户名
    public static String MYNAME = "";

    //状态码定义
    //1.成功
    public final static int SUCCESSFUL = 0;
    //2.用户登录时，用户不存在
    public final static int USERNOTEXIT = 1;
    //3.用户登录时，账号或密码错误
    public final static int PASSWORDWRONG = 2;
    //4.用户注册时，用户已存在
    public final static int USEREXIST = 3;
    //5.添加好友时，好友已存在好友列表中
    public final static int FRIENDEXIST = 4;
    //6.请求失败
    public final static int FAILED = 5;
    //7.好友请求结果：重复发送
    public final static int COMMITFRIENDAPPLY = 6;
    //8.好友请求结果：同意
    public final static int AGREEFRIENDAPPLY = 7;
    //9.好友请求结果：拒绝
    public final static int REJECTFRIENDAPPLY = 8;

    //定义默认自定义端口
    public final static String DEFAULT_HOST = "192.168.43.1";
    public final static int DEFAULT_PORT = 5555;

}
