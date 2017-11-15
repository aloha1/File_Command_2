package com.amaze.file_command.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amaze.file_command.R;
import com.amaze.file_command.adapters.HomeAdapter;
import com.amaze.file_command.adapters.helper.SimpleItemTouchHelperCallback;
import com.amaze.file_command.database.Category;
import com.amaze.file_command.database.CategoryRepo;
import com.amaze.file_command.utils.ClassBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Yunwen on 5/22/2017.
 */

public class HomeFragment extends Fragment {
    String TAG = "HomeFragment";
    RecyclerView mRecyclerView;
    private HomeAdapter homeAdapter;
    List<ClassBean> listClass = new ArrayList<>();
    private Context mContext;
    private SearchView searchView;
    private ImageView imageEditor, imageAnalyzer;
    public HomeFragment(){
        mContext = getActivity();
    }
    private ItemTouchHelper mItemTouchHelper;

    public void dataFlow(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData(view);
        initView(view);
    }

    private void initData(View view){
        listAll(view);
    }

    private void initView(View view){
        imageAnalyzer = view.findViewById(R.id.image_home_analyzer);
        imageEditor = view.findViewById(R.id.image_home_editor);
        imageEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
//                getActivity().startActivity(intent);
            }
        });
        CardView cardView = (CardView) view.findViewById(R.id.card_normal);
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), InterAd_Activity.class);
//                getActivity().startActivity(intent);
//            }
//        });

        //final TextView occupiedSpaceText = (TextView) view.findViewById(R.id.occupiedSpace);
        //final TextView freeSpaceText = (TextView) view.findViewById(R.id.freeSpace);
        final ProgressBar progressIndicator = (ProgressBar) view.findViewById(R.id.indicator);
        final float totalSpace = DeviceMemory.getInternalStorageSpace();
        final float occupiedSpace = DeviceMemory.getInternalUsedSpace();
        final float freeSpace = DeviceMemory.getInternalFreeSpace();
        final DecimalFormat outputFormat = new DecimalFormat("#.##");
//        if (null != occupiedSpaceText) {
//            occupiedSpaceText.setText(outputFormat.format(occupiedSpace) + " MB");
//        }
//
//        if (null != freeSpaceText) {
//            freeSpaceText.setText(outputFormat.format(freeSpace) + " MB");
//        }

        if (null != progressIndicator) {
            progressIndicator.setMax((int) totalSpace);
            progressIndicator.setProgress((int)occupiedSpace);
        }
    }

    private void initRecycler(View view, List<ClassBean> listTemp){
        mRecyclerView = view.findViewById(R.id.recyclerview_home);
        homeAdapter = new HomeAdapter(getActivity(),listTemp);

        mRecyclerView.setAdapter(homeAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(homeAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    public void listAll(View view) {
        int _algorithm_id = 0;
        CategoryRepo repo = new CategoryRepo(getActivity());
        Category category = new Category();
        Log.d(TAG, "The id is: " + _algorithm_id);
        category = repo.getColumnById(_algorithm_id);
        ArrayList<HashMap<String, String>> algorithmList = repo.getAlgorithmList();
        if (algorithmList.size() != 0) {//Show Db list
            initRecyclerView(algorithmList, view);
        } else {
            Toast.makeText(getActivity(), "No Content!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initRecyclerView(ArrayList<HashMap<String, String>> algorithmList, View view){
        List<ClassBean> listTemp = new ArrayList<>();
        for(int i = 0; i< algorithmList.size();i++){
            String data = algorithmList.get(i).get("topic");
            Log.d(TAG,"The "+i+"th is: "+data);
            //transfer to resId
            int resId = getResources().getIdentifier(data, "id", getActivity().getPackageName());
            Log.d(TAG,"The "+i+"th ID is: "+resId);
            //transfer image name into image id and text,
            ClassBean classBean = new ClassBean();
            classBean.setTagString(data.replace("image_tick_",""));
            classBean.setDescription(data);
            classBean.setCoverImageUri(data);
            listTemp.add(classBean);
            initRecycler(view, listTemp);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onResume() {
        listAll(getView());
        super.onResume();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * From question http://stackoverflow.com/questions/2652935/android-internal-phone-storage by Lazy Ninja
     */
    public static class DeviceMemory {

        public static float getInternalStorageSpace() {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            //StatFs statFs = new StatFs("/data");
            float total = ((float)statFs.getBlockCount() * statFs.getBlockSize()) / 1048576;
            return total;
        }

        public static float getInternalFreeSpace() {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            //StatFs statFs = new StatFs("/data");
            float free  = ((float)statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
            return free;
        }

        public static float getInternalUsedSpace() {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            //StatFs statFs = new StatFs("/data");
            float total = ((float)statFs.getBlockCount() * statFs.getBlockSize()) / 1048576;
            float free  = ((float)statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
            float busy  = total - free;
            return busy;
        }
    }
}