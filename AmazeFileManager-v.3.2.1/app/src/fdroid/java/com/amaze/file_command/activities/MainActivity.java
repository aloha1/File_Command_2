/*
 * Copyright (C) 2014 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 *                      Emmanuel Messulam<emmanuelbendavid@gmail.com>
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amaze.file_command.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.hardware.usb.UsbManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.quicksettings.TileService;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.file_command.R;
import com.amaze.file_command.adapters.DrawerAdapter;
import com.amaze.file_command.database.CloudContract;
import com.amaze.file_command.database.CloudHandler;
import com.amaze.file_command.database.CryptHandler;
import com.amaze.file_command.database.TabHandler;
import com.amaze.file_command.database.UtilsHandler;
import com.amaze.file_command.database.models.CloudEntry;
import com.amaze.file_command.database.models.Tab;
import com.amaze.file_command.exceptions.CloudPluginException;
import com.amaze.file_command.filesystem.BaseFile;
import com.amaze.file_command.filesystem.FileUtil;
import com.amaze.file_command.filesystem.HFile;
import com.amaze.file_command.filesystem.RootHelper;
import com.amaze.file_command.fragments.AppsList;
import com.amaze.file_command.fragments.CloudSheetFragment;
import com.amaze.file_command.fragments.CloudSheetFragment.CloudConnectionCallbacks;
import com.amaze.file_command.fragments.FTPServerFragment;
import com.amaze.file_command.fragments.HomeFragment;
import com.amaze.file_command.fragments.MainFragment;
import com.amaze.file_command.fragments.ProcessViewer;
import com.amaze.file_command.fragments.SearchWorkerFragment;
import com.amaze.file_command.fragments.TabFragment;
import com.amaze.file_command.fragments.ZipViewer;
import com.amaze.file_command.fragments.preference_fragments.QuickAccessPref;
import com.amaze.file_command.services.CopyService;
import com.amaze.file_command.services.DeleteTask;
import com.amaze.file_command.services.asynctasks.CopyFileCheck;
import com.amaze.file_command.services.asynctasks.MoveFiles;
import com.amaze.file_command.ui.dialogs.GeneralDialogCreation;
import com.amaze.file_command.ui.dialogs.RenameBookmark;
import com.amaze.file_command.ui.dialogs.RenameBookmark.BookmarkCallback;
import com.amaze.file_command.ui.dialogs.SmbConnectDialog;
import com.amaze.file_command.ui.dialogs.SmbConnectDialog.SmbConnectionListener;
import com.amaze.file_command.ui.drawer.EntryItem;
import com.amaze.file_command.ui.drawer.FragmentItem;
import com.amaze.file_command.ui.drawer.Item;
import com.amaze.file_command.ui.drawer.SectionItem;
import com.amaze.file_command.ui.views.RoundedImageView;
import com.amaze.file_command.ui.views.ScrimInsetsRelativeLayout;
import com.amaze.file_command.ui.views.appbar.AppBar;
import com.amaze.file_command.ui.views.appbar.SearchView;
import com.amaze.file_command.utils.AppConfig;
import com.amaze.file_command.utils.BookSorter;
import com.amaze.file_command.utils.DataUtils;
import com.amaze.file_command.utils.DataUtils.DataChangeListener;
import com.amaze.file_command.utils.MainActivityHelper;
import com.amaze.file_command.utils.OTGUtil;
import com.amaze.file_command.utils.OpenMode;
import com.amaze.file_command.utils.PreferenceUtils;
import com.amaze.file_command.utils.ServiceWatcherUtil;
import com.amaze.file_command.utils.TinyDB;
import com.amaze.file_command.utils.Utils;
import com.amaze.file_command.utils.color.ColorUsage;
import com.amaze.file_command.utils.files.Futils;
import com.amaze.file_command.utils.theme.AppTheme;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cloudrail.si.CloudRail;
import com.cloudrail.si.exceptions.AuthenticationException;
import com.cloudrail.si.exceptions.ParseException;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Box;
import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.services.GoogleDrive;
import com.cloudrail.si.services.OneDrive;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import eu.chainfire.libsuperuser.Shell;

import static android.os.Build.VERSION.SDK_INT;
import static com.amaze.file_command.fragments.preference_fragments.Preffrag.PREFERENCE_SHOW_SIDEBAR_FOLDERS;
import static com.amaze.file_command.fragments.preference_fragments.Preffrag.PREFERENCE_SHOW_SIDEBAR_QUICKACCESSES;

public class MainActivity extends ThemedActivity implements OnRequestPermissionsResultCallback,
        SmbConnectionListener, DataChangeListener, BookmarkCallback,
        SearchWorkerFragment.HelperCallbacks, CloudConnectionCallbacks,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final Pattern DIR_SEPARATOR = Pattern.compile("/");
    public static final String TAG_ASYNC_HELPER = "async_helper";

    /* Request code used to invoke sign in user interactions. */
    static final int RC_SIGN_IN = 0;

    private DataUtils dataUtils = DataUtils.getInstance();

    public DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    public ScrimInsetsRelativeLayout mDrawerLinear;
    public String path = "", launchPath;
    public ArrayList<BaseFile> COPY_PATH = null, MOVE_PATH = null;
    public FrameLayout frameLayout;
    public boolean mReturnIntent = false;
    public boolean useGridView, openzip = false;
    public boolean mRingtonePickerIntent = false, colourednavigation = false;
    public int skinStatusBar;

    public volatile int storage_count = 0; // number of storage available (internal/external/otg etc)

    public FloatingActionMenu floatingActionButton;
    public LinearLayout pathbar;
    public FrameLayout buttonBarFrame;
    public boolean isDrawerLocked = false;

    public DrawerAdapter adapter;
    public MainActivityHelper mainActivityHelper;

    public int operation = -1;
    public ArrayList<BaseFile> oparrayList;
    public ArrayList<ArrayList<BaseFile>> oparrayListList;
    private AdView mAdView;
    // oppathe - the path at which certain operation needs to be performed
    // oppathe1 - the new path which user wants to create/modify
    // oppathList - the paths at which certain operation needs to be performed (pairs with oparrayList)
    public String oppathe, oppathe1;
    public ArrayList<String> oppatheList;
    public RelativeLayout drawerHeaderParent;

    /**
     * @deprecated use getCurrentMainFragment()
     */
    public MainFragment mainFragment;

    public static final String KEY_PREF_OTG = "uri_usb_otg";

    private static final int image_selector_request_code = 31;

    private AppBar appbar;
    //private HistoryManager history, grid;
    private Futils utils;
    private MainActivity mainActivity = this;
    private Context con = this;
    private String zippath;
    private FragmentTransaction pending_fragmentTransaction;
    private String pendingPath;
    private boolean openProcesses = false;
    private int hidemode;
    private MaterialDialog materialDialog;
    private String newPath = null;
    private boolean backPressedToExitOnce = false;
    private Toast toast = null;
    private ActionBarDrawerToggle mDrawerToggle;
    private Intent intent;
    private View drawerHeaderLayout;
    private View drawerHeaderView, indicator_layout;
    private RoundedImageView drawerProfilePic;
    private ImageLoader mImageLoader;

    private TextView mGoogleName, mGoogleId;
    private TabHandler tabHandler;
    // Check for user interaction for Google+ api only once
    private boolean mGoogleApiKey = false;
    /* A flag indicating that a PendingIntent is in progress and prevents
   * us from starting further intents.
   */
    private boolean mIntentInProgress, showHidden = false;
    private AsyncTask<Void, Void, Boolean> cloudSyncTask;

    private AppBarLayout appBarLayout;

    //TODO make var names meaningful
    private static final int SELECT_MINUS_2 = -2, NO_VALUE = -1, SELECT_0 = 0, SELECT_102 = 102;
    private int selectedStorage;

    private CoordinatorLayout mScreenLayout;
    private View fabBgView;
    private UtilsHandler utilsHandler;
    private CloudHandler cloudHandler;

    private static final int REQUEST_CODE_SAF = 223;
    private static final String VALUE_PREF_OTG_NULL = "n/a";

    public static final String KEY_INTENT_PROCESS_VIEWER = "openprocesses";
    public static final String TAG_INTENT_FILTER_FAILED_OPS = "failedOps";
    public static final String TAG_INTENT_FILTER_GENERAL = "general_communications";
    public static final String ARGS_KEY_LOADER = "loader_cloud_args_service";

    private static final String CLOUD_AUTHENTICATOR_GDRIVE = "android.intent.category.BROWSABLE";
    private static final String CLOUD_AUTHENTICATOR_REDIRECT_URI = "com.amaze.filemanager:/oauth2redirect";

    // the current visible tab, either 0 or 1
    public static int currentTab;

    public static Shell.Interactive shellInteractive;
    public static Handler handler;

    private static HandlerThread handlerThread;

    private static final int REQUEST_CODE_CLOUD_LIST_KEYS = 5463;
    private static final int REQUEST_CODE_CLOUD_LIST_KEY = 5472;

    private static final String KEY_PREFERENCE_BOOKMARKS_ADDED = "books_added";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialisePreferences();
        initializeInteractiveShell();

        dataUtils.registerOnDataChangedListener(this);

        setContentView(R.layout.main_toolbar);
        appbar = new AppBar(this, sharedPref, new SearchView.SearchListener() {
            @Override
            public void onSearch(String queue) {
                if(!queue.isEmpty()) {
                    mainActivityHelper.search(queue);
                }
            }
        });
        //init framelayout?
        initialiseViews();

        initAds();
        tabHandler = new TabHandler(this);
        utilsHandler = new UtilsHandler(this);
        cloudHandler = new CloudHandler(this);

        mImageLoader = AppConfig.getInstance().getImageLoader();
        utils = getFutils();
        mainActivityHelper = new MainActivityHelper(this);
        initialiseFab();

        if (CloudSheetFragment.isCloudProviderAvailable(this)) {

            getSupportLoaderManager().initLoader(REQUEST_CODE_CLOUD_LIST_KEYS, null, this);
        }

        path = getIntent().getStringExtra("path");
        openProcesses = getIntent().getBooleanExtra(KEY_INTENT_PROCESS_VIEWER, false);
        intent = getIntent();

        String actionIntent = intent.getAction();
        String typeIntent = intent.getType();
        if (intent.getStringArrayListExtra(TAG_INTENT_FILTER_FAILED_OPS) != null) {
            ArrayList<BaseFile> failedOps = intent.getParcelableArrayListExtra(TAG_INTENT_FILTER_FAILED_OPS);
            if (failedOps != null) {
                mainActivityHelper.showFailedOperationDialog(failedOps, intent.getBooleanExtra("move", false), this);
            }
        }
        if (actionIntent != null) {
            if (actionIntent.equals(Intent.ACTION_GET_CONTENT)) {

                // file picker intent
                mReturnIntent = true;
                Toast.makeText(this, getString(R.string.pick_a_file), Toast.LENGTH_LONG).show();

                // disable screen rotation just for convenience purpose
                // TODO: Support screen rotation when picking file
                Utils.disableScreenRotation(this);
            } else if (actionIntent.equals(RingtoneManager.ACTION_RINGTONE_PICKER)) {
                // ringtone picker intent
                mReturnIntent = true;
                mRingtonePickerIntent = true;
                Toast.makeText(this, getString(R.string.pick_a_file), Toast.LENGTH_LONG).show();

                // disable screen rotation just for convenience purpose
                // TODO: Support screen rotation when picking file
                Utils.disableScreenRotation(this);
            } else if (actionIntent.equals(Intent.ACTION_VIEW)) {

                // zip viewer intent
                Uri uri = intent.getData();
                openzip = true;
                zippath = uri.toString();
            } else if (actionIntent.equals(Intent.ACTION_SEND) && typeIntent != null) {
                // save a single file to filesystem

                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                ArrayList<Uri> uris = new ArrayList<>();
                uris.add(uri);
                initFabToSave(uris);

                // disable screen rotation just for convenience purpose
                // TODO: Support screen rotation when saving a file
                Utils.disableScreenRotation(this);
            } else if (actionIntent.equals(Intent.ACTION_SEND_MULTIPLE) && typeIntent != null) {
                // save multiple files to filesystem

                ArrayList<Uri> arrayList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                initFabToSave(arrayList);

                // disable screen rotation just for convenience purpose
                // TODO: Support screen rotation when saving a file
                Utils.disableScreenRotation(this);
            }
        }

        if (savedInstanceState != null) {

            selectedStorage = savedInstanceState.getInt("selectitem", SELECT_0);
        }

        // setting window background color instead of each item, in order to reduce pixel overdraw
        if (getAppTheme().equals(AppTheme.LIGHT)) {
            /*if(Main.IS_LIST)
                getWindow().setBackgroundDrawableResource(android.R.color.white);
            else
                getWindow().setBackgroundDrawableResource(R.color.grid_background_light);
            */
            getWindow().setBackgroundDrawableResource(android.R.color.white);
        } else {
            getWindow().setBackgroundDrawableResource(R.color.holo_dark_background);
        }

        if (getAppTheme().equals(AppTheme.DARK)) {
            mDrawerList.setBackgroundColor(ContextCompat.getColor(this, R.color.holo_dark_background));
        }
        mDrawerList.setDivider(null);
        if (!isDrawerLocked) {
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer_l,  /* nav drawer image to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description for accessibility */
                    R.string.drawer_close  /* "close drawer" description for accessibility */
            ) {
                public void onDrawerClosed(View view) {
                    mainActivity.onDrawerClosed();
                }

                public void onDrawerOpened(View drawerView) {
                    //title.setText("Amaze File Manager");
                    // creates call to onPrepareOptionsMenu()
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_l);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerToggle.syncState();
        }
        /*((ImageButton) findViewById(R.id.drawer_buttton)).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(mDrawerLinear)) {
                    mDrawerLayout.closeDrawer(mDrawerLinear);
                } else mDrawerLayout.openDrawer(mDrawerLinear);
            }
        });*/
        if (mDrawerToggle != null) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer_l);
        }
        //recents header color implementation
        if (SDK_INT >= 21) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("Amaze",
                    ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap(),
                    getColorPreference().getColor(ColorUsage.getPrimary(MainActivity.currentTab)));
            setTaskDescription(taskDescription);
        }

        if (!sharedPref.getBoolean(KEY_PREFERENCE_BOOKMARKS_ADDED, false)) {
            utilsHandler.addCommonBookmarks();
            sharedPref.edit().putBoolean(KEY_PREFERENCE_BOOKMARKS_ADDED, true).commit();
        }

        AppConfig.runInBackground(new AppConfig.CustomAsyncCallbacks() {
            @Override
            public <E> E doInBackground() {

                dataUtils.setHiddenfiles(utilsHandler.getHiddenList());
                dataUtils.setGridfiles(utilsHandler.getGridViewList());
                dataUtils.setListfiles(utilsHandler.getListViewList());
                dataUtils.setBooks(utilsHandler.getBookmarksList());
                dataUtils.setServers(utilsHandler.getSmbList());

                return null;
            }

            @Override
            public Void onPostExecute(Object result) {

                refreshDrawer();

                if (savedInstanceState == null) {
                    if (openProcesses) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content_frame, new ProcessViewer(), KEY_INTENT_PROCESS_VIEWER);
                        //transaction.addToBackStack(null);
                        selectedStorage = SELECT_102;
                        openProcesses = false;
                        //title.setText(utils.getString(con, R.string.process_viewer));
                        //Commit the transaction
                        transaction.commit();
                        supportInvalidateOptionsMenu();
                    }  else if (intent.getAction() != null &&
                            intent.getAction().equals(TileService.ACTION_QS_TILE_PREFERENCES)) {
                        // tile preferences, open ftp fragment

                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                        transaction2.replace(R.id.content_frame, new FTPServerFragment());
                        appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

                        selectedStorage = SELECT_MINUS_2;
                        adapter.toggleChecked(false);
                        transaction2.commit();
                    } else {
                        if (path != null && path.length() > 0) {
                            HFile file = new HFile(OpenMode.UNKNOWN, path);
                            file.generateMode(MainActivity.this);
                            if (file.isDirectory(MainActivity.this))
                                goToMain(path);
                            else {
                                goToMain("");
                                utils.openFile(new File(path), MainActivity.this);
                            }
                        } else {
                            firstGoToMain();
                        }
                    }
                } else {
                    COPY_PATH = savedInstanceState.getParcelableArrayList("COPY_PATH");
                    MOVE_PATH = savedInstanceState.getParcelableArrayList("MOVE_PATH");
                    oppathe = savedInstanceState.getString("oppathe");
                    oppathe1 = savedInstanceState.getString("oppathe1");
                    oparrayList = savedInstanceState.getParcelableArrayList("oparrayList");
                    operation = savedInstanceState.getInt("operation");
                    selectedStorage = savedInstanceState.getInt("selectitem", SELECT_0);
                    //mainFragment = (Main) savedInstanceState.getParcelable("main_fragment");
                    adapter.toggleChecked(selectedStorage);
                }
                return null;
            }

            @Override
            public Void onPreExecute() {
                return null;
            }

            @Override
            public Void publishResult(Object... result) {
                return null;
            }

            @Override
            public <T> T[] params() {
                return null;
            }
        });
    }

    /**
     * Initializes the floating action button to act as to save data from an external intent
     */
    private void initFabToSave(final ArrayList<Uri> uris) {

        floatingActionButton.setVisibility(View.VISIBLE);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_file_download_black_24dp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) drawable;
            vectorDrawable.setTint(getResources().getColor(android.R.color.white));

            floatingActionButton.getMenuIconView().setImageDrawable(vectorDrawable);
        } else {

            VectorDrawableCompat vectorDrawableCompat = (VectorDrawableCompat) drawable;
            vectorDrawableCompat.setTint(getResources().getColor(android.R.color.white));

            floatingActionButton.getMenuIconView().setImageDrawable(vectorDrawableCompat);
        }


        floatingActionButton.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileUtil.writeUriToStorage(MainActivity.this, uris, getContentResolver(), getCurrentMainFragment().getCurrentPath());
                Toast.makeText(MainActivity.this, getResources().getString(R.string.saving), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    /**
     * Initializes an interactive shell, which will stay throughout the app lifecycle
     * The shell is associated with a handler thread which maintain the message queue from the
     * callbacks of shell as we certainly cannot allow the callbacks to run on same thread because
     * of possible deadlock situation and the asynchronous behaviour of LibSuperSU
     */
    private void initializeInteractiveShell() {
        // only one looper can be associated to a thread. So we're making sure not to create new
        // handler threads every time the code relaunch.
        if (rootMode) {
            handlerThread = new HandlerThread("handler");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
            shellInteractive = (new Shell.Builder()).useSU().setHandler(handler).open();

            // TODO: check for busybox
            /*try {
                if (!RootUtils.isBusyboxAvailable()) {
                    Toast.makeText(this, getString(R.string.error_busybox), Toast.LENGTH_LONG).show();
                    closeInteractiveShell();
                    sharedPref.edit().putBoolean(PreferenceUtils.KEY_ROOT, false).apply();
                }
            } catch (RootNotPermittedException e) {
                e.printStackTrace();
                sharedPref.edit().putBoolean(PreferenceUtils.KEY_ROOT, false).apply();
            }*/
        }
    }

    /**
     * Returns all available SD-Cards in the system (include emulated)
     * <p>
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standard way to get it.
     * TODO: Test on future Android versions 4.4+
     *
     * @return paths to all available SD-Cards in the system (include emulated)
     */
    public synchronized ArrayList<String> getStorageDirectories() {
        // Final set of paths
        final ArrayList<String> rv = new ArrayList<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPARATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        if (SDK_INT >= Build.VERSION_CODES.M && checkStoragePermission())
            rv.clear();
        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String strings[] = FileUtil.getExtSdCardPathsForActivity(this);
            for (String s : strings) {
                File f = new File(s);
                if (!rv.contains(s) && Futils.canListFiles(f))
                    rv.add(s);
            }
        }
        if (ThemedActivity.rootMode)
            rv.add("/");
        File usb = getUsbDrive();
        if (usb != null && !rv.contains(usb.getPath())) rv.add(usb.getPath());

        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isUsbDeviceConnected()) rv.add(OTGUtil.PREFIX_OTG + "/");
        }
        return rv;
    }

    /**
     * Method finds whether a USB device is connected or not
     * @return true if device is connected
     */
    private boolean isUsbDeviceConnected() {
        UsbManager usbManager = (UsbManager) getSystemService(USB_SERVICE);
        if (usbManager.getDeviceList().size()!=0) {
            // we need to set this every time as there is no way to know that whether USB device was
            // disconnected after closing the app and another one was connected
            // in that case the uri will obviously change
            // other wise we could persist the uri even after reopening the app by not writing
            // this preference when it's not null
            sharedPref.edit().putString(KEY_PREF_OTG, VALUE_PREF_OTG_NULL).apply();
            return true;
        } else {
            sharedPref.edit().putString(KEY_PREF_OTG, null).apply();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!isDrawerLocked) {
            if (mDrawerLayout.isDrawerOpen(mDrawerLinear)) {
                mDrawerLayout.closeDrawer(mDrawerLinear);
            } else {
                onbackpressed();
            }
        } else onbackpressed();
    }

    void onbackpressed() {
        Fragment fragment = getFragmentAtFrame();
        if (getAppbar().getSearchView().isShown()) {
            // hide search view if visible, with an animation
            getAppbar().getSearchView().hideSearchView();
        } else if (fragment instanceof TabFragment) {
            if (floatingActionButton.isOpened()) {
                floatingActionButton.close(true);
            } else {
                getCurrentMainFragment().goBack();
            }
        } else if (fragment instanceof ZipViewer) {
            ZipViewer zipViewer = (ZipViewer) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (zipViewer.mActionMode == null) {
                if (zipViewer.canGoBack()) {
                    zipViewer.goBack();
                } else if (openzip) {
                    openzip = false;
                    finish();
                } else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_out_bottom, R.anim.slide_out_bottom);
                    fragmentTransaction.remove(zipViewer);
                    fragmentTransaction.commit();
                    supportInvalidateOptionsMenu();
                    floatingActionButton.setVisibility(View.VISIBLE);
                    floatingActionButton.showMenuButton(true);
                }
            } else {
                zipViewer.mActionMode.finish();
            }
        } else if (fragment instanceof FTPServerFragment) {
            //returning back from FTP server
            if (path != null && path.length() > 0) {
                HFile file = new HFile(OpenMode.UNKNOWN, path);
                file.generateMode(this);
                if (file.isDirectory(this))
                    goToMain(path);
                else {
                    goToMain("");
                    utils.openFile(new File(path), this);
                }
            } else {
                goToMain("");
            }
        }else if (fragment instanceof HomeFragment) {
            exit();
        } else {goToMain("");}
    }

    public void invalidatePasteButton(MenuItem paste) {
        if (MOVE_PATH != null || COPY_PATH != null) {
            paste.setVisible(true);
        } else {
            paste.setVisible(false);
        }
    }

    public void exit() {
        if (backPressedToExitOnce) {
            finish();
            if (ThemedActivity.rootMode) {
                // TODO close all shells
            }
        } else {
            this.backPressedToExitOnce = true;
            showToast(getString(R.string.pressagain));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 2000);
        }
    }

    public void updateDrawer(String path) {
        new AsyncTask<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... strings) {
                String path = strings[0];
                int k = 0, i = 0;
                String entryItemPathOld = "";
                for (Item item : dataUtils.getList()) {
                    if (!item.isSection()) {

                        String entryItemPath = ((EntryItem) item).getPath();

                        if (path.contains(((EntryItem) item).getPath())) {

                            if (entryItemPath.length() > entryItemPathOld.length()) {
                                // we don't need to match with the quick search drawer items
                                // whether current entry item path is bigger than the older one found,
                                // for eg. when we have /storage and /storage/Movies as entry items
                                // we would choose to highlight /storage/Movies in drawer adapter
                                k = i;
                                entryItemPathOld = entryItemPath;
                            }
                        }
                    }
                    i++;
                }
                return k;
            }

            @Override
            public void onPostExecute(Integer integers) {
                if (adapter != null)
                    adapter.toggleChecked(integers);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);

    }

    public void goToMain(String path) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //title.setText(R.string.app_name);
        TabFragment tabFragment = new TabFragment();
        if (path != null && path.length() > 0) {
            Bundle b = new Bundle();
            b.putString("path", path);
            tabFragment.setArguments(b);
        }

        transaction.replace(R.id.content_frame, tabFragment);
        // Commit the transaction
        selectedStorage = SELECT_0;
        transaction.addToBackStack("tabt" + 1);
        transaction.commitAllowingStateLoss();
        appbar.setTitle(null);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.showMenuButton(true);
        if (openzip && zippath != null) {
            if (zippath.endsWith(".zip") || zippath.endsWith(".apk")) openZip(zippath);
            else {
                openRar(zippath);
            }
            zippath = null;
        }
    }

    public void firstGoToMain() {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //title.setText(R.string.app_name);
        HomeFragment homeFragment = new HomeFragment();
        if (path != null && path.length() > 0) {
            Bundle b = new Bundle();
            b.putString("path", path);
            homeFragment.setArguments(b);
        }

        transaction.replace(R.id.content_frame, homeFragment);
        selectedStorage = SELECT_0;
        transaction.addToBackStack("tabt" + 1);
        transaction.commitAllowingStateLoss();
        appbar.setTitle(null);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.showMenuButton(true);
        if (openzip && zippath != null) {
            if (zippath.endsWith(".zip") || zippath.endsWith(".apk")) openZip(zippath);
            else {
                openRar(zippath);
            }
            zippath = null;
        }
    }

    public void selectItem(final int i) {
        ArrayList<Item> directoryItems = dataUtils.getList();
        if (!directoryItems.get(i).isSection()) {
            if ((selectedStorage == NO_VALUE || selectedStorage >= directoryItems.size())) {
                TabFragment tabFragment = new TabFragment();
                Bundle a = new Bundle();
                a.putString("path", ((EntryItem) directoryItems.get(i)).getPath());

                tabFragment.setArguments(a);

                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, tabFragment);

                transaction.addToBackStack("tabt1" + 1);
                pending_fragmentTransaction = transaction;
                selectedStorage = i;
                adapter.toggleChecked(selectedStorage);
                if (!isDrawerLocked) mDrawerLayout.closeDrawer(mDrawerLinear);
                else onDrawerClosed();
                floatingActionButton.setVisibility(View.VISIBLE);
                floatingActionButton.showMenuButton(true);
            } else {
                pendingPath = ((EntryItem) directoryItems.get(i)).getPath();

                selectedStorage = i;
                adapter.toggleChecked(selectedStorage);

                if (((EntryItem) directoryItems.get(i)).getPath().contains(OTGUtil.PREFIX_OTG) &&
                        sharedPref.getString(KEY_PREF_OTG, null).equals(VALUE_PREF_OTG_NULL)) {
                    // we've not gotten otg path yet
                    // start system request for storage access framework
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.otg_access), Toast.LENGTH_LONG).show();
                    Intent safIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    startActivityForResult(safIntent, REQUEST_CODE_SAF);
                } else {
                    if (!isDrawerLocked) mDrawerLayout.closeDrawer(mDrawerLinear);
                    else onDrawerClosed();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_extra, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem s = menu.findItem(R.id.view);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem paste = menu.findItem(R.id.paste);
        String fragmentName;
        Fragment fragment;
        try {
            fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            fragmentName = fragment.getClass().getName();
        } catch (Exception e) {
            return true;
        }
        if (fragmentName.contains("TabFragment")) {
            appbar.setTitle(getResources().getString(R.string.app_name));
            if (useGridView) {
                s.setTitle(getResources().getString(R.string.gridview));
            } else {
                s.setTitle(getResources().getString(R.string.listview));
            }
            try {
                TabFragment tabFragment = (TabFragment) fragment;
                MainFragment ma = getCurrentMainFragment();
                if (ma.IS_LIST) s.setTitle(R.string.gridview);
                else s.setTitle(R.string.listview);
                appbar.getBottomBar().updatePath(ma.getCurrentPath(), ma.results, MainActivityHelper.SEARCH_TEXT, ma.openMode, ma.folder_count, ma.file_count);
            } catch (Exception e) {}

            appbar.getBottomBar().setClickListener();

            invalidatePasteButton(paste);
            search.setVisible(true);
            if (indicator_layout != null) indicator_layout.setVisibility(View.VISIBLE);
            menu.findItem(R.id.search).setVisible(true);
            menu.findItem(R.id.home).setVisible(true);
            menu.findItem(R.id.history).setVisible(true);
            menu.findItem(R.id.sethome).setVisible(true);
            menu.findItem(R.id.sort).setVisible(true);
            if (showHidden) menu.findItem(R.id.hiddenitems).setVisible(true);
            menu.findItem(R.id.view).setVisible(true);
            menu.findItem(R.id.extract).setVisible(false);
            invalidatePasteButton(menu.findItem(R.id.paste));
            findViewById(R.id.buttonbarframe).setVisibility(View.VISIBLE);
        } else if (fragmentName.contains("AppsList") || fragmentName.contains("ProcessViewer") ||
                fragmentName.contains(FTPServerFragment.class.getName()) ) {
            appBarLayout.setExpanded(true);
            menu.findItem(R.id.sethome).setVisible(false);
            if (indicator_layout != null) indicator_layout.setVisibility(View.GONE);
            findViewById(R.id.buttonbarframe).setVisibility(View.GONE);
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.home).setVisible(false);
            menu.findItem(R.id.history).setVisible(false);
            menu.findItem(R.id.extract).setVisible(false);
            if (fragmentName.contains("ProcessViewer")) menu.findItem(R.id.sort).setVisible(false);
            else {
                menu.findItem(R.id.dsort).setVisible(false);
                menu.findItem(R.id.sortby).setVisible(false);
            }
            menu.findItem(R.id.hiddenitems).setVisible(false);
            menu.findItem(R.id.view).setVisible(false);
            menu.findItem(R.id.paste).setVisible(false);
        } else if(fragmentName.contains(HomeFragment.class.getName())){
            appBarLayout.setExpanded(true);
            menu.findItem(R.id.sethome).setVisible(false);
            if (indicator_layout != null) indicator_layout.setVisibility(View.GONE);
            findViewById(R.id.buttonbarframe).setVisibility(View.GONE);
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.home).setVisible(false);
            menu.findItem(R.id.history).setVisible(false);
            menu.findItem(R.id.extract).setVisible(false);
            menu.findItem(R.id.dsort).setVisible(false);
            menu.findItem(R.id.sortby).setVisible(false);
            menu.findItem(R.id.hiddenitems).setVisible(false);
            menu.findItem(R.id.view).setVisible(false);
            menu.findItem(R.id.paste).setVisible(false);
            menu.findItem(R.id.exit).setVisible(false);
            menu.findItem(R.id.sort).setVisible(false);
            menu.findItem(R.id.recommend).setVisible(true);
        } else if (fragmentName.contains("ZipViewer")) {
            menu.findItem(R.id.sethome).setVisible(false);
            if (indicator_layout != null) indicator_layout.setVisibility(View.GONE);
            getAppbar().getBottomBar().resetClickListener();
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.home).setVisible(false);
            menu.findItem(R.id.history).setVisible(false);
            menu.findItem(R.id.sort).setVisible(false);
            menu.findItem(R.id.hiddenitems).setVisible(false);
            menu.findItem(R.id.view).setVisible(false);
            menu.findItem(R.id.paste).setVisible(false);
            menu.findItem(R.id.extract).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
    }

    void killToast() {
        if (this.toast != null)
            this.toast.cancel();
    }

    // called when the user exits the action mode
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        MainFragment ma = getCurrentMainFragment();

        switch (item.getItemId()) {
            case R.id.home:
                if (ma != null)
                    ma.home();
                break;
            case R.id.history:
                if (ma != null)
                    GeneralDialogCreation.showHistoryDialog(dataUtils, utils, ma, getAppTheme());
                break;
            case R.id.sethome:
                if (ma == null) return super.onOptionsItemSelected(item);
                final MainFragment main = ma;
                if (main.openMode != OpenMode.FILE && main.openMode != OpenMode.ROOT) {
                    Toast.makeText(mainActivity, R.string.not_allowed, Toast.LENGTH_SHORT).show();
                    break;
                }
                final MaterialDialog dialog = GeneralDialogCreation.showBasicDialog(mainActivity,
                        new String[]{getResources().getString(R.string.questionset),
                                getResources().getString(R.string.setashome), getResources().getString(R.string.yes), getResources().getString(R.string.no), null});
                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main.home = main.getCurrentPath();
                        updatePaths(main.no);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.sort:
                Fragment fragment = getFragmentAtFrame();
                if (fragment.getClass().getName().contains("AppsList"))
                    GeneralDialogCreation.showSortDialog((AppsList) fragment, getAppTheme());
                break;
            case R.id.sortby:
                if (ma != null)
                    GeneralDialogCreation.showSortDialog(ma, getAppTheme(), sharedPref);
                break;
            case R.id.dsort:
                if (ma == null) return super.onOptionsItemSelected(item);
                String[] sort = getResources().getStringArray(R.array.directorysortmode);
                MaterialDialog.Builder builder = new MaterialDialog.Builder(mainActivity);
                builder.theme(getAppTheme().getMaterialDialogTheme());
                builder.title(R.string.directorysort);
                int current = Integer.parseInt(sharedPref.getString("dirontop", "0"));

                final MainFragment mainFrag = ma;

                builder.items(sort).itemsCallbackSingleChoice(current, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        sharedPref.edit().putString("dirontop", "" + which).commit();
                        mainFrag.getSortModes();
                        mainFrag.updateList();
                        dialog.dismiss();
                        return true;
                    }
                });
                builder.build().show();
                break;
            case R.id.hiddenitems:
                GeneralDialogCreation.showHiddenDialog(dataUtils, utils, ma, getAppTheme());
                break;
            case R.id.view:
                final MainFragment mainFragment = ma;
                if (ma.IS_LIST) {
                    if (dataUtils.getListfiles().contains(ma.getCurrentPath())) {
                        dataUtils.getListfiles().remove(ma.getCurrentPath());

                        AppConfig.runInBackground(new Runnable() {
                            @Override
                            public void run() {
                                utilsHandler.removeListViewPath(mainFragment.getCurrentPath());
                            }
                        });
                        //grid.removePath(ma.CURRENT_PATH, DataUtils.LIST);
                    }

                    AppConfig.runInBackground(new Runnable() {
                        @Override
                        public void run() {
                            utilsHandler.addGridView(mainFragment.getCurrentPath());
                        }
                    });
                    //grid.addPath(null, ma.CURRENT_PATH, DataUtils.GRID, 0);
                    dataUtils.getGridFiles().add(ma.getCurrentPath());
                } else {
                    if (dataUtils.getGridFiles().contains(ma.getCurrentPath())) {
                        dataUtils.getGridFiles().remove(ma.getCurrentPath());
                        //grid.removePath(ma.CURRENT_PATH, DataUtils.GRID);

                        AppConfig.runInBackground(new Runnable() {
                            @Override
                            public void run() {
                                utilsHandler.removeGridViewPath(mainFragment.getCurrentPath());
                            }
                        });
                    }

                    AppConfig.runInBackground(new Runnable() {
                        @Override
                        public void run() {
                            utilsHandler.addListView(mainFragment.getCurrentPath());
                        }
                    });
                    //grid.addPath(null, ma.CURRENT_PATH, DataUtils.LIST, 0);
                    dataUtils.getListfiles().add(ma.getCurrentPath());
                }
                ma.switchView();
                break;
            case R.id.paste:
                String path = ma.getCurrentPath();
                ArrayList<BaseFile> arrayList = COPY_PATH != null? COPY_PATH:MOVE_PATH;
                boolean move = MOVE_PATH != null;
                new CopyFileCheck(ma, path, move, mainActivity, ThemedActivity.rootMode)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arrayList);
                COPY_PATH = null;
                MOVE_PATH = null;
                invalidatePasteButton(item);
                break;
            case R.id.extract:
                Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (fragment1.getClass().getName().contains("ZipViewer"))
                    mainActivityHelper.extractFile(((ZipViewer) fragment1).f);
                break;
            case R.id.search:
                getAppbar().getSearchView().revealSearchView();
                break;
            case R.id.recommend:
                Intent intent = new Intent(mainActivity, AppStoreActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        COPY_PATH=savedInstanceState.getStringArrayList("COPY_PATH");
        MOVE_PATH=savedInstanceState.getStringArrayList("MOVE_PATH");
        oppathe = savedInstanceState.getString("oppathe");
        oppathe1 = savedInstanceState.getString("oppathe1");
        oparrayList = savedInstaniceState.getStringArrayList("oparrayList");
        opnameList=savedInstanceState.getStringArrayList("opnameList");
        operation = savedInstanceState.getInt("operation");
        selectedStorage = savedInstanceState.getInt("selectitem", 0);
    }*/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        if (mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedStorage != NO_VALUE)
            outState.putInt("selectitem", selectedStorage);
        if (COPY_PATH != null)
            outState.putParcelableArrayList("COPY_PATH", COPY_PATH);
        if (MOVE_PATH != null)
            outState.putParcelableArrayList("MOVE_PATH", MOVE_PATH);
        if (oppathe != null) {
            outState.putString("oppathe", oppathe);
            outState.putString("oppathe1", oppathe1);
            outState.putParcelableArrayList("oparraylist", (oparrayList));
            outState.putInt("operation", operation);
        }
        /*if (mainFragment!=null) {
            outState.putParcelable("main_fragment", mainFragment);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mainActivityHelper.mNotificationReceiver);
        unregisterReceiver(receiver2);

        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            unregisterReceiver(mOtgReceiver);
        }
        killToast();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (materialDialog != null && !materialDialog.isShowing()) {
            materialDialog.show();
            materialDialog = null;
        }

        IntentFilter newFilter = new IntentFilter();
        newFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        newFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        newFilter.addDataScheme(ContentResolver.SCHEME_FILE);
        registerReceiver(mainActivityHelper.mNotificationReceiver, newFilter);
        registerReceiver(receiver2, new IntentFilter(TAG_INTENT_FILTER_GENERAL));

        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Registering intent filter for OTG
            IntentFilter otgFilter = new IntentFilter();
            otgFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            otgFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(mOtgReceiver, otgFilter);
        }
    }

    /**
     * Receiver to check if a USB device is connected at the runtime of application
     * If device is not connected at runtime (i.e. it was connected when the app was closed)
     * then {@link #isUsbDeviceConnected()} method handles the connection through
     * {@link #getStorageDirectories()}
     */
    BroadcastReceiver mOtgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                sharedPref.edit().putString(KEY_PREF_OTG, VALUE_PREF_OTG_NULL).apply();
                refreshDrawer();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                sharedPref.edit().putString(KEY_PREF_OTG, null).apply();
                refreshDrawer();
                goToMain("");
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            /*
            ImageView ib = (ImageView) findViewById(R.id.action_overflow);
            if (ib.getVisibility() == View.VISIBLE) {
                ib.performClick();
            }
            */
            // return 'true' to prevent further propagation of the key event
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: 6/5/2017 Android may choose to not call this method before destruction
        // TODO: https://developer.android.com/reference/android/app/Activity.html#onDestroy%28%29
        closeInteractiveShell();

        tabHandler.close();
        utilsHandler.close();
        cloudHandler.close();

        CryptHandler cryptHandler = new CryptHandler(this);
        cryptHandler.close();

        /*if (mainFragment!=null)
            mainFragment = null;*/
    }

    /**
     * Closes the interactive shell and threads associated
     */
    private void closeInteractiveShell() {
        if (rootMode) {
            // close interactive shell and handler thread associated with it
            if (SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                // let it finish up first with what it's doing
                handlerThread.quitSafely();
            } else handlerThread.quit();
            shellInteractive.close();
        }
    }

    public void updatePaths(int pos) {
        TabFragment tabFragment = getTabFragment();
        if (tabFragment != null)
            tabFragment.updatepaths(pos);
    }

    public void openZip(String path) {
        appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_in_bottom);
        Fragment zipFragment = new ZipViewer();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        zipFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.content_frame, zipFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void openRar(String path) {
        openZip(path);
    }

    public MainFragment getCurrentMainFragment() {
        TabFragment tab = getTabFragment();

        if(tab != null && tab.getCurrentTabFragment() instanceof MainFragment) {
            return (MainFragment) tab.getCurrentTabFragment();
        } else return null;
    }

    public TabFragment getTabFragment() {
        Fragment fragment = getFragmentAtFrame();

        if (!(fragment instanceof TabFragment)) return null;
        else return (TabFragment) fragment;
    }

    public Fragment getFragmentAtFrame() {
        return getSupportFragmentManager().findFragmentById(R.id.content_frame);
    }

    public void setPagingEnabled(boolean b) {
        getTabFragment().mViewPager.setPagingEnabled(b);
    }

    public File getUsbDrive() {
        File parent = new File("/storage");

        try {
            for (File f : parent.listFiles())
                if (f.exists() && f.getName().toLowerCase().contains("usb") && f.canExecute())
                    return f;
        } catch (Exception e) {}

        parent = new File("/mnt/sdcard/usbStorage");
        if (parent.exists() && parent.canExecute())
            return (parent);
        parent = new File("/mnt/sdcard/usb_storage");
        if (parent.exists() && parent.canExecute())
            return parent;

        return null;
    }

    public void refreshDrawer() {
        ArrayList<Item> sectionItems = new ArrayList<>();//initialize items
        ArrayList<String> storageDirectories = getStorageDirectories();

        storage_count = 0;
        for (String file : storageDirectories) {
            File f = new File(file);
            String name;
            Drawable icon1 = ContextCompat.getDrawable(this, R.drawable.ic_sd_storage_white_56dp);
            if ("/storage/emulated/legacy".equals(file) || "/storage/emulated/0".equals(file)) {
                name = getResources().getString(R.string.storage);
            } else if ("/storage/sdcard1".equals(file)) {
                name = getResources().getString(R.string.extstorage);
            } else if ("/".equals(file)) {
                name = getResources().getString(R.string.rootdirectory);
                icon1 = ContextCompat.getDrawable(this, R.drawable.ic_drawer_root_white);
            } else if (file.contains(OTGUtil.PREFIX_OTG)) {
                name = "OTG";
                icon1 = ContextCompat.getDrawable(this, R.drawable.ic_usb_white_48dp);
            } else name = f.getName();
            if (!f.isDirectory() || f.canExecute()) {
                storage_count++;
                sectionItems.add(new EntryItem(name, file, icon1));
            }
        }
        dataUtils.setStorages(storageDirectories);
        sectionItems.add(new SectionItem());

        if (dataUtils.getServers().size() > 0) {
            Collections.sort(dataUtils.getServers(), new BookSorter());
            synchronized (dataUtils.getServers()) {
                for (String[] file : dataUtils.getServers()) {
                    sectionItems.add(new EntryItem(file[0], file[1], ContextCompat.getDrawable(this,
                            R.drawable.ic_settings_remote_white_48dp)));
                }
            }
            sectionItems.add(new SectionItem());
        }

        ArrayList<String[]> accountAuthenticationList = new ArrayList<>();

        if (CloudSheetFragment.isCloudProviderAvailable(this)) {
            for (CloudStorage cloudStorage : dataUtils.getAccounts()) {
                if (cloudStorage instanceof Dropbox) {

                    sectionItems.add(new EntryItem(CloudHandler.CLOUD_NAME_DROPBOX,
                            CloudHandler.CLOUD_PREFIX_DROPBOX + "/",
                            ContextCompat.getDrawable(this, R.drawable.ic_dropbox_white_24dp)));

                    accountAuthenticationList.add(new String[] {
                            CloudHandler.CLOUD_NAME_DROPBOX,
                            CloudHandler.CLOUD_PREFIX_DROPBOX + "/",
                    });
                } else if (cloudStorage instanceof Box) {

                    sectionItems.add(new EntryItem(CloudHandler.CLOUD_NAME_BOX,
                            CloudHandler.CLOUD_PREFIX_BOX + "/",
                            ContextCompat.getDrawable(this, R.drawable.ic_box_white_24dp)));

                    accountAuthenticationList.add(new String[] {
                            CloudHandler.CLOUD_NAME_BOX,
                            CloudHandler.CLOUD_PREFIX_BOX + "/",
                    });
                } else if (cloudStorage instanceof OneDrive) {

                    sectionItems.add(new EntryItem(CloudHandler.CLOUD_NAME_ONE_DRIVE,
                            CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/",
                            ContextCompat.getDrawable(this, R.drawable.ic_onedrive_white_24dp)));

                    accountAuthenticationList.add(new String[] {
                            CloudHandler.CLOUD_NAME_ONE_DRIVE,
                            CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/",
                    });
                } else if (cloudStorage instanceof GoogleDrive) {

                    sectionItems.add(new EntryItem(CloudHandler.CLOUD_NAME_GOOGLE_DRIVE,
                            CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/",
                            ContextCompat.getDrawable(this, R.drawable.ic_google_drive_white_24dp)));

                    accountAuthenticationList.add(new String[] {
                            CloudHandler.CLOUD_NAME_GOOGLE_DRIVE,
                            CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/",
                    });
                }
            }
            Collections.sort(accountAuthenticationList, new BookSorter());

            if (accountAuthenticationList.size() != 0)
                sectionItems.add(new SectionItem());
        }

        if (sharedPref.getBoolean(PREFERENCE_SHOW_SIDEBAR_FOLDERS, true)) {
            if (dataUtils.getBooks().size() > 0) {

                Collections.sort(dataUtils.getBooks(), new BookSorter());

                synchronized (dataUtils.getBooks()) {
                    for (String[] file : dataUtils.getBooks()) {
                        sectionItems.add(new EntryItem(file[0], file[1],
                                ContextCompat.getDrawable(this, R.drawable.folder_fab)));
                    }
                }
                sectionItems.add(new SectionItem());
            }
        }
        Boolean[] quickAccessPref = TinyDB.getBooleanArray(sharedPref, QuickAccessPref.KEY,
                QuickAccessPref.DEFAULT);

        if (sharedPref.getBoolean(PREFERENCE_SHOW_SIDEBAR_QUICKACCESSES, true)) {
            if (quickAccessPref[0])
                sectionItems.add(new EntryItem(getResources().getString(R.string.quick), "5",
                        ContextCompat.getDrawable(this, R.drawable.ic_star_white_18dp)));
            if (quickAccessPref[1])
                sectionItems.add(new EntryItem(getResources().getString(R.string.recent), "6",
                        ContextCompat.getDrawable(this, R.drawable.ic_history_white_48dp)));
            if (quickAccessPref[2])
                sectionItems.add(new EntryItem(getResources().getString(R.string.images), "0",
                        ContextCompat.getDrawable(this, R.drawable.ic_doc_image)));
            if (quickAccessPref[3])
                sectionItems.add(new EntryItem(getResources().getString(R.string.videos), "1",
                        ContextCompat.getDrawable(this, R.drawable.ic_doc_video_am)));
            if (quickAccessPref[4])
                sectionItems.add(new EntryItem(getResources().getString(R.string.audio), "2",
                        ContextCompat.getDrawable(this, R.drawable.ic_doc_audio_am)));
            if (quickAccessPref[5])
                sectionItems.add(new EntryItem(getResources().getString(R.string.documents), "3",
                        ContextCompat.getDrawable(this, R.drawable.ic_doc_doc_am)));
            if (quickAccessPref[6])
                sectionItems.add(new EntryItem(getResources().getString(R.string.apks), "4",
                        ContextCompat.getDrawable(this, R.drawable.ic_doc_apk_grid)));
        } else {
            sectionItems.remove(sectionItems.size() - 1); //Deletes last divider
        }
//        sectionItems.add(new EntryItem(getResources().getString(R.string.home), "100",
//                ContextCompat.getDrawable(this, R.drawable.ic_home_white_24dp)));
        //add home fragment here

        dataUtils.setList(sectionItems);
        adapter = new DrawerAdapter(this, this, sectionItems, this, sharedPref);
        mDrawerList.setAdapter(adapter);
    }

    public AppBar getAppbar() {
        return appbar;
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {

        if (requestCode == image_selector_request_code) {
            if (sharedPref != null && intent != null && intent.getData() != null) {
                if (SDK_INT >= 19)
                    getContentResolver().takePersistableUriPermission(intent.getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharedPref.edit().putString("drawer_header_path", intent.getData().toString()).commit();
                setDrawerHeaderBackground();
            }
        } else if (requestCode == 3) {
            Uri treeUri;
            if (responseCode == Activity.RESULT_OK) {
                // Get Uri from Storage Access Framework.
                treeUri = intent.getData();
                // Persist URI - this is required for verification of writability.
                if (treeUri != null) sharedPref.edit().putString("URI", treeUri.toString()).commit();
            } else {
                // If not confirmed SAF, or if still not writable, then revert settings.
                /* DialogUtil.displayError(getActivity(), R.string.message_dialog_cannot_write_to_folder_saf, false, currentFolder);
                        ||!FileUtil.isWritableNormalOrSaf(currentFolder)*/
                return;
            }

            // After confirmation, update stored value of folder.
            // Persist access permissions.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            switch (operation) {
                case DataUtils.DELETE://deletion
                    new DeleteTask(null, mainActivity).execute((oparrayList));
                    break;
                case DataUtils.COPY://copying
                    //legacy compatibility
                    if(oparrayList != null && oparrayList.size() != 0) {
                        oparrayListList = new ArrayList<>();
                        oparrayListList.add(oparrayList);
                        oparrayList = null;
                        oppatheList = new ArrayList<>();
                        oppatheList.add(oppathe);
                        oppathe = "";
                    }
                    for (int i = 0; i < oparrayListList.size(); i++) {
                        Intent intent1 = new Intent(con, CopyService.class);
                        intent1.putExtra(CopyService.TAG_COPY_SOURCES, oparrayList.get(i));
                        intent1.putExtra(CopyService.TAG_COPY_TARGET, oppatheList.get(i));
                        ServiceWatcherUtil.runService(this, intent1);
                    }
                    break;
                case DataUtils.MOVE://moving
                    //legacy compatibility
                    if(oparrayList != null && oparrayList.size() != 0) {
                        oparrayListList = new ArrayList<>();
                        oparrayListList.add(oparrayList);
                        oparrayList = null;
                        oppatheList = new ArrayList<>();
                        oppatheList.add(oppathe);
                        oppathe = "";
                    }

                    new MoveFiles(oparrayListList, getCurrentMainFragment(),
                            getCurrentMainFragment().getActivity(), OpenMode.FILE)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, oppatheList);
                    break;
                case DataUtils.NEW_FOLDER://mkdir
                    mainActivityHelper.mkDir(RootHelper.generateBaseFile(new File(oppathe), true),
                            getCurrentMainFragment());
                    break;
                case DataUtils.RENAME:
                    MainFragment ma = getCurrentMainFragment();
                    mainActivityHelper.rename(ma.openMode, (oppathe),
                            (oppathe1), mainActivity, ThemedActivity.rootMode);
                    ma.updateList();
                    break;
                case DataUtils.NEW_FILE:
                    mainActivityHelper.mkFile(new HFile(OpenMode.FILE, oppathe), getCurrentMainFragment());

                    break;
                case DataUtils.EXTRACT:
                    mainActivityHelper.extractFile(new File(oppathe));
                    break;
                case DataUtils.COMPRESS:
                    mainActivityHelper.compressFiles(new File(oppathe), oparrayList);
            }
            operation = -1;
        } else if (requestCode == REQUEST_CODE_SAF && responseCode == Activity.RESULT_OK) {
            // otg access
            sharedPref.edit().putString(KEY_PREF_OTG, intent.getData().toString()).apply();

            if (!isDrawerLocked) mDrawerLayout.closeDrawer(mDrawerLinear);
            else onDrawerClosed();
        } else if (requestCode == REQUEST_CODE_SAF && responseCode != Activity.RESULT_OK) {
            // otg access not provided
            pendingPath = null;
        }
    }

    void initialisePreferences() {
        hidemode = sharedPref.getInt("hidemode", 0);
        showHidden = sharedPref.getBoolean("showHidden", false);
        useGridView = sharedPref.getBoolean("view", true);
        currentTab = sharedPref.getInt(PreferenceUtils.KEY_CURRENT_TAB, PreferenceUtils.DEFAULT_CURRENT_TAB);
        skinStatusBar = (PreferenceUtils.getStatusColor(getColorPreference().getColorAsString(ColorUsage.getPrimary(MainActivity.currentTab))));
        colourednavigation = sharedPref.getBoolean("colorednavigation", false);
    }

    protected void initAds(){
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-3456168518371304/2700959193");
        mAdView =   (AdView)  findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    void initialiseViews() {
        appBarLayout = getAppbar().getAppbarLayout();

        mScreenLayout = (CoordinatorLayout) findViewById(R.id.main_frame);
        buttonBarFrame = (FrameLayout) findViewById(R.id.buttonbarframe);

        //buttonBarFrame.setBackgroundColor(Color.parseColor(currentTab==1 ? skinTwo : skin));
        drawerHeaderLayout = getLayoutInflater().inflate(R.layout.drawerheader, null);
        drawerHeaderParent = (RelativeLayout) drawerHeaderLayout.findViewById(R.id.drawer_header_parent);
        drawerHeaderView = drawerHeaderLayout.findViewById(R.id.drawer_header);
        drawerHeaderView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent;
                if (SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                }
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, image_selector_request_code);
                return false;
            }
        });
        drawerProfilePic = (RoundedImageView) drawerHeaderLayout.findViewById(R.id.profile_pic);
        mGoogleName = (TextView) drawerHeaderLayout.findViewById(R.id.account_header_drawer_name);
        mGoogleId = (TextView) drawerHeaderLayout.findViewById(R.id.account_header_drawer_email);
        setSupportActionBar(getAppbar().getToolbar());
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        indicator_layout = findViewById(R.id.indicator_layout);
        mDrawerLinear = (ScrimInsetsRelativeLayout) findViewById(R.id.left_drawer);
        if (getAppTheme().equals(AppTheme.DARK)) mDrawerLinear.setBackgroundColor(Utils.getColor(this, R.color.holo_dark_background));
        else mDrawerLinear.setBackgroundColor(Color.WHITE);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerLayout.setStatusBarBackgroundColor(Color.parseColor((currentTab==1 ? skinTwo : skin)));
        mDrawerList = (ListView) findViewById(R.id.menu_drawer);
        drawerHeaderView.setBackgroundResource(R.drawable.drawer_title);
        //drawerHeaderParent.setBackgroundColor(Color.parseColor((currentTab==1 ? skinTwo : skin)));
        if (findViewById(R.id.tab_frame) != null) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, mDrawerLinear);
            mDrawerLayout.openDrawer(mDrawerLinear);
            mDrawerLayout.setScrimColor(Color.TRANSPARENT);
            isDrawerLocked = true;
        } else if (findViewById(R.id.tab_frame) == null) {

            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mDrawerLinear);
            mDrawerLayout.closeDrawer(mDrawerLinear);
            isDrawerLocked = false;
        }
        mDrawerList.addHeaderView(drawerHeaderLayout);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        fabBgView = findViewById(R.id.fab_bg);
        if (getAppTheme().equals(AppTheme.DARK)) {
            fabBgView.setBackgroundResource(R.drawable.fab_shadow_dark);
        }

        fabBgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionButton.close(true);
                if (getAppbar().getSearchView().isEnabled()) getAppbar().getSearchView().hideSearchView();
            }
        });

        ImageView divider = (ImageView) findViewById(R.id.divider1);
        if (getAppTheme().equals(AppTheme.LIGHT))
            divider.setImageResource(R.color.divider);
        else
            divider.setImageResource(R.color.divider_dark);

        setDrawerHeaderBackground();
        View settingsButton = findViewById(R.id.settingsbutton);
        if (getAppTheme().equals(AppTheme.DARK)) {
            settingsButton.setBackgroundResource(R.drawable.safr_ripple_black);
            ((ImageView) settingsButton.findViewById(R.id.settingicon)).setImageResource(R.drawable.ic_settings_white_48dp);
            ((TextView) settingsButton.findViewById(R.id.settingtext)).setTextColor(Utils.getColor(this, android.R.color.white));
        }
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(in);
                finish();
            }

        });
        View appButton = findViewById(R.id.appbutton);
        if (getAppTheme().equals(AppTheme.DARK)) {
            appButton.setBackgroundResource(R.drawable.safr_ripple_black);
            ((ImageView) appButton.findViewById(R.id.appicon)).setImageResource(R.drawable.home);
            ((TextView) appButton.findViewById(R.id.apptext)).setTextColor(Utils.getColor(this, android.R.color.white));
        }
        appButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.replace(R.id.content_frame, new HomeFragment());
                appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                pending_fragmentTransaction = transaction2;
                if (!isDrawerLocked) mDrawerLayout.closeDrawer(mDrawerLinear);
                else onDrawerClosed();
                selectedStorage = SELECT_MINUS_2;
                adapter.toggleChecked(false);
            }
        });

        View ftpButton = findViewById(R.id.ftpbutton);
        if (getAppTheme().equals(AppTheme.DARK)) {
            ftpButton.setBackgroundResource(R.drawable.safr_ripple_black);
            ((ImageView) ftpButton.findViewById(R.id.ftpicon)).setImageResource(R.drawable.ic_ftp_dark);
            ((TextView) ftpButton.findViewById(R.id.ftptext)).setTextColor(Utils.getColor(this, android.R.color.white));
        }
        ftpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.replace(R.id.content_frame, new FTPServerFragment());
                appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                pending_fragmentTransaction = transaction2;
                if (!isDrawerLocked) mDrawerLayout.closeDrawer(mDrawerLinear);
                else onDrawerClosed();
                selectedStorage = SELECT_MINUS_2;
                adapter.toggleChecked(false);
            }
        });
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor((currentTab==1 ? skinTwo : skin))));

        // status bar0
        if (SDK_INT == 20 || SDK_INT == 19) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            //tintManager.setStatusBarTintColor(Color.parseColor((currentTab==1 ? skinTwo : skin)));
            FrameLayout.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) findViewById(R.id.drawer_layout).getLayoutParams();
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            if (!isDrawerLocked) p.setMargins(0, config.getStatusBarHeight(), 0, 0);
        } else if (SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (isDrawerLocked) {
                window.setStatusBarColor((skinStatusBar));
            } else window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (colourednavigation)
                window.setNavigationBarColor(skinStatusBar);
        }
    }

    /**
     * Call this method when you need to update the MainActivity view components' colors based on
     * update in the {@link MainActivity#currentTab}
     * Warning - All the variables should be initialised before calling this method!
     */
    public void updateViews(ColorDrawable colorDrawable) {
        // appbar view color
        mainActivity.buttonBarFrame.setBackgroundColor(colorDrawable.getColor());
        // action bar color
        mainActivity.getSupportActionBar().setBackgroundDrawable(colorDrawable);
        // drawer status bar I guess
        mainActivity.mDrawerLayout.setStatusBarBackgroundColor(colorDrawable.getColor());
        // drawer header background
        mainActivity.drawerHeaderParent.setBackgroundColor(colorDrawable.getColor());

        if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // for lollipop devices, the status bar color
            mainActivity.getWindow().setStatusBarColor(colorDrawable.getColor());
            if (colourednavigation)
                mainActivity.getWindow().setNavigationBarColor(PreferenceUtils
                        .getStatusColor(colorDrawable.getColor()));
        } else if (SDK_INT == 20 || SDK_INT == 19) {

            // for kitkat devices, the status bar color
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(colorDrawable.getColor());
        }
    }

    void initialiseFab() {
        int colorAccent = getColorPreference().getColor(ColorUsage.ACCENT);
        int iconSkin = getColorPreference().getColor(ColorUsage.ICON_SKIN);

        floatingActionButton = (FloatingActionMenu) findViewById(R.id.menu);
        floatingActionButton.setMenuButtonColorNormal(colorAccent);
        floatingActionButton.setMenuButtonColorPressed(colorAccent);

        floatingActionButton.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean b) {
                if (b) utils.revealShow(fabBgView, true);
                else utils.revealShow(fabBgView, false);
            }
        });

        FloatingActionButton fabNewFolder = (FloatingActionButton) findViewById(R.id.menu_new_folder);
        fabNewFolder.setColorNormal(iconSkin);
        fabNewFolder.setColorPressed(iconSkin);
        fabNewFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivityHelper.add(MainActivityHelper.NEW_FOLDER);
                //utils.revealShow(fabBgView, false);
                floatingActionButton.close(true);
            }
        });
        FloatingActionButton fabNewFile = (FloatingActionButton) findViewById(R.id.menu_new_file);
        fabNewFile.setColorNormal(iconSkin);
        fabNewFile.setColorPressed(iconSkin);
        fabNewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityHelper.add(MainActivityHelper.NEW_FILE);
                //utils.revealShow(fabBgView, false);
                floatingActionButton.close(true);
            }
        });
        final FloatingActionButton floatingActionButton3 = (FloatingActionButton) findViewById(R.id.menu_new_cloud);
        floatingActionButton3.setColorNormal(iconSkin);
        floatingActionButton3.setColorPressed(iconSkin);
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityHelper.add(MainActivityHelper.NEW_CLOUD);
                //utils.revealShow(fabBgView, false);
                floatingActionButton.close(true);
            }
        });
    }

    public boolean copyToClipboard(Context context, String text) {
        try {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                    .getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText("Path copied to clipboard", text);
            clipboard.setPrimaryClip(clip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    public void renameBookmark(final String title, final String path) {
        if (dataUtils.containsBooks(new String[]{title, path}) != -1) {
            RenameBookmark renameBookmark = RenameBookmark.getInstance(title, path, getColorPreference().getColor(ColorUsage.ACCENT));
            if (renameBookmark != null)
                renameBookmark.show(getFragmentManager(), "renamedialog");
        }
    }

    void onDrawerClosed() {
        if (pending_fragmentTransaction != null) {
            pending_fragmentTransaction.commit();
            pending_fragmentTransaction = null;
        }

        if (pendingPath != null) {
            HFile hFile = new HFile(OpenMode.UNKNOWN, pendingPath);
            hFile.generateMode(this);
            if (hFile.isSimpleFile()) {
                utils.openFile(new File(pendingPath), mainActivity);
                pendingPath = null;
                return;
            }

            MainFragment mainFrag = getCurrentMainFragment();
            if (mainFrag != null) {
                mainFrag.loadlist(pendingPath, false, OpenMode.UNKNOWN);
            } else {
                goToMain(pendingPath);
                return;
            }
            pendingPath = null;
        }
        supportInvalidateOptionsMenu();
    }


    @Override
    public void onNewIntent(Intent i) {
        intent = i;
        path = i.getStringExtra("path");

        if (path != null) {
            if (new File(path).isDirectory()) {
                MainFragment ma = getCurrentMainFragment();
                if (ma != null) {
                    ma.loadlist(path, false, OpenMode.FILE);
                } else goToMain(path);
            } else utils.openFile(new File(path), mainActivity);
        } else if (i.getStringArrayListExtra(TAG_INTENT_FILTER_FAILED_OPS) != null) {
            ArrayList<BaseFile> failedOps = i.getParcelableArrayListExtra(TAG_INTENT_FILTER_FAILED_OPS);
            if (failedOps != null) {
                mainActivityHelper.showFailedOperationDialog(failedOps, i.getBooleanExtra("move", false), this);
            }
        } else if (i.getCategories() != null && i.getCategories().contains(CLOUD_AUTHENTICATOR_GDRIVE)) {

            // we used an external authenticator instead of APIs. Probably for Google Drive
            CloudRail.setAuthenticationResponse(intent);

        } else if ((openProcesses = i.getBooleanExtra(KEY_INTENT_PROCESS_VIEWER, false))) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new ProcessViewer(), KEY_INTENT_PROCESS_VIEWER);
            //   transaction.addToBackStack(null);
            selectedStorage = SELECT_102;
            openProcesses = false;
            //title.setText(utils.getString(con, R.string.process_viewer));
            //Commit the transaction
            transaction.commitAllowingStateLoss();
            supportInvalidateOptionsMenu();
        } else if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_GET_CONTENT)) {
                // file picker intent
                mReturnIntent = true;
                Toast.makeText(this, getString(R.string.pick_a_file), Toast.LENGTH_LONG).show();
            } else if (intent.getAction().equals(RingtoneManager.ACTION_RINGTONE_PICKER)) {
                // ringtone picker intent
                mReturnIntent = true;
                mRingtonePickerIntent = true;
                Toast.makeText(this, getString(R.string.pick_a_file), Toast.LENGTH_LONG).show();
            } else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
                // zip viewer intent
                Uri uri = intent.getData();
                zippath = uri.toString();
                openZip(zippath);
            }

            if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    if (sharedPref.getString(KEY_PREF_OTG, null) == null) {
                        sharedPref.edit().putString(KEY_PREF_OTG, VALUE_PREF_OTG_NULL).apply();
                        refreshDrawer();
                    }
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    sharedPref.edit().putString(KEY_PREF_OTG, null).apply();
                    refreshDrawer();
                }
            }
        }
    }

    void setDrawerHeaderBackground() {
        new Thread(new Runnable() {
            public void run() {
                if (sharedPref.getBoolean("plus_pic", false)) return;
                String path = sharedPref.getString("drawer_header_path", null);
                if (path == null) return;
                try {
                    final ImageView headerImageView = new ImageView(MainActivity.this);
                    headerImageView.setImageDrawable(drawerHeaderParent.getBackground());
                    mImageLoader.get(path, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            headerImageView.setImageBitmap(response.getBitmap());
                            drawerHeaderView.setBackgroundResource(R.drawable.amaze_header_2);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {}
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }

    private BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            if (i.getStringArrayListExtra(TAG_INTENT_FILTER_FAILED_OPS) != null) {
                ArrayList<BaseFile> failedOps = i.getParcelableArrayListExtra(TAG_INTENT_FILTER_FAILED_OPS);
                if (failedOps != null) {
                    mainActivityHelper.showFailedOperationDialog(failedOps, i.getBooleanExtra("move", false), mainActivity);
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == 77) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                refreshDrawer();
                TabFragment tabFragment = getTabFragment();
                boolean b = sharedPref.getBoolean("needtosethome", true);
                //reset home and current paths according to new storages
                if (b) {
                    tabHandler.clear();
                    if (storage_count > 1)
                        tabHandler.addTab(new Tab(1, "", ((EntryItem) dataUtils.getList().get(1)).getPath(), "/"));
                    else
                        tabHandler.addTab(new Tab(1, "", "/", "/"));
                    if (!dataUtils.getList().get(0).isSection()) {
                        String pa = ((EntryItem) dataUtils.getList().get(0)).getPath();
                        tabHandler.addTab(new Tab(2, "", pa, pa));
                    } else
                        tabHandler.addTab(new Tab(2, "", ((EntryItem) dataUtils.getList().get(1)).getPath(), "/"));
                    if (tabFragment != null) {
                        Fragment main = tabFragment.getFragmentAtIndex(0);
                        if (main != null)
                            ((MainFragment) main).updateTabWithDb(tabHandler.findTab(1));
                        Fragment main1 = tabFragment.getFragmentAtIndex(1);
                        if (main1 != null)
                            ((MainFragment) main1).updateTabWithDb(tabHandler.findTab(2));
                    }
                    sharedPref.edit().putBoolean("needtosethome", false).commit();
                } else {
                    //just refresh list
                    if (tabFragment != null) {
                        Fragment main = tabFragment.getFragmentAtIndex(0);
                        if (main != null)
                            ((MainFragment) main).updateList();
                        Fragment main1 = tabFragment.getFragmentAtIndex(1);
                        if (main1 != null)
                            ((MainFragment) main1).updateList();
                    }
                }
            } else {
                Toast.makeText(this, R.string.grantfailed, Toast.LENGTH_SHORT).show();
                requestStoragePermission();
            }
        }
    }

    public void showSMBDialog(String name, String path, boolean edit) {
        if (path.length() > 0 && name.length() == 0) {
            int i = dataUtils.containsServer(new String[]{name, path});
            if (i != -1)
                name = dataUtils.getServers().get(i)[0];
        }
        SmbConnectDialog smbConnectDialog = new SmbConnectDialog();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("path", path);
        bundle.putBoolean("edit", edit);
        smbConnectDialog.setArguments(bundle);
        smbConnectDialog.show(getFragmentManager(), "smbdailog");
    }

    /**
     * Shows a view that goes from white at it's lowest part to transparent a the top.
     * It covers the fragment.
     */
    public void showSmokeScreen() {
        Futils.revealShow(fabBgView, true);
    }

    public void hideSmokeScreen() {
        Futils.revealShow(fabBgView, false);
    }

    @Override
    public void addConnection(boolean edit, final String name, final String path, final String encryptedPath,
                              final String oldname, final String oldPath) {

        String[] s = new String[]{name, path};
        if (!edit) {
            if ((dataUtils.containsServer(path)) == -1) {
                dataUtils.addServer(s);
                refreshDrawer();

                AppConfig.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        utilsHandler.addSmb(name, encryptedPath);
                    }
                });
                //grid.addPath(name, encryptedPath, DataUtils.SMB, 1);
                MainFragment ma = getCurrentMainFragment();
                if (ma != null) getCurrentMainFragment().loadlist(path, false, OpenMode.UNKNOWN);
            } else {
                Snackbar.make(frameLayout, getResources().getString(R.string.connection_exists), Snackbar.LENGTH_SHORT).show();
            }
        } else {
            int i = dataUtils.containsServer(new String[]{oldname, oldPath});
            if (i != -1) {
                dataUtils.removeServer(i);

                AppConfig.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        utilsHandler.renameSMB(oldname, oldPath, name, path);
                    }
                });
                //mainActivity.grid.removePath(oldname, oldPath, DataUtils.SMB);
            }
            dataUtils.addServer(s);
            Collections.sort(dataUtils.getServers(), new BookSorter());
            mainActivity.refreshDrawer();
            //mainActivity.grid.addPath(name, encryptedPath, DataUtils.SMB, 1);
        }
    }

    @Override
    public void deleteConnection(final String name, final String path) {

        int i = dataUtils.containsServer(new String[]{name, path});
        if (i != -1) {
            dataUtils.removeServer(i);

            AppConfig.runInBackground(new Runnable() {
                @Override
                public void run() {
                    utilsHandler.removeSmbPath(name, path);
                }
            });
            //grid.removePath(name, path, DataUtils.SMB);
            refreshDrawer();
        }

    }

    @Override
    public void onHiddenFileAdded(String path) {

        utilsHandler.addHidden(path);
    }

    @Override
    public void onHiddenFileRemoved(String path) {

        utilsHandler.removeHiddenPath(path);
    }

    @Override
    public void onHistoryAdded(String path) {

        utilsHandler.addHistory(path);
    }

    @Override
    public void onBookAdded(String[] path, boolean refreshdrawer) {

        utilsHandler.addBookmark(path[0], path[1]);
        if (refreshdrawer)
            refreshDrawer();
    }

    @Override
    public void onHistoryCleared() {

        utilsHandler.clearHistoryTable();
    }

    @Override
    public void delete(String title, String path) {

        utilsHandler.removeBookmarksPath(title, path);
        refreshDrawer();

    }

    @Override
    public void modify(String oldpath, String oldname, String newPath, String newname) {

        utilsHandler.renameBookmark(oldname, oldpath, newname, newPath);
        refreshDrawer();
    }

    @Override
    public void onPreExecute(String query) {
        mainFragment.mSwipeRefreshLayout.setRefreshing(true);
        mainFragment.onSearchPreExecute(query);
    }

    @Override
    public void onPostExecute(String query) {
        mainFragment.onSearchCompleted(query);
        mainFragment.mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onProgressUpdate(BaseFile val , String query) {
        mainFragment.addSearchResult(val,query);
    }

    @Override
    public void onCancelled() {
        mainFragment.createViews(mainFragment.getLayoutElements(), false, mainFragment.getCurrentPath(),
                mainFragment.openMode, false, !mainFragment.IS_LIST);
        mainFragment.mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void addConnection(OpenMode service) {

        try {
            if (cloudHandler.findEntry(service) != null) {
                // cloud entry already exists
                Toast.makeText(this, getResources().getString(R.string.connection_exists),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
                Bundle args = new Bundle();
                args.putInt(ARGS_KEY_LOADER, service.ordinal());

                // check if we already had done some work on the loader
                Loader loader = getSupportLoaderManager().getLoader(REQUEST_CODE_CLOUD_LIST_KEY);
                if (loader != null && loader.isStarted()) {

                    // making sure that loader is not started
                    getSupportLoaderManager().destroyLoader(REQUEST_CODE_CLOUD_LIST_KEY);
                }

                getSupportLoaderManager().initLoader(REQUEST_CODE_CLOUD_LIST_KEY, args, this);
            }
        } catch (CloudPluginException e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.cloud_error_plugin),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void deleteConnection(OpenMode service) {

        cloudHandler.clear(service);
        dataUtils.removeAccount(service);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                refreshDrawer();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (cloudSyncTask != null && cloudSyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            cloudSyncTask.cancel(true);

        }

        Uri uri = Uri.withAppendedPath(Uri.parse("content://" + CloudContract.PROVIDER_AUTHORITY), "/keys.db/secret_keys");

        String[] projection = new String[] {
                CloudContract.COLUMN_ID,
                CloudContract.COLUMN_CLIENT_ID,
                CloudContract.COLUMN_CLIENT_SECRET_KEY
        };

        switch (id) {
            case REQUEST_CODE_CLOUD_LIST_KEY:
                Uri uriAppendedPath = uri;
                switch (OpenMode.getOpenMode(args.getInt(ARGS_KEY_LOADER, 2))) {
                    case GDRIVE:
                        uriAppendedPath = ContentUris.withAppendedId(uri, 2);
                        break;
                    case DROPBOX:
                        uriAppendedPath = ContentUris.withAppendedId(uri, 3);
                        break;
                    case BOX:
                        uriAppendedPath = ContentUris.withAppendedId(uri, 4);
                        break;
                    case ONEDRIVE:
                        uriAppendedPath = ContentUris.withAppendedId(uri, 5);
                        break;
                }
                return new CursorLoader(this, uriAppendedPath, projection, null, null, null);
            case REQUEST_CODE_CLOUD_LIST_KEYS:
                // we need a list of all secret keys

                try {
                    List<CloudEntry> cloudEntries = cloudHandler.getAllEntries();

                    // we want keys for services saved in database, and the cloudrail app key which
                    // is at index 1
                    String ids[] = new String[cloudEntries.size() + 1];

                    ids[0] = 1 + "";
                    for (int i=1; i<=cloudEntries.size(); i++) {

                        // we need to get only those cloud details which user wants
                        switch (cloudEntries.get(i-1).getServiceType()) {
                            case GDRIVE:
                                ids[i] = 2 + "";
                                break;
                            case DROPBOX:
                                ids[i] = 3 + "";
                                break;
                            case BOX:
                                ids[i] = 4 + "";
                                break;
                            case ONEDRIVE:
                                ids[i] = 5 + "";
                                break;
                        }
                    }
                    return new CursorLoader(this, uri, projection, CloudContract.COLUMN_ID, ids, null);
                } catch (CloudPluginException e) {
                    e.printStackTrace();

                    Toast.makeText(this, getResources().getString(R.string.cloud_error_plugin),
                            Toast.LENGTH_LONG).show();
                }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {

        if (data == null) {
            Toast.makeText(this, getResources().getString(R.string.cloud_error_failed_restart),
                    Toast.LENGTH_LONG).show();
            return;
        }

        cloudSyncTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {

                if (data.getCount() > 0 && data.moveToFirst()) {
                    do {

                        switch (data.getInt(0)) {
                            case 1:
                                try {
                                    CloudRail.setAppKey(data.getString(1));
                                } catch (Exception e) {
                                    // any other exception due to network conditions or other error
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.failed_cloud_api_key));
                                    return false;
                                }
                                break;
                            case 2:
                                // DRIVE
                                try {

                                    CloudEntry cloudEntryGdrive = null;
                                    CloudEntry savedCloudEntryGdrive;


                                    GoogleDrive cloudStorageDrive = new GoogleDrive(getApplicationContext(),
                                            data.getString(1), "", CLOUD_AUTHENTICATOR_REDIRECT_URI, data.getString(2));
                                    cloudStorageDrive.useAdvancedAuthentication();

                                    if ((savedCloudEntryGdrive = cloudHandler.findEntry(OpenMode.GDRIVE)) != null) {
                                        // we already have the entry and saved state, get it

                                        try {
                                            cloudStorageDrive.loadAsString(savedCloudEntryGdrive.getPersistData());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            // we need to update the persist string as existing one is been compromised

                                            cloudStorageDrive.login();
                                            cloudEntryGdrive = new CloudEntry(OpenMode.GDRIVE, cloudStorageDrive.saveAsString());
                                            cloudHandler.updateEntry(OpenMode.GDRIVE, cloudEntryGdrive);
                                        }

                                    } else {

                                        cloudStorageDrive.login();
                                        cloudEntryGdrive = new CloudEntry(OpenMode.GDRIVE, cloudStorageDrive.saveAsString());
                                        cloudHandler.addEntry(cloudEntryGdrive);
                                    }

                                    dataUtils.addAccount(cloudStorageDrive);
                                } catch (CloudPluginException e) {

                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.cloud_error_plugin));
                                    deleteConnection(OpenMode.GDRIVE);
                                    return false;
                                } catch (AuthenticationException e) {
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.cloud_fail_authenticate));
                                    deleteConnection(OpenMode.GDRIVE);
                                    return false;
                                } catch (Exception e) {
                                    // any other exception due to network conditions or other error
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.failed_cloud_new_connection));
                                    deleteConnection(OpenMode.GDRIVE);
                                    return false;
                                }
                                break;
                            case 3:
                                // DROPBOX
                                try {

                                    CloudEntry cloudEntryDropbox = null;
                                    CloudEntry savedCloudEntryDropbox;

                                    CloudStorage cloudStorageDropbox = new Dropbox(getApplicationContext(),
                                            data.getString(1), data.getString(2));

                                    if ((savedCloudEntryDropbox = cloudHandler.findEntry(OpenMode.DROPBOX)) != null) {
                                        // we already have the entry and saved state, get it

                                        try {
                                            cloudStorageDropbox.loadAsString(savedCloudEntryDropbox.getPersistData());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            // we need to persist data again

                                            cloudStorageDropbox.login();
                                            cloudEntryDropbox = new CloudEntry(OpenMode.DROPBOX, cloudStorageDropbox.saveAsString());
                                            cloudHandler.updateEntry(OpenMode.DROPBOX, cloudEntryDropbox);
                                        }

                                    } else {

                                        cloudStorageDropbox.login();
                                        cloudEntryDropbox = new CloudEntry(OpenMode.DROPBOX, cloudStorageDropbox.saveAsString());
                                        cloudHandler.addEntry(cloudEntryDropbox);
                                    }

                                    dataUtils.addAccount(cloudStorageDropbox);
                                } catch (CloudPluginException e) {
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.cloud_error_plugin));
                                    deleteConnection(OpenMode.DROPBOX);
                                    return false;
                                } catch (AuthenticationException e) {
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.cloud_fail_authenticate));
                                    deleteConnection(OpenMode.DROPBOX);
                                    return false;
                                } catch (Exception e) {
                                    // any other exception due to network conditions or other error
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.failed_cloud_new_connection));
                                    deleteConnection(OpenMode.DROPBOX);
                                    return false;
                                }
                                break;
                            case 4:
                                // BOX
                                try {

                                    CloudEntry cloudEntryBox = null;
                                    CloudEntry savedCloudEntryBox;

                                    CloudStorage cloudStorageBox = new Box(getApplicationContext(),
                                            data.getString(1), data.getString(2));

                                    if ((savedCloudEntryBox = cloudHandler.findEntry(OpenMode.BOX)) != null) {
                                        // we already have the entry and saved state, get it

                                        try {
                                            cloudStorageBox.loadAsString(savedCloudEntryBox.getPersistData());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            // we need to persist data again

                                            cloudStorageBox.login();
                                            cloudEntryBox = new CloudEntry(OpenMode.BOX, cloudStorageBox.saveAsString());
                                            cloudHandler.updateEntry(OpenMode.BOX, cloudEntryBox);
                                        }

                                    } else {

                                        cloudStorageBox.login();
                                        cloudEntryBox = new CloudEntry(OpenMode.BOX, cloudStorageBox.saveAsString());
                                        cloudHandler.addEntry(cloudEntryBox);
                                    }

                                    dataUtils.addAccount(cloudStorageBox);
                                } catch (CloudPluginException e) {

                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.cloud_error_plugin));
                                    deleteConnection(OpenMode.BOX);
                                    return false;
                                } catch (AuthenticationException e) {
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.cloud_fail_authenticate));
                                    deleteConnection(OpenMode.BOX);
                                    return false;
                                } catch (Exception e) {
                                    // any other exception due to network conditions or other error
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.failed_cloud_new_connection));
                                    deleteConnection(OpenMode.BOX);
                                    return false;
                                }
                                break;
                            case 5:
                                // ONEDRIVE
                                try {

                                    CloudEntry cloudEntryOnedrive = null;
                                    CloudEntry savedCloudEntryOnedrive;

                                    CloudStorage cloudStorageOnedrive = new OneDrive(getApplicationContext(),
                                            data.getString(1), data.getString(2));

                                    if ((savedCloudEntryOnedrive = cloudHandler.findEntry(OpenMode.ONEDRIVE)) != null) {
                                        // we already have the entry and saved state, get it

                                        try {
                                            cloudStorageOnedrive.loadAsString(savedCloudEntryOnedrive.getPersistData());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            // we need to persist data again

                                            cloudStorageOnedrive.login();
                                            cloudEntryOnedrive = new CloudEntry(OpenMode.ONEDRIVE, cloudStorageOnedrive.saveAsString());
                                            cloudHandler.updateEntry(OpenMode.ONEDRIVE, cloudEntryOnedrive);
                                        }

                                    } else {

                                        cloudStorageOnedrive.login();
                                        cloudEntryOnedrive = new CloudEntry(OpenMode.ONEDRIVE, cloudStorageOnedrive.saveAsString());
                                        cloudHandler.addEntry(cloudEntryOnedrive);
                                    }

                                    dataUtils.addAccount(cloudStorageOnedrive);
                                } catch (CloudPluginException e) {

                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.cloud_error_plugin));
                                    deleteConnection(OpenMode.ONEDRIVE);
                                    return false;
                                } catch (AuthenticationException e) {
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.cloud_fail_authenticate));
                                    deleteConnection(OpenMode.ONEDRIVE);
                                    return false;
                                } catch (Exception e) {
                                    // any other exception due to network conditions or other error
                                    e.printStackTrace();
                                    AppConfig.toast(MainActivity.this, getResources().getString(R.string.failed_cloud_new_connection));
                                    deleteConnection(OpenMode.ONEDRIVE);
                                    return false;
                                }
                                break;
                            default:
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.cloud_error_failed_restart),
                                        Toast.LENGTH_LONG).show();
                                return false;
                        }
                    } while (data.moveToNext());
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean refreshDrawer) {
                super.onPostExecute(refreshDrawer);
                if (refreshDrawer) {
                    refreshDrawer();
                }
            }
        }.execute();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}