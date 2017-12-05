package com.amaze.file_command.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bgsltd.file_command.R;
import com.amaze.file_command.activities.MainActivity;
import com.amaze.file_command.adapters.DocumentAdapter;
import com.amaze.file_command.utils.ClassBean;
import com.amaze.file_command.utils.color.ColorUsage;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Yunwen on 5/22/2017.
 */

public class MusicFragment extends android.support.v4.app.Fragment {
    String TAG = "PhotoFragment";
    private int skin_color, skinTwoColor, accentColor;
    private MainActivity mainActivity;
    List<ClassBean> listClass = new ArrayList<>();
    private Context mContext;
    List<String> listImage;

    public MusicFragment(){
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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        listImage = new ArrayList<>();
        Log.d(TAG,getsonglist().get(0));
        listImage = getsonglist();
        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.music);
        DocumentAdapter adapter = new DocumentAdapter(getActivity(), listImage, icon);
        recyclerView.setAdapter(adapter);
    }

    List<String> getsonglist() {

        List<String> songlist = new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device
        } else {
            do {

                String fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                // ...process entry...
                Log.e("Full Path : ", fullPath);

                songlist.add(fullPath);
            } while (cursor.moveToNext());
        }
        return songlist;
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
        mainActivity.getAppbar().setTitle(getResources().getString(R.string.text_tick_music));
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