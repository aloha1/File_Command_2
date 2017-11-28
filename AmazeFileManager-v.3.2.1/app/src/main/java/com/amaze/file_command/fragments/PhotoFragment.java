package com.amaze.file_command.fragments;

import android.app.Activity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amaze.file_command.R;
import com.amaze.file_command.adapters.PhotoAdapter;
import com.amaze.file_command.utils.ClassBean;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Yunwen on 5/22/2017.
 */

public class PhotoFragment extends Fragment {
    String TAG = "PhotoFragment";
    RecyclerView mRecyclerView;
    List<ClassBean> listClass = new ArrayList<>();
    private Context mContext;
    List<String> listImage;

    public PhotoFragment(){
        mContext = getActivity();
    }

    public void dataFlow(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView(view);
    }

    private void initData(){

    }

    private void initView(View view){
        initRecycler(view);
    }

    private void initRecycler(View view){
        RecyclerView recyclerView =  view.findViewById(R.id.recyclerview_photo);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);

        listImage = new ArrayList<>();
        listImage = getAllShownImagesPath(getActivity());
        PhotoAdapter adapter = new PhotoAdapter(getActivity(), listImage);
        recyclerView.setAdapter(adapter);
    }
    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }
    @Override
    public void onStop() {
        super.onStop();
    }
}