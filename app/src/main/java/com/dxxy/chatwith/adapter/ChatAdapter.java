package com.dxxy.chatwith.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dxxy.chatwith.bean.Message;
import com.dxxy.code.chatwith.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public List<Message> messages = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemClickListener (OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener (OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick (View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick (View view, int position);
    }

    public ChatAdapter (List<Message> messages) {
        this.messages = messages;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_message, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return null;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder (final ViewHolder viewHolder, final int position) {
        if(messages.get(position).getSource().equals("me")){
            //消息类型为发送类型
            viewHolder.rl_chat_message_send.setVisibility(View.VISIBLE);
            viewHolder.rl_chat_message_receive.setVisibility(View.GONE);
            viewHolder.text_send_time.setText(messages.get(position).getDate());
            viewHolder.text_message_send.setText(messages.get(position).getMessage());
        }else{
            //消息类型为接受类型
            viewHolder.rl_chat_message_send.setVisibility(View.GONE);
            viewHolder.rl_chat_message_receive.setVisibility(View.VISIBLE);
            viewHolder.text_friend.setText(messages.get(position).getSource());
            viewHolder.text_receive_time.setText(messages.get(position).getDate());
            viewHolder.text_message_receive.setText(messages.get(position).getMessage());
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount () {
        return messages.size();
    }

    public Message getItem (int index) {
        return messages.get(index);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rl_chat_message_send,rl_chat_message_receive;
        private TextView text_send_time,text_message_send,text_receive_time,text_friend,text_message_receive;

        public ViewHolder (View view) {
            super(view);
            rl_chat_message_send = view.findViewById(R.id.rl_chat_message_send);
            rl_chat_message_receive = view.findViewById(R.id.rl_chat_message_receive);

            text_send_time = view.findViewById(R.id.text_send_time);
            text_message_send = view.findViewById(R.id.text_message_send);
            text_receive_time = view.findViewById(R.id.text_receive_time);
            text_friend = view.findViewById(R.id.text_friend);
            text_message_receive = view.findViewById(R.id.text_message_receive);
        }

    }

}
