package com.gzoneandroid.ringtonetool.ui;

import java.util.Hashtable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cmsc.cmmusic.init.InitCmmInterface;
import com.gzoneandroid.ringtonetool.R;

public class HomePage extends Activity {
    private AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Debug", "HomePage's onCreate");
        mDialog = LightProgressDialog.create(this,
                "authenticating");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDialog.show();

        AsyncTask<Void, Void, Hashtable<String, String>> initTask = new AsyncTask<Void, Void, Hashtable<String, String>>() {
            @Override
            protected Hashtable<String, String> doInBackground(Void... params) {
                if (!InitCmmInterface.initCheck(getApplicationContext())) {
                    Hashtable<String, String> b = InitCmmInterface.initCmmEnv(getApplicationContext());
                    return b;
                } else {
                    return null;
                }
                
            }
            @Override
            protected void onPostExecute(Hashtable<String, String> hashtable) {
                // TODO Auto-generated method stub
                if (hashtable == null) {
                    Toast.makeText(getApplicationContext(), R.string.has_been_inited,
                            Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), hashtable.toString(),
                            Toast.LENGTH_LONG).show();
                }
                mDialog.dismiss();
                startActivity(new Intent(getApplicationContext(), SampleTabsDefault.class));
                finish();
            }
        };
        initTask.execute(null, null, null);
    }
}
