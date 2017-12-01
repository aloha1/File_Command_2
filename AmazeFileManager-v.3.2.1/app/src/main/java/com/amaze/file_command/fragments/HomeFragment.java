package com.amaze.file_command.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.file_command.R;
import com.amaze.file_command.activities.AddCategoryActivity;
import com.amaze.file_command.activities.InterAd_Activity;
import com.amaze.file_command.activities.MainActivity;
import com.amaze.file_command.adapters.HomeAdapter;
import com.amaze.file_command.adapters.helper.SimpleItemTouchHelperCallback;
import com.amaze.file_command.database.Category;
import com.amaze.file_command.database.CategoryRepo;
import com.amaze.file_command.exceptions.CryptException;
import com.amaze.file_command.services.ftpservice.FTPService;
import com.amaze.file_command.utils.ClassBean;
import com.amaze.file_command.utils.Utils;
import com.amaze.file_command.utils.color.ColorUsage;
import com.amaze.file_command.utils.files.CryptUtil;
import com.amaze.file_command.utils.theme.AppTheme;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yashwanthreddyg on 10-06-2016.
 * Edited by Luca D'Amico (Luca91) on 25 Jul 2017 (Fixed FTP Server while using Eth connection)
 */
public class HomeFragment extends Fragment {
    String TAG = "HomeFragment";

    private TextView statusText, username, password, port, sharedPath;
    private AppCompatEditText usernameEditText, passwordEditText;
    private TextInputLayout usernameTextInput, passwordTextInput;
    private AppCompatCheckBox mAnonymousCheckBox, mSecureCheckBox;
    private Button ftpBtn;
    private MainActivity mainActivity;
    private View rootView, startDividerView, statusDividerView;
    private int skin_color, skinTwoColor, accentColor;
    private Spanned spannedStatusNoConnection, spannedStatusConnected;
    private Spanned spannedStatusSecure, spannedStatusNotRunning;
    private ImageButton ftpPasswordVisibleButton;


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
    private int _algorithm_id = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        skin_color = mainActivity.getColorPreference().getColor(ColorUsage.PRIMARY);
        skinTwoColor = mainActivity.getColorPreference().getColor(ColorUsage.PRIMARY_TWO);
        accentColor = mainActivity.getColorPreference().getColor(ColorUsage.ACCENT);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData(view);
        initView(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mainActivity.getAppbar().setTitle(getResources().getString(R.string.home));
        mainActivity.getAppbar().getToolbar().dismissPopupMenus();
        mainActivity.floatingActionButton.hideMenuButton(true);
        mainActivity.buttonBarFrame.setVisibility(View.GONE);
        mainActivity.supportInvalidateOptionsMenu();

        mainActivity.updateViews(new ColorDrawable(MainActivity.currentTab==1 ?
                skinTwoColor : skin_color));
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    private void initData(View view){
        addToFavorite("image_tick_favorites");
        addToFavorite("image_tick_documents");
        addToFavorite("image_tick_videos");
        addToFavorite("image_tick_archives");
        addToFavorite("image_tick_downloads");
        addToFavorite("image_tick_recent_files");
        addToFavorite("image_tick_convert_files");
        listAll(view);
    }

    private void initView(View view){
        imageAnalyzer = view.findViewById(R.id.image_home_analyzer);
        imageEditor = view.findViewById(R.id.image_home_editor);
        imageEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
                getActivity().startActivity(intent);
            }
        });
        CardView cardView = view.findViewById(R.id.card_normal);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InterAd_Activity.class);
                getActivity().startActivity(intent);
            }
        });
        CardView cardViewProgress = view.findViewById(R.id.card_home_progress);
        cardViewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.goToMain("");
            }
        });
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
            Log.d(TAG,"No Content!");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mainActivity.getMenuInflater().inflate(R.menu.empty, menu);
    }

    private void addToFavorite(String topic) {
        CategoryRepo repo = new CategoryRepo(getActivity());
        Category category = repo.getColumnByTopic(topic);
        try {
            if (category.topic.equals(topic)) {
                repo.update(category);
                Toast.makeText(getActivity(), R.string.no_content, Toast.LENGTH_SHORT).show();
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
