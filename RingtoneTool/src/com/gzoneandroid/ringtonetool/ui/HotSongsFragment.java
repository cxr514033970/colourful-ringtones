package com.gzoneandroid.ringtonetool.ui;

import java.util.Hashtable;
import java.util.List;

import com.cmsc.cmmusic.common.InitCmmInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.ChartInfo;
import com.cmsc.cmmusic.common.data.ChartListRsp;
import com.gzoneandroid.ringtonetool.CMMusicDemo;
import com.gzoneandroid.ringtonetool.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HotSongsFragment extends ListFragment {
    private AlertDialog mDialog;
    private ChartListRsp mChartListRsp;
    
    public static HotSongsFragment newInstance() {
        HotSongsFragment fragment = new HotSongsFragment();

        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity().getApplicationContext(), "HotSongsFragment", Toast.LENGTH_LONG).show();
        super.onActivityCreated(savedInstanceState);
        mDialog = LightProgressDialog.create(getActivity(),
                "authenticating");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDialog.show();
        
        AsyncTask<Void, Void, ChartListRsp> initTask = new AsyncTask<Void, Void, ChartListRsp>() {
            @Override
            protected ChartListRsp doInBackground(Void... params) {
                mChartListRsp = MusicQueryInterface.getChartInfo(getActivity().getApplicationContext(), 1, 10);
                return mChartListRsp;
            }
            @Override
            protected void onPostExecute(ChartListRsp chartListRsp) {
                // TODO Auto-generated method stub
                if (chartListRsp == null) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            chartListRsp.getResMsg().toString(),
                            Toast.LENGTH_LONG).show();
                }
                mDialog.dismiss();
                setListAdapter(new ChartListAdapter());
                List<ChartInfo> infos = mChartListRsp.getChartInfos();
                for (ChartInfo chartInfo : infos) {
                    Log.d("Debug", chartInfo.getChartName() + ", " + chartInfo.getChartCode());
                }
            }
        };
        initTask.execute(null, null, null);

    }
    class ChartListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
    }

}
