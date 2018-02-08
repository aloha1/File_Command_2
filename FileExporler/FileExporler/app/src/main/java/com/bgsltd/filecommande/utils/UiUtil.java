package com.bgsltd.filecommande.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;

import com.bgsltd.filecommande.R;

/**
 * Created by marinaracu on 22.11.2017.
 */

public class UiUtil {

    public static void showVoteDialoge(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_done_vote);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private static void hide(final Dialog dialog) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //  cancel();
                dialog.dismiss();
            }
        }, 3000);
    }
}
