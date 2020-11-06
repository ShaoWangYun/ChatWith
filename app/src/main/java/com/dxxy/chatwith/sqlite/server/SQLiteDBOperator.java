package com.dxxy.chatwith.sqlite.server;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dxxy.chatwith.bean.Friend;
import com.dxxy.chatwith.bean.Message;
import com.dxxy.chatwith.bean.User;

import java.util.ArrayList;
import java.util.List;

import static com.dxxy.chatwith.Constant.FAILED;
import static com.dxxy.chatwith.Constant.PASSWORDWRONG;
import static com.dxxy.chatwith.Constant.REJECTFRIENDAPPLY;
import static com.dxxy.chatwith.Constant.SUCCESSFUL;
import static com.dxxy.chatwith.Constant.USERNOTEXIT;

public class SQLiteDBOperator {

    private SQLiteDBHelper sqLiteDBHelper;
    private SQLiteDatabase db;

    public SQLiteDBOperator(Context context) {
        //数据库名：db_sqlite
        sqLiteDBHelper = new SQLiteDBHelper(context, "db_sqlite", null, 1);
        //初始化数据库操作对象
        db = sqLiteDBHelper.getWritableDatabase();
    }

    //-------------------------------------------USER-----------------------------------------------
    //注册用户
    public void register(User user_add) {
        db.execSQL("insert into User(username,password) values(?,?)"
                , new Object[] { user_add.getUsername(), user_add.getPassword()});
    }

    //修改密码
    public void resetPassword(User user_update) {
        db.execSQL("update User set password=? where username=?",
                new Object[] { user_update.getPassword(), user_update.getUsername()});
    }

    //用户登录
    public int login(User user_query) {
        User user = null;
        Cursor c = db.rawQuery("select * from User where username= ?", new String[] { user_query.getUsername() });
        while (c.moveToNext()) {
            user = new User();
            user.setUsername(c.getString(1));
            user.setPassword(c.getString(2));
        }
        c.close();
        if(user == null){
            //用户不存在
            return USERNOTEXIT;
        }else{
            String password = user.getPassword();
            String password_sql = user_query.getPassword();
            if(password.equals(password_sql)){
                //用户登录验证通过
                return SUCCESSFUL;
            }else{
                //账户或密码错误（密码错误）
                return PASSWORDWRONG;
            }
        }
    }

    //查找用户
    public boolean findUser(String username){
        try {
            User user = null;
            Cursor c = db.rawQuery("select * from User where username= ?", new String[]{username});
            while (c.moveToNext()) {
                user = new User();
                user.setUsername(c.getString(1));
                user.setPassword(c.getString(2));
            }
            c.close();
            if (user != null) {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //-----------------------------------------Message----------------------------------------------
    //保存信息
    public void sendMessage(Message message_add) {
        db.execSQL("insert into Message(flag,source,destination,message,date) values(?,?,?,?,?)"
                , new Object[] { message_add.getFlag(),message_add.getSource(), message_add.getDestination(),message_add.getMessage(),message_add.getDate()});
    }

    //获取新消息
    public List<Message> getNewMessage(String flag,String lastMessageID) {
        List<Message> list = new ArrayList<>();
        Cursor c = db.rawQuery("select * from Message where flag =? and id>? order by id",new String[] { flag,lastMessageID});
        while (c.moveToNext()) {
            Message message = new Message();
            message.setId(c.getInt(0));
            message.setFlag(c.getString(1));
            message.setSource(c.getString(2));
            message.setDestination(c.getString(3));
            message.setMessage(c.getString(4));
            message.setDate(c.getString(5));
            list.add(message);
        }
        c.close();
        return list;
    }

    //--------------------------------------------Friend--------------------------------------------
    //添加好友(在对方同意之后的动作，如果对方拒绝，将不会执行该方法)
    public int commitFriendApply(Friend friend_add) {
       try {
           //1. 查询自己和用户的好友关系
           Cursor c2 = db.rawQuery("select * from Friend where myName = ? and friendName = ?",
                   new String[]{friend_add.getMyName(), friend_add.getFriendName()});
           Friend friend = null;
           while (c2.moveToNext()) {
               friend = new Friend();
               friend.setMyName(c2.getString(1));
               friend.setFriendName(c2.getString(2));
               friend.setFriendStatus(c2.getInt(3));
           }
           c2.close();
           if(null!=friend){
                //2. 如果数据库中存在和改名用户的关系，证明之前已经申请过，结果有三种：等待、同意、拒绝
               //等待状态（好友申请不能重复发送）
               //同意状态（该用户已是您的好友）
               //拒绝状态（好友申请之前被拒绝过，可以再次发起申请）
               if(friend.getFriendStatus() == REJECTFRIENDAPPLY){
                   //向好友发送请求
                   db.execSQL("insert into Friend(myName,friendName,friendStatus) values(?,?,?)"
                           , new Object[]{friend_add.getMyName(), friend_add.getFriendName(), friend_add.getFriendStatus()});
                   return SUCCESSFUL;
               }else{
                   return friend.getFriendStatus();
               }
           }else{
               //向好友发送请求
               db.execSQL("insert into Friend(myName,friendName,friendStatus) values(?,?,?)"
                       , new Object[]{friend_add.getMyName(), friend_add.getFriendName(), friend_add.getFriendStatus()});
               return SUCCESSFUL;
           }
       }catch (Exception e){
           e.printStackTrace();
           return FAILED;
       }
    }

    //更新好友状态：同意 1 、拒绝 2 、等待 0
    public int agreeFriendApply(Friend friend_update){
        try {
            //更新好友于我的状态
            db.execSQL("update Friend set friendStatus=? where myName=? and friendName=?",
                    new Object[]{friend_update.getFriendStatus(), friend_update.getMyName(), friend_update.getFriendName()});
            //更新我于好友的状态
            db.execSQL("update Friend set friendStatus=? where myName=? and friendName=?",
                    new Object[]{friend_update.getFriendStatus(), friend_update.getFriendName(), friend_update.getMyName()});
            return SUCCESSFUL;
        }catch (Exception e){
            e.printStackTrace();
            return FAILED;
        }
    }

    public int rejectFriendApply(Friend friend_update){
        try {
            db.execSQL("update Friend set friendStatus=? where myName=? and friendName=?",
                    new Object[]{friend_update.getFriendStatus(), friend_update.getMyName(), friend_update.getFriendName()});
            return SUCCESSFUL;
        }catch (Exception e){
            e.printStackTrace();
            return FAILED;
        }
    }

    //获取所有好友申请列表
    public List<Friend> getAllFriendAppliesList(String myName) {
        //输入自己的名字，但是需要查询的是所有申请里面friendName = myName的数据，因为对于对方发起的好友请求而言，自己是friend
        List<Friend> list = new ArrayList<>();
        Cursor c = db.rawQuery("select * from Friend where friendName=? and friendStatus = 6",new String[] {myName});
        while (c.moveToNext()) {
            Friend friend = new Friend();
            friend.setMyName(c.getString(1));
            friend.setFriendName(c.getString(2));
            friend.setFriendStatus(c.getInt(3));
            list.add(friend);
        }
        c.close();
        return list;
    }

    //获取所有好友列表
    public List<Friend> getAllFriend(String myName) {
        List<Friend> list = new ArrayList<>();
        Cursor c = db.rawQuery("select * from Friend where myName=? and friendStatus = 7",new String[] {myName});
        while (c.moveToNext()) {
            Friend friend = new Friend();
            friend.setMyName(c.getString(1));
            friend.setFriendName(c.getString(2));
            friend.setFriendStatus(c.getInt(3));
            list.add(friend);
        }
        c.close();
        return list;
    }

}
