package com.gzoneandroid.ringtonetool;

import java.util.Hashtable;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.cmsc.cmmusic.common.InitCmmInterface;

public class RingtoneToolApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new T1().start();
    }
    private UIHandler mUIHandler = new UIHandler();
    private ProgressDialog mProgress = null;

    void showProgressBar(final String msg) {

        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgress == null) {
                    mProgress = new ProgressDialog(getApplicationContext());
                    mProgress.setMessage(msg);
                    mProgress.setIndeterminate(false);
                    mProgress.setCancelable(false);
                    mProgress.show();
                }
            }
        });
    }
    
    void hideProgressBar() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgress != null) {
                    mProgress.dismiss();
                    mProgress = null;
                }
            }
        });
    }
    
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
            case 0:
                if (msg.obj == null) {
                    hideProgressBar();
                    Toast.makeText(getApplicationContext(), R.string.result_equals_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(getApplicationContext()).setTitle(R.string.result)
                        .setMessage((msg.obj).toString()).setPositiveButton(R.string.confirm, null).show();
                break;
            }
            hideProgressBar();
            if (null != dialog) {
                dialog.dismiss();
            }

        }
    }
    private ProgressDialog dialog;
    class T1 extends Thread {
        @Override
        public void run() {
            super.run();
            Looper.prepare();
            if (!InitCmmInterface.initCheck(getApplicationContext())) {
                dialog = ProgressDialog.show(getApplicationContext(), null, getString(R.string.please_wait), true, false);
                Hashtable<String, String> b = InitCmmInterface.initCmmEnv(getApplicationContext());
                Message m = new Message();
                m.what = 0;
                m.obj = b;
                mUIHandler.sendMessage(m);
            } else {
                if (null != dialog) {
                    dialog.dismiss();
                }

                Toast.makeText(getApplicationContext(), "已初始化过",
                        Toast.LENGTH_LONG).show();
            }
            Looper.loop();
        }
    }
}
