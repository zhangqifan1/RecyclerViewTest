package com.example.anadministrator.mytest2.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.anadministrator.mytest2.JavaBean.Bean;
import com.example.anadministrator.mytest2.R;
import com.example.anadministrator.mytest2.Utils.ImageLoaderUtils;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> implements View.OnClickListener {
    private Bean bean;
    private Context mContext;
    private final LayoutInflater inflater;
    private View view;

    public MyRecyclerAdapter(Bean bean, Context mContext) {
        this.bean = bean;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageLoaderUtils.setImageView(bean.美女.get(position).img,mContext,holder.iv);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return bean.美女.size();
    }

    @Override
    public void onClick(View view) {
        if(mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
        }
    }

    public  interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}