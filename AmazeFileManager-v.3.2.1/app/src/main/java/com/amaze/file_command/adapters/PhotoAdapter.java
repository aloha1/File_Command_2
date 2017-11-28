package com.amaze.file_command.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.amaze.file_command.R;
import com.amaze.file_command.activities.MainActivity;

import java.io.File;
import java.util.List;


import static android.content.Context.MODE_PRIVATE;
import static com.amaze.file_command.activities.helper.Helper.fileExt;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public enum ITEM_TYPE {
        ITEM_TYPE_IMAGE,
        ITEM_TYPE_TEXT
    }
    private SharedPreferences sharedPrefs;
    private final static String TAG = "AppStoreAdapter";
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<String> dataList;

    public PhotoAdapter(Context context, List<String> dataList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        sharedPrefs = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TextViewHolder(mLayoutInflater.inflate(R.layout.card_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TextViewHolder) {
            setBitmap(dataList.get(position),((TextViewHolder)holder).imageView);
            final File imgFile = new  File(dataList.get(position));
            ((TextViewHolder)holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mContext).clickFile(imgFile);
                }
            });
        }
    }

    private void setBitmap(String imageUri, ImageView imageView){
        File imgFile = new  File(imageUri);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }

    @Override
    public int getItemCount() {
        return (dataList.isEmpty())?0:dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? ITEM_TYPE.ITEM_TYPE_IMAGE.ordinal() : ITEM_TYPE.ITEM_TYPE_TEXT.ordinal();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        CardView mCardView;
        ImageView imageView;

        TextViewHolder(View view) {
            super(view);
            mCardView =  view.findViewById(R.id.card_photo);
            imageView =  view.findViewById(R.id.image_photo);
        }
    }

    public void clickFile(File file){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mimeType =  MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt(file.getAbsolutePath()));
            Uri apkURI = FileProvider.getUriForFile(
                    mContext,
                    mContext
                            .getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {

            e.printStackTrace();
        }
    }
}