package com.gpsgetwoweducation.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.gpsgetwoweducation.R;


public class ProgressDialogHUD extends Dialog {
    public ProgressDialogHUD(Context context) {
        super(context);
    }

    public ProgressDialogHUD(Context context, int theme) {
        super(context, theme);
    }


    public void onWindowFocusChanged(boolean hasFocus) {
//        ImageView imageView = findViewById(R.id.spinnerImageView);
//        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
//        spinner.start();
    }

    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(androidx.appcompat.R.id.message).setVisibility(View.VISIBLE);
            androidx.appcompat.widget.AppCompatTextView txt = findViewById(androidx.appcompat.R.id.message);
            txt.setText(message);
            txt.invalidate();
        }
    }

    public static ProgressDialogHUD show(Context context, CharSequence message, boolean indeterminate, boolean cancelable,
                                         OnCancelListener cancelListener) {
        ProgressDialogHUD dialog = new ProgressDialogHUD(context, R.style.ProgressHUD);
        dialog.setTitle("");
        dialog.setContentView(R.layout.progress_hud);
        /*if (message == null || message.length() == 0) {
            dialog.findViewById(R.id.message).setVisibility(View.GONE);
        } else {
            androidx.appcompat.widget.AppCompatTextView txt = dialog.findViewById(R.id.message);
            txt.setText(message);
        }*/
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.show();
        return dialog;
    }
}
