package com.bgsltd.file_command.fragments.preference_fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bgsltd.file_command.R;
import com.bgsltd.file_command.activities.PreferencesActivity;
import com.bgsltd.file_command.ui.views.preference.CheckBox;
import com.bgsltd.file_command.utils.Utils;
import com.bgsltd.file_command.utils.color.ColorPreference;
import com.bgsltd.file_command.utils.color.ColorUsage;

import java.util.List;

/**
 * Created by Arpit on 21-06-2015.
 */
public class ColorPref extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private MaterialDialog dialog;

    SharedPreferences sharedPref;
    PreferencesActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.color_prefs);

        activity = (PreferencesActivity) getActivity();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        final CheckBox checkBoxPreference = (CheckBox) findPreference("random_checkbox");
        checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (activity != null) activity.setChanged();
                Toast.makeText(getActivity(), R.string.setRandom, Toast.LENGTH_LONG).show();
                return true;
            }
        });
        CheckBox preference8 = (CheckBox) findPreference("colorednavigation");
        preference8.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (activity != null) activity.setChanged();
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= 21)
            preference8.setEnabled(true);

        findPreference(ColorUsage.PRIMARY.asString()).setOnPreferenceClickListener(this);
        findPreference(ColorUsage.PRIMARY_TWO.asString()).setOnPreferenceClickListener(this);
        findPreference(ColorUsage.ACCENT.asString()).setOnPreferenceClickListener(this);
        findPreference(ColorUsage.ICON_SKIN.asString()).setOnPreferenceClickListener(this);
    }

    @Override
    public void onPause() {
        if (dialog != null){
            dialog.dismiss();
        }
        super.onPause();
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        if (activity != null) activity.setChanged();

        final ColorUsage usage = ColorUsage.fromString(preference.getKey());
        if (usage != null) {
            ColorAdapter adapter = new ColorAdapter(getActivity(), ColorPreference.availableColors, usage);

            GridView v = (GridView) getActivity().getLayoutInflater().inflate(R.layout.dialog_grid, null);
            v.setAdapter(adapter);
            v.setOnItemClickListener(adapter);

            int fab_skin = activity.getColorPreference().getColor(ColorUsage.ACCENT);
            dialog = new MaterialDialog.Builder(getActivity())
                    .positiveText(R.string.cancel)
                    .title(R.string.choose_color)
                    .theme(activity.getAppTheme().getMaterialDialogTheme())
                    .autoDismiss(true)
                    .positiveColor(fab_skin)
                    .neutralColor(fab_skin)
                    .neutralText(R.string.defualt)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onNeutral(MaterialDialog dialog) {
                            super.onNeutral(dialog);
                            activity.getColorPreference()
                                    .setRes(usage, usage.getDefaultColor())
                                    .saveToPreferences(sharedPref);
                        }
                    })
                    .customView(v, false)
                    .build();

            adapter.setDialog(dialog);
            dialog.show();
        }

        return false;
    }

    private class ColorAdapter extends ArrayAdapter<Integer> implements AdapterView.OnItemClickListener {
        private String prefKey;
        private ColorUsage usage;
        @ColorInt
        private int selectedColor;
        private MaterialDialog dialog;

        public void setDialog(MaterialDialog b) {
            this.dialog = b;
        }

        /**
         * Constructor for adapter that handles the view creation of color chooser dialog in preferences
         *
         * @param context the context
         * @param colors  array list of color hex values in form of string; for the views
         * @param usage   the preference usage for setting new selected color preference value
         */
        ColorAdapter(Context context, List<Integer> colors, ColorUsage usage) {
            super(context, R.layout.rowlayout, colors);
            this.prefKey = usage.asString();
            this.usage = usage;
            this.selectedColor = activity.getColorPreference().getColor(usage);
        }

        @ColorInt
        private int getColor(@ColorRes int colorRes) {
            return Utils.getColor(getContext(), colorRes);
        }

        @ColorRes
        private int getColorResAt(int position) {
            Integer item = getItem(position);

            if (item == null) {
                return usage.getDefaultColor();
            } else {
                return item;
            }
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //TODO solve unconditional layout inflation
            View rowView = inflater.inflate(R.layout.dialog_grid_item, parent, false);

            int color = getColor(getColorResAt(position));

            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            if (color == selectedColor)
                imageView.setImageResource(R.drawable.ic_checkmark_selected);
            GradientDrawable gradientDrawable = (GradientDrawable) imageView.getBackground();

            gradientDrawable.setColor(color);

            return rowView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int selectedColorRes = getColorResAt(position);

            activity.getColorPreference().setRes(usage, selectedColorRes).saveToPreferences(sharedPref);

            if (dialog != null) dialog.dismiss();
        }
    }
}
