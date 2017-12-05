package com.bgsltd.file_command.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bgsltd.file_command.R;
import com.bgsltd.file_command.database.Category;
import com.bgsltd.file_command.database.CategoryRepo;
import com.bgsltd.file_command.ui.views.appbar.AppBar;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yunwen on 10/30/2017.
 */

public class AddCategoryActivity extends BasicActivity implements View.OnClickListener{

    private MainActivity mainActivity;
    private String TAG = "AddCategoryActivity";
    public SharedPreferences sharedPref;
    private Toolbar toolbar;
    private TextView toolbarTitle, textTitle, textContent;
    private ImageView imagePicture, imageMusic, imageVideos, imageDocuments, imageArchives, imageDownloads, imageSecuredFiles;
    private ImageView imageRecentFiles, imageConvertFiles, imageRecycleBin, imageFavorites,imagePcFileTransfer,imageScreenSharing;
    private CardView cardView;
    private RecyclerView mRecyclerView;
    private int _algorithm_id = 0;
    private AppBar appbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcategory);
        initView();
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.accent_indigo)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarTitle = (TextView) findViewById(R.id.text_toolbar_title);
        toolbarTitle.setText(R.string.title_add_category);
        toolbarTitle.setTextColor(getResources().getColor(R.color.text_disabled));
        setImageView();
        listAll();
    }

    private void setImageView(){
        imagePicture = (ImageView) findViewById(R.id.image_tick_picture);
        imageMusic = (ImageView) findViewById(R.id.image_tick_music);
        imageVideos = (ImageView) findViewById(R.id.image_tick_videos);
        imageDocuments = (ImageView) findViewById(R.id.image_tick_documents);
        imageArchives = (ImageView) findViewById(R.id.image_tick_archives);
        imageDownloads = (ImageView) findViewById(R.id.image_tick_downloads);
        imageSecuredFiles = (ImageView) findViewById(R.id.image_tick_secured_files);
        imageRecentFiles = (ImageView) findViewById(R.id.image_tick_recent_files);
        imageConvertFiles = (ImageView) findViewById(R.id.image_tick_convert_files);
        //imageRecycleBin = (ImageView) findViewById(R.id.image_tick_recycle_bin);
        imageFavorites = (ImageView) findViewById(R.id.image_tick_favorites);
        imagePcFileTransfer = (ImageView) findViewById(R.id.image_tick_pc_files_transfer);
        //imageScreenSharing = (ImageView) findViewById(R.id.image_tick_screen_sharing);
        imagePicture.setOnClickListener(this);imageMusic.setOnClickListener(this); imageVideos.setOnClickListener(this);
        imageDocuments.setOnClickListener(this); imageArchives.setOnClickListener(this); imageDownloads.setOnClickListener(this);
        imageSecuredFiles.setOnClickListener(this);imageRecentFiles.setOnClickListener(this); imageConvertFiles.setOnClickListener(this);
//        imageRecycleBin.setOnClickListener(this);
        imageFavorites.setOnClickListener(this);imagePcFileTransfer.setOnClickListener(this);
        //imageScreenSharing.setOnClickListener(this);
    }

    public void onClick(View v) {
        changeImageLike(v.getId());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void listAll() {
        int _algorithm_id = 0;
        CategoryRepo repo = new CategoryRepo(this);
        Category category = new Category();
        Log.d(TAG, "The id is: " + _algorithm_id);
        category = repo.getColumnById(_algorithm_id);
        ArrayList<HashMap<String, String>> algorithmList = repo.getAlgorithmList();
        if (algorithmList.size() != 0) {//Show Db list
            initRecyclerView(algorithmList);
        } else {
            Log.d(TAG, "No content");
        }
    }

    private void checkAllTick(ArrayList<HashMap<String, String>> algorithmList){
        for(int i = 0; i < algorithmList.size(); i++){
            //checkTick(algorithmList.get(i));

            //
        }
    }
    private void initRecyclerView(ArrayList<HashMap<String, String>> algorithmList){
        for(int i = 0; i< algorithmList.size();i++){
            try{
            Log.d(TAG,"The "+i+"th is: "+algorithmList.get(i).get("topic"));
            //transfer to resId
            int resId = getResources().getIdentifier(algorithmList.get(i).get("topic"), "id", getPackageName());
            Log.d(TAG,"The "+i+"th ID is: "+resId);
            ImageView imageTarget = (ImageView) findViewById(resId);
            imageTarget.setImageResource(R.drawable.tick);}catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void checkTick(ImageView image, TextView textView){
        String data = "";
        CategoryRepo repo = new CategoryRepo(this);
        Category category = repo.getColumnByTopic(data);
        try{
            if(category.topic.equals(data)){
                image.setImageResource(R.drawable.tick);
            }else{
                image.setImageResource(R.drawable.square);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void changeImageLike(Integer resId){
        String ResourceIdAsString = getResources().getResourceName(resId);
        Log.d(TAG, "ID name: "+ResourceIdAsString);
        // get picture
        String pack = getPackageName();
        String data = ResourceIdAsString.replace(pack+":id/","");
        Log.d(TAG, "ID data name: "+data);
        //transfer id from image to text
        ImageView imageTarget =  (ImageView) findViewById(resId);
        assert(resId == imageTarget.getId());
        Drawable drawableId =  imageTarget.getDrawable();
        Integer integer = (Integer) imageTarget.getTag();
        integer = integer == null ? 0 : integer;
        switch(integer) {
            case R.drawable.tick:
                imageTarget.setImageResource(R.drawable.square);
                imageTarget.setTag(R.drawable.square);
                deleteFavorite(data);//topic is from the imageTarget, --> textTarget --> topic
                break;
            case R.drawable.square:
            default:
                imageTarget.setImageResource(R.drawable.tick);
                imageTarget.setTag(R.drawable.tick);
                addToFavorite(data);
                break;
        }
    }


    private void deleteFavorite(String topic) {
        CategoryRepo repo = new CategoryRepo(this);
        Category dbFavorite = repo.getColumnByTopic(topic);
        Log.d(TAG, "delete:"+topic);
        repo.delete(dbFavorite.dbId);
    }

    private void addToFavorite(String topic) {
        CategoryRepo repo = new CategoryRepo(this);
        Category category = repo.getColumnByTopic(topic);
        try {
            if (category.topic.equals(topic)) {
                repo.update(category);
            }
        } catch (Exception e) {
            category.time = 25;
            category.content = "";
            category.topic = topic;
            category.dbId = _algorithm_id;
            _algorithm_id = repo.insert(category);
            Log.d(TAG, "add:"+category.topic);
            e.printStackTrace();
        }
    }
}
