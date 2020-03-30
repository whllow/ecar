package com.example.excar;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public abstract class Myadapter<T> extends BaseAdapter {
    private int layoutid;
    private ArrayList<T> mData;

    public Myadapter(){}

    public Myadapter(ArrayList<T> mData,int layoutid){
        this.mData = mData;
        this.layoutid = layoutid;

    }

    @Override
    public int getCount() {
        return mData!=null?mData.size():0;
    }

    @Override
    public T getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        HolderView holder = HolderView.bind(viewGroup.getContext(),viewGroup,layoutid,view,i);
        BInd(holder,getItem(i));
        return holder.getItemView();
    }

   public abstract void  BInd(HolderView holder,T obj);

    public static class HolderView{

        private SparseArray<View> mView;
        private Context mContext;
        private int position;
        private View item;

        public HolderView(){}

        public HolderView(Context mContext,int layoutid,ViewGroup viewGroup){
            mView = new SparseArray<>();
            View view = LayoutInflater.from(mContext).inflate(layoutid,viewGroup,false);
            this.mContext = mContext;
            item = view;
            item.setTag(this);
        }

        public static HolderView bind(Context mContext,ViewGroup viewGroup,int layoutid,
                                      View view,int position){
            HolderView holder;
            if(view==null){
                holder = new HolderView(mContext,layoutid,viewGroup);
            }else{
                holder  = (HolderView) view.getTag();
                holder.item = view;
            }
            holder.position = position;
            return holder;
        }

        public <T extends View> T getId(int id){
            T t = (T)mView.get(id);
            if(t==null){
                t = item.findViewById(id);
                mView.put(id,t);
            }
            return  t;
        }

        public int getPosition() {
            return position;
        }
        public View getItemView(){
            return  item;
        }

        public HolderView setImage(int id,int image){
            View view = getId(id);
            if(view instanceof ImageView){
                ((ImageView) view).setImageResource(image);
            }
            else
                view.setBackgroundResource(image);
            return this;
        }

    }


}
