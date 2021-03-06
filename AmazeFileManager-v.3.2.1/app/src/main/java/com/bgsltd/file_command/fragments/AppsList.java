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

package com.bgsltd.file_command.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.bgsltd.file_command.R;
import com.bgsltd.file_command.activities.ThemedActivity;
import com.bgsltd.file_command.activities.MainActivity;
import com.bgsltd.file_command.adapters.AppsAdapter;
import com.bgsltd.file_command.services.asynctasks.AppListLoader;
import com.bgsltd.file_command.ui.LayoutElement;
import com.bgsltd.file_command.ui.icons.IconHolder;
import com.bgsltd.file_command.utils.Utils;
import com.bgsltd.file_command.utils.provider.UtilitiesProviderInterface;
import com.bgsltd.file_command.utils.theme.AppTheme;

import java.util.List;

public class AppsList extends ListFragment implements LoaderManager.LoaderCallbacks<List<LayoutElement>> {

    UtilitiesProviderInterface utilsProvider;
    AppsList app = this;
    AppsAdapter adapter;

    public SharedPreferences Sp;
    ListView vl;
    public IconHolder ic;
    int asc, sortby;

    int index = 0, top = 0;

    public static final int ID_LOADER_APP_LIST = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilsProvider = (UtilitiesProviderInterface) getActivity();

        setHasOptionsMenu(false);
        ic = new IconHolder(getActivity(), true, true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getAppbar().setTitle(getResources().getString(R.string.apps));
        mainActivity.floatingActionButton.hideMenuButton(true);
        mainActivity.buttonBarFrame.setVisibility(View.GONE);
        mainActivity.supportInvalidateOptionsMenu();
        vl = getListView();
        Sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getSortModes();
        ListView vl = getListView();
        vl.setDivider(null);
        if (utilsProvider.getAppTheme().equals(AppTheme.DARK))
            getActivity().getWindow().getDecorView().setBackgroundColor(Utils.getColor(getContext(), R.color.holo_dark_background));

        adapter = new AppsAdapter(getContext(), (ThemedActivity) getActivity(), utilsProvider,
                R.layout.rowlayout, app);
        setListAdapter(adapter);
        setListShown(false);
        setEmptyText(getResources().getString(R.string.no_applications));
        getLoaderManager().initLoader(ID_LOADER_APP_LIST, null, this);

        if (savedInstanceState != null) {

            index = savedInstanceState.getInt("index");
            top = savedInstanceState.getInt("top");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        if (vl != null) {
            int index = vl.getFirstVisiblePosition();
            View vi = vl.getChildAt(0);
            int top = (vi == null) ? 0 : vi.getTop();
            b.putInt("index", index);
            b.putInt("top", top);
        }
    }

    public boolean unin(String pkg) {
        try {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + pkg));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Assigns sort modes
     * A value from 0 to 2 defines sort mode as name/last modified/size in ascending order
     * Values from 3 to 5 defines sort mode as name/last modified/size in descending order
     * <p>
     * Final value of {@link #sortby} varies from 0 to 2
     */
    public void getSortModes() {
        int t = Integer.parseInt(Sp.getString("sortbyApps", "0"));
        if (t <= 2) {
            sortby = t;
            asc = 1;
        } else if (t > 2) {
            asc = -1;
            sortby = t - 3;
        }
    }

    @Override
    public Loader<List<LayoutElement>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(getContext(), sortby, asc);
    }

    @Override
    public void onLoadFinished(Loader<List<LayoutElement>> loader, List<LayoutElement> data) {
        // set new data to adapter
        adapter.setData(data);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }

        if (vl != null)
            vl.setSelectionFromTop(index, top);
    }

    @Override
    public void onLoaderReset(Loader<List<LayoutElement>> loader) {
        adapter.setData(null);
    }
}
