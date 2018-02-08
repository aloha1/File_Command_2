package com.bgsltd.filecommande.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bgsltd.filecommande.R;
import com.google.android.gms.ads.reward.RewardedVideoAd;

/**
 * Created by marinaracu on 22.11.2017.
 */

public class ShowAdsDialog extends Dialog implements
        android.view.View.OnClickListener{

    public Activity activity;
    public Dialog dialog;
    public TextView yes;
    private ImageView no;
    private RewardedVideoAd mRewardedVideoAd;
    private OnItemShowAdsRewardListener mOnItemShowAdsRewardListener;

    public void setOnItemRewardAdsListener(OnItemShowAdsRewardListener onItemShowAdsRewardListener) {
        this.mOnItemShowAdsRewardListener = onItemShowAdsRewardListener;
    }

    public interface OnItemShowAdsRewardListener {

        void showRewardAds();
    }

    public ShowAdsDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_done_vote);
        yes = (TextView) findViewById(R.id.txt_watch_ads);
        no = (ImageView) findViewById(R.id.img_close_ads);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        setCancelable(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_watch_ads:
                if (mOnItemShowAdsRewardListener != null) {
                    mOnItemShowAdsRewardListener.showRewardAds();
                }
                dismiss();
                break;
            case R.id.img_close_ads:
                dismiss();
                break;
            default:
                break;
        }
    }
}