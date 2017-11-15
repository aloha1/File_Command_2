package com.amaze.file_command.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amaze.file_command.R;
import com.amaze.file_command.adapters.helper.ItemTouchHelperAdapter;
import com.amaze.file_command.utils.ClassBean;

import java.util.Collections;
import java.util.List;


import static android.content.Context.MODE_PRIVATE;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    public enum ITEM_TYPE {
        ITEM_TYPE_IMAGE,
        ITEM_TYPE_TEXT
    }
    private SharedPreferences sharedPrefs;
    private final static String TAG = "HomeAdapter";
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<ClassBean> dataList;
   // private final OnStartDragListener mDragStartListener;

    public HomeAdapter(Context context, List<ClassBean> dataList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;

        sharedPrefs = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TextViewHolder(mLayoutInflater.inflate(R.layout.card_content, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TextViewHolder) {
            String stringRes = dataList.get(position).getDescription().replace("image","text");
            Log.d(TAG,"Text is: " + stringRes);
            int resId = mContext.getResources().getIdentifier(stringRes, "string", mContext.getPackageName());
            try {
                final String res = mContext.getResources().getString(resId);
                ((TextViewHolder)holder).mTextView.setText(res);
                ((TextViewHolder)holder).mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoPage(res);
                    }
                });
                int drawableId = mContext.getResources().getIdentifier(dataList.get(position).getTagString(), "drawable", mContext.getPackageName());
                Log.d(TAG,"Drawable is: "+drawableId);
                Drawable drawable = mContext.getResources().getDrawable(drawableId);
                ((TextViewHolder) holder).imageView.setImageDrawable(drawable);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void gotoPage(String res){
        switch (res){
            case "Favorites":
                break;
            case "Documents":
                break;
            case "Picture":
                break;
            case "Music":
                break;
            case "Archives":
                break;
            case "Downloads":
                break;
            case "Convert Files":
                break;
            case "Recycler Bin":
                break;
            case "Videos":
                break;
            case "Recent Files":
                break;
            default:
                break;

        }

    }
    @Override
    public int getItemCount() {
        return (dataList.size() >= 1)?dataList.size():1;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? ITEM_TYPE.ITEM_TYPE_IMAGE.ordinal() : ITEM_TYPE.ITEM_TYPE_TEXT.ordinal();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        CardView mCardView;
        ImageView imageView;

        public TextViewHolder(View view) {
            super(view);
            mCardView =  view.findViewById(R.id.card_normal);
            imageView =  view.findViewById(R.id.image_content);
            mTextView = view.findViewById(R.id.text_content);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(dataList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


}