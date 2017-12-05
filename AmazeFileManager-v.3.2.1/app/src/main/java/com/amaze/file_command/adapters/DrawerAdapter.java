/*
 * Copyright (C) 2014 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>
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

package com.amaze.file_command.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bgsltd.file_command.R;
import com.amaze.file_command.activities.GoPremiumActivity;
import com.amaze.file_command.activities.MainActivity;
import com.amaze.file_command.activities.PreferencesActivity;
import com.amaze.file_command.activities.ThemedActivity;
import com.amaze.file_command.database.CloudHandler;
import com.amaze.file_command.filesystem.HFile;
import com.amaze.file_command.filesystem.Operations;
import com.amaze.file_command.filesystem.RootHelper;
import com.amaze.file_command.fragments.FTPServerFragment;
import com.amaze.file_command.fragments.HomeFragment;
import com.amaze.file_command.fragments.MusicFragment;
import com.amaze.file_command.fragments.PhotoFragment;
import com.amaze.file_command.fragments.VideoFragment;
import com.amaze.file_command.ui.dialogs.GeneralDialogCreation;
import com.amaze.file_command.ui.drawer.EntryItem;
import com.amaze.file_command.ui.drawer.Item;
import com.amaze.file_command.utils.DataUtils;
import com.amaze.file_command.utils.OpenMode;
import com.amaze.file_command.utils.Utils;
import com.amaze.file_command.utils.cloud.CloudUtil;
import com.amaze.file_command.utils.color.ColorUsage;
import com.amaze.file_command.utils.provider.UtilitiesProviderInterface;
import com.amaze.file_command.utils.theme.AppTheme;

import java.io.File;
import java.util.ArrayList;

public class DrawerAdapter extends ArrayAdapter<Item> {
    private String TAG = "DrawerAdapter";
    private final Context context;
    private UtilitiesProviderInterface utilsProvider;
    private final ArrayList<Item> values;
    private MainActivity m;
    private SparseBooleanArray myChecked = new SparseBooleanArray();
    private DataUtils dataUtils = DataUtils.getInstance();

    public void toggleChecked(int position) {
        toggleChecked(false);
        myChecked.put(position, true);
        notifyDataSetChanged();
    }

    public void toggleChecked(boolean b) {
        for (int i = 0; i < values.size(); i++) {
            myChecked.put(i, b);
        }
        notifyDataSetChanged();
    }

    private LayoutInflater inflater;

    public DrawerAdapter(Context context, UtilitiesProviderInterface utilsProvider,
                         ArrayList<Item> values, MainActivity m, SharedPreferences Sp) {
        super(context, R.layout.drawerrow, values);
        this.utilsProvider = utilsProvider;

        this.context = context;
        this.values = values;

        for (int i = 0; i < values.size(); i++) {
            myChecked.put(i, false);
        }
        this.m = m;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (values.get(position).isSection()) {
            Log.d(TAG, "Section position: "+ position);
            ImageView view = new ImageView(context);
            if (utilsProvider.getAppTheme().equals(AppTheme.LIGHT))
                view.setImageResource(R.color.divider);
            else
                view.setImageResource(R.color.divider_dark);
            view.setClickable(false);
            view.setFocusable(false);
            if (utilsProvider.getAppTheme().equals(AppTheme.LIGHT))
                view.setBackgroundColor(Color.WHITE);
            else view.setBackgroundResource(R.color.background_material_dark);
            view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dpToPx(m, 17)));
            view.setPadding(0, Utils.dpToPx(m, 8), 0, Utils.dpToPx(m, 8));
            return view;
        } else {
            View view = inflater.inflate(R.layout.drawerrow, parent, false);
            final TextView txtTitle = (TextView) view.findViewById(R.id.firstline);
            final ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            if (utilsProvider.getAppTheme().equals(AppTheme.LIGHT)) {
                view.setBackgroundResource(R.drawable.safr_ripple_white);
            } else {
                view.setBackgroundResource(R.drawable.safr_ripple_black);
            }
            view.setOnClickListener(new View.OnClickListener() {

                public void onClick(View p1) {

                    EntryItem item = (EntryItem) getItem(position);
                    if(item.getPath().contains("premium")){
                        Intent intent = new Intent(context, GoPremiumActivity.class);
                        context.startActivity(intent);
                    }else if(item.getPath().contains("home")){
                        ((MainActivity)context).onBackPressed();
                        ((MainActivity)context).addFragment(new HomeFragment());
                    }else if(item.getPath().contains("ftp")){
                        ((MainActivity)context).onBackPressed();
                        ((MainActivity)context).addFragment(new FTPServerFragment());
                    }else if(item.getPath().contains("picture")){
                        ((MainActivity)context).onBackPressed();
                        ((MainActivity)context).addFragment(new PhotoFragment());
                    }else if(item.getPath().contains("music")){
                        ((MainActivity)context).onBackPressed();
                        ((MainActivity)context).addFragment(new MusicFragment());
                    } else if(item.getPath().contains("video")){
                        ((MainActivity)context).onBackPressed();
                        ((MainActivity)context).addFragment(new VideoFragment());
                    }else if(item.getPath().contains("setting")){
                        Intent in = new Intent(context, PreferencesActivity.class);
                        context.startActivity(in);
                    }else {
                        if (dataUtils.containsBooks(new String[]{item.getTitle(), item.getPath()}) != -1) {

                            checkForPath(item.getPath());
                        }

                        if (dataUtils.getAccounts().size() > 0 && (item.getPath().startsWith(CloudHandler.CLOUD_PREFIX_BOX) ||
                                item.getPath().startsWith(CloudHandler.CLOUD_PREFIX_DROPBOX) ||
                                item.getPath().startsWith(CloudHandler.CLOUD_PREFIX_ONE_DRIVE) ||
                                item.getPath().startsWith(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE))) {
                            // we have cloud accounts, try see if token is expired or not
                            CloudUtil.checkToken(item.getPath(), m);
                        }
                        m.selectItem(position);
                    }
                }
                // TODO: Implement this method
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!getItem(position).isSection())
                        // not to remove the first bookmark (storage) and permanent bookmarks
                        if (position > m.storage_count && position < values.size() - 7) {
                            EntryItem item = (EntryItem) getItem(position);
                            String title = item.getTitle();
                            String path = (item).getPath();
                            if (dataUtils.containsBooks(new String[]{item.getTitle(), path}) != -1) {
                                m.renameBookmark((item).getTitle(), path);
                            } else if (path.startsWith("smb:/")) {
                                m.showSMBDialog(item.getTitle(), path, true);
                            } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_DROPBOX)) {

                                GeneralDialogCreation.showCloudDialog(m, utilsProvider.getAppTheme(), OpenMode.DROPBOX);

                            } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE)) {

                                GeneralDialogCreation.showCloudDialog(m, utilsProvider.getAppTheme(), OpenMode.GDRIVE);

                            } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_BOX)) {

                                GeneralDialogCreation.showCloudDialog(m, utilsProvider.getAppTheme(), OpenMode.BOX);

                            } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_ONE_DRIVE)) {

                                GeneralDialogCreation.showCloudDialog(m, utilsProvider.getAppTheme(), OpenMode.ONEDRIVE);
                            }
                        } else if (position < m.storage_count) {
                            String path = ((EntryItem) getItem(position)).getPath();
                            if (!path.equals("/"))
                                GeneralDialogCreation.showPropertiesDialogForStorage(RootHelper.generateBaseFile(new File(path), true), m, utilsProvider.getAppTheme());
                        }

                    // return true to denote no further processing
                    return true;
                }
            });

            txtTitle.setText(((EntryItem) (values.get(position))).getTitle());
            imageView.setImageDrawable(getDrawable(position));
           // imageView.clearColorFilter();

            if (myChecked.get(position)) {
                int accentColor = m.getColorPreference().getColor(ColorUsage.ACCENT);
                if (utilsProvider.getAppTheme().equals(AppTheme.LIGHT)) {
                    //view.setBackgroundColor(Color.parseColor("#ffeeeeee"));
                } else {
                    //view.setBackgroundColor(Color.parseColor("#ff424242"));
                }
                imageView.setColorFilter(accentColor);
                txtTitle.setTextColor(accentColor);
            } else {
                if (utilsProvider.getAppTheme().equals(AppTheme.LIGHT)) {
                    //imageView.setColorFilter(Color.parseColor("#666666"));
                    txtTitle.setTextColor(Utils.getColor(m, android.R.color.black));
                } else {
                    //imageView.setColorFilter(Color.WHITE);
                    txtTitle.setTextColor(Utils.getColor(m, android.R.color.white));
                }
            }

            return view;
        }
    }

    /**
     * Checks whether path for bookmark exists
     * If path is not found, empty directory is created
     *
     * @param path
     */
    private void checkForPath(String path) {
        // TODO: Add support for SMB and OTG in this function
        if (!new File(path).exists()) {
            Toast.makeText(getContext(), getContext().getString(R.string.bookmark_lost), Toast.LENGTH_SHORT).show();
            Operations.mkdir(RootHelper.generateBaseFile(new File(path), true), getContext(),
                    ThemedActivity.rootMode, new Operations.ErrorCallBack() {
                        //TODO empty
                        @Override
                        public void exists(HFile file) {

                        }

                        @Override
                        public void launchSAF(HFile file) {

                        }

                        @Override
                        public void launchSAF(HFile file, HFile file1) {

                        }

                        @Override
                        public void done(HFile hFile, boolean b) {

                        }

                        @Override
                        public void invalidName(HFile file) {

                        }
                    });
        }

    }

    private Drawable getDrawable(int position) {
        return ((EntryItem) getItem(position)).getIcon();
    }
}