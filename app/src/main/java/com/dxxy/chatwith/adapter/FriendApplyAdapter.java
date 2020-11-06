package com.dxxy.chatwith.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dxxy.chatwith.bean.Friend;
import com.dxxy.code.chatwith.R;

import java.util.ArrayList;
import java.util.List;

public class FriendApplyAdapter extends RecyclerView.Adapter<FriendApplyAdapter.ViewHolder> {

    public List<Friend> friends = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemClickListener (OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener (OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public FriendApplyAdapter(List<Friend> friends) {
        this.friends = friends;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friend, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder (final ViewHolder viewHolder, final int position) {

        viewHolder.text_friend_name.setText(friends.get(position).getMyName());

        if (onItemClickListener != null) {
            viewHolder.rl_friend_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    onItemClickListener.onItemClick(viewHolder.itemView, position);
                }
            });
            viewHolder.rl_friend_info.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick (View v) {
                    onItemLongClickListener.onItemLongClick(viewHolder.itemView, position);
                    return false;
                }
            });
        }

    }

    //获取数据的数量
    @Override
    public int getItemCount () {
        return friends.size();
    }

    public Friend getItem (int index) {
        return friends.get(index);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_friend_name;
        private RelativeLayout rl_friend_info;
        public ViewHolder (View view) {
            super(view);
            rl_friend_info = view.findViewById(R.id.rl_friend_info);
            text_friend_name = view.findViewById(R.id.text_friend_name);
        }
    }

}
