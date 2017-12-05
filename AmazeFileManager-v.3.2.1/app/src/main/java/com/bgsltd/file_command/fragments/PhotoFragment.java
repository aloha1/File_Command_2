package com.bgsltd.file_command.fragments;

import android.app.Activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
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

import com.bgsltd.file_command.R;
import com.bgsltd.file_command.activities.MainActivity;
import com.bgsltd.file_command.adapters.PhotoAdapter;
import com.bgsltd.file_command.utils.ClassBean;
import com.bgsltd.file_command.utils.color.ColorUsage;

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
    private int skin_color, skinTwoColor, accentColor;
    private MainActivity mainActivity;
    public PhotoFragment(){
        mContext = getActivity();
    }

    public void dataFlow(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        skin_color = mainActivity.getColorPreference().getColor(ColorUsage.PRIMARY);
        skinTwoColor = mainActivity.getColorPreference().getColor(ColorUsage.PRIMARY_TWO);
        accentColor = mainActivity.getColorPreference().getColor(ColorUsage.ACCENT);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mainActivity = (MainActivity) getActivity();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mainActivity.getAppbar().setTitle(getResources().getString(R.string.text_tick_picture));
        mainActivity.floatingActionButton.hideMenuButton(true);
        mainActivity.buttonBarFrame.setVisibility(View.GONE);
        mainActivity.supportInvalidateOptionsMenu();

        mainActivity.updateViews(new ColorDrawable(MainActivity.currentTab==1 ?
                skinTwoColor : skin_color));
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}