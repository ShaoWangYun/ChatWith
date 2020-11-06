package com.dxxy.chatwith.androidserver;

import android.util.Log;

import com.dxxy.chatwith.ChatApplication;
import com.dxxy.chatwith.Constant;
import com.dxxy.chatwith.bean.Friend;
import com.dxxy.chatwith.bean.Message;
import com.dxxy.chatwith.bean.RequestFromClient;
import com.dxxy.chatwith.bean.ResponseFromServer;
import com.dxxy.chatwith.bean.User;
import com.dxxy.chatwith.sqlite.server.SQLiteDBOperator;
import com.dxxy.chatwith.utils.AppUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MessageServer extends NanoHTTPD {
    //用户上传请求参数数据
    private String requestJson = "";
    //定义请求类型
    private String responseType;
    private Gson gson = new Gson();
    private SQLiteDBOperator sqLiteDBOperator = new SQLiteDBOperator(ChatApplication.getInstance());

    //定义日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MessageServer(String host, int port) {
        super(host, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        //从这里可以接收到客户端上传的所有的参数
        Method method = session.getMethod();
        if (method.equals(Method.POST)) {
            Map<String, String> files = new HashMap<String, String>();
            /*获取header信息，NanoHttp的header不仅仅是HTTP的header，还包括其他信息。*/
            Map<String, String> header = session.getHeaders();

            try {
                session.parseBody(files);
                requestJson = files.get("postData");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }

        }
        RequestFromClient requestFromClient = new RequestFromClient();
        requestFromClient = new Gson().fromJson(requestJson, RequestFromClient.class);
        responseType = requestFromClient.getResponseType();

        //User部分
        if (responseType.equals("REGISTER")) {
            //注册
            User user = gson.fromJson(requestFromClient.getRequestJson(), User.class);
            ResponseFromServer responseFromServer = new ResponseFromServer();
            if (sqLiteDBOperator.findUser(user.getUsername())) {
                responseFromServer.setStatusCode(Constant.USEREXIST);
                responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(Constant.USEREXIST));
                responseFromServer.setResponseJson("");
            } else {
                sqLiteDBOperator.register(user);
                responseFromServer.setStatusCode(Constant.SUCCESSFUL);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson("");
            }
            return newFixedLengthResponse(gson.toJson(responseFromServer));
        } else if (responseType.equals("LOGIN")) {
            //登录
            User user = gson.fromJson(requestFromClient.getRequestJson(), User.class);
            ResponseFromServer responseFromServer = new ResponseFromServer();
            int statusCode = sqLiteDBOperator.login(user);
            if (statusCode == Constant.SUCCESSFUL) {
                responseFromServer.setStatusCode(statusCode);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson("");
            } else {
                responseFromServer.setStatusCode(statusCode);
                responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(statusCode));
                responseFromServer.setResponseJson("");
            }
            return newFixedLengthResponse(gson.toJson(responseFromServer));
        } else if (responseType.equals("RESETPASSWORD")) {
            //重置密码
            User user = gson.fromJson(requestFromClient.getRequestJson(), User.class);
            ResponseFromServer responseFromServer = new ResponseFromServer();
            if (sqLiteDBOperator.findUser(user.getUsername())) {
                sqLiteDBOperator.resetPassword(user);
                responseFromServer.setStatusCode(Constant.SUCCESSFUL);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson("");
            } else {
                responseFromServer.setStatusCode(Constant.USERNOTEXIT);
                responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(Constant.USERNOTEXIT));
                responseFromServer.setResponseJson("");
            }
            return newFixedLengthResponse(gson.toJson(responseFromServer));
        }


        //Friend部分
        else if (responseType.equals("COMMITFRIENDAPPLY")) {
            //提交好友申请
            //(被申请好友方)添加好友
            Friend friend = gson.fromJson(requestFromClient.getRequestJson(), Friend.class);
            Log.i("debugInfo","COMMITFRIENDAPPLY friend is : "+friend.toString());
            ResponseFromServer responseFromServer = new ResponseFromServer();
            if (sqLiteDBOperator.findUser(friend.getFriendName())) {
                //用户存在
                Log.i("debugInfo","用户存在");
                int statusCode = sqLiteDBOperator.commitFriendApply(friend);
                Log.i("debugInfo","COMMITFRIENDAPPLY statusCode is : "+statusCode);
                if(statusCode == Constant.SUCCESSFUL){
                    responseFromServer.setStatusCode(statusCode);
                    responseFromServer.setErrorDescription("");
                    responseFromServer.setResponseJson("");
                }else{
                    responseFromServer.setStatusCode(statusCode);
                    responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(statusCode));
                    responseFromServer.setResponseJson("");
                }
            } else {
                //用户不存在
                Log.i("debugInfo","用户不存在");
                responseFromServer.setStatusCode(Constant.USERNOTEXIT);
                responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(Constant.USERNOTEXIT));
                responseFromServer.setResponseJson("");
            }
            String response = gson.toJson(responseFromServer);
            Log.i("debugInfo","COMMITFRIENDAPPLY response is : "+response);
            return newFixedLengthResponse(response);
        }else if (responseType.equals("AGREEFRIENDAPPLY")) {
            //同意别人的好友申请
            Friend friend = gson.fromJson(requestFromClient.getRequestJson(), Friend.class);
            Log.i("debugInfo","AGREEFRIENDAPPLY friend is : "+friend.toString());
            ResponseFromServer responseFromServer = new ResponseFromServer();
            int statusCode = sqLiteDBOperator.agreeFriendApply(friend);
            Log.i("debugInfo","AGREEFRIENDAPPLY statusCode is : "+statusCode);
            if(statusCode == Constant.SUCCESSFUL){
                responseFromServer.setStatusCode(statusCode);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson("");
            }else{
                responseFromServer.setStatusCode(statusCode);
                responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(statusCode));
                responseFromServer.setResponseJson("");
            }
            String response = gson.toJson(responseFromServer);
            Log.i("debugInfo","AGREEFRIENDAPPLY response is : "+response);
            return newFixedLengthResponse(response);
        }else if (responseType.equals("REJECTFRIENDAPPLY")) {
            //拒绝别人的好友申请
            Friend friend = gson.fromJson(requestFromClient.getRequestJson(), Friend.class);
            Log.i("debugInfo","REJECTFRIENDAPPLY friend is : "+friend.toString());
            ResponseFromServer responseFromServer = new ResponseFromServer();
            int statusCode = sqLiteDBOperator.rejectFriendApply(friend);
            Log.i("debugInfo","REJECTFRIENDAPPLY statusCode is : "+statusCode);
            if(statusCode == Constant.SUCCESSFUL){
                responseFromServer.setStatusCode(statusCode);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson("");
            }else{
                responseFromServer.setStatusCode(statusCode);
                responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(statusCode));
                responseFromServer.setResponseJson("");
            }
            String response = gson.toJson(responseFromServer);
            Log.i("debugInfo","REJECTFRIENDAPPLY response is : "+response);
            return newFixedLengthResponse(response);
        }else if (responseType.equals("GETALLFRIENDAPPLIESLIST")) {
            //获取所有的好友申请列表
            Friend friend = gson.fromJson(requestFromClient.getRequestJson(), Friend.class);
            Log.i("debugInfo","GETALLFRIENDAPPLIESLIST friend is : "+friend.toString());
            String myName = friend.getMyName();
            List<Friend> friends = sqLiteDBOperator.getAllFriendAppliesList(myName);

            for(int i=0;i<friends.size();i++){
                Log.i("debugInfo","GETALLFRIENDAPPLIESLIST friend "+i+" is : "+friends.get(i).toString());
            }

            ResponseFromServer responseFromServer = new ResponseFromServer();
            if(null!=friends && friends.size()>0){
                responseFromServer.setStatusCode(Constant.SUCCESSFUL);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson(gson.toJson(friends));
            }else{
                responseFromServer.setStatusCode(Constant.FAILED);
                responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(Constant.FAILED));
                responseFromServer.setResponseJson("");
            }
            String response = gson.toJson(responseFromServer);
            Log.i("debugInfo","GETALLFRIENDAPPLIESLIST response is : "+response);
            return newFixedLengthResponse(response);
        } else if (responseType.equals("GETFRIENDLIST")) {
            //获取好友列表
            Friend friend = gson.fromJson(requestFromClient.getRequestJson(), Friend.class);
            Log.i("debugInfo","GETFRIENDLIST friend is : "+friend.toString());
            String myName = friend.getMyName();
            List<Friend> friends = sqLiteDBOperator.getAllFriend(myName);

            for(int i=0;i<friends.size();i++){
                Log.i("debugInfo","GETFRIENDLIST friend "+i+" is : "+friends.get(i).toString());
            }

            ResponseFromServer responseFromServer = new ResponseFromServer();
            if(null!=friends && friends.size()>0){
                responseFromServer.setStatusCode(Constant.SUCCESSFUL);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson(gson.toJson(friends));
            }else{
                responseFromServer.setStatusCode(Constant.FAILED);
                responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(Constant.FAILED));
                responseFromServer.setResponseJson("");
            }
            String response = gson.toJson(responseFromServer);
            Log.i("debugInfo","GETFRIENDLIST response is : "+response);
            return newFixedLengthResponse(response);
        }

        //Message部分
        else if (responseType.equals("SENDMESSAGE")) {
            //发送消息请求：
            ResponseFromServer responseFromServer = new ResponseFromServer();
            try {
                Message message = gson.fromJson(requestFromClient.getRequestJson(), Message.class);
                sqLiteDBOperator.sendMessage(message);
                responseFromServer.setStatusCode(Constant.SUCCESSFUL);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson("");
            } catch (Exception e) {
                e.printStackTrace();
                responseFromServer.setStatusCode(Constant.FAILED);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson("");
            }
            return newFixedLengthResponse(gson.toJson(responseFromServer));
        } else if (responseType.equals("QUERYNEWMESSAGE")) {
            //获取新消息
            ResponseFromServer responseFromServer = new ResponseFromServer();
            String request = requestFromClient.getRequestJson();
            String[] strArray = request.split(";");
            String flag = strArray[0];
            String lastMessageID = strArray[1];
            final List<Message> messages = sqLiteDBOperator.getNewMessage(flag, lastMessageID);
            if (null != messages) {
                responseFromServer.setStatusCode(Constant.SUCCESSFUL);
                responseFromServer.setErrorDescription("");
                responseFromServer.setResponseJson(gson.toJson(messages));
            } else {
                responseFromServer.setStatusCode(Constant.FAILED);
                responseFromServer.setErrorDescription(AppUtils.getErrorDescriptionFromStatusCode(Constant.FAILED));
                responseFromServer.setResponseJson("");
            }
            return newFixedLengthResponse(gson.toJson(responseFromServer));
        }
        return super.serve(session);
    }

}
