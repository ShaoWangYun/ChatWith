package com.dxxy.chatwith.sqlite.local;

import com.dxxy.chatwith.bean.Message;

import org.litepal.LitePal;

import java.util.List;

public class LitepalOpreator {

    //保存消息记录到本地数据库
    public void saveMessage(Message message_save){
        Message message = new Message();
        message.setId(message_save.getId());
        message.setSource(message_save.getSource());
        message.setDestination(message_save.getDestination());
        message.setDate(message_save.getDate());
        message.save();
    }

    //从本地数据库获取所有的消息记录
    public List<Message> getMessageRecordFromLocal(String flag){
        List<Message> messages = LitePal.where("flag = ?", flag).find(Message.class);
        return messages;
    }

}
