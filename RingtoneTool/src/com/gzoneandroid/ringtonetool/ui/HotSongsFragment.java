package com.gzoneandroid.ringtonetool.ui;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.ChartInfo;
import com.cmsc.cmmusic.common.data.ChartListRsp;
import com.gzoneandroid.ringtonetool.R;

public class HotSongsFragment extends ListFragment {
    private AlertDialog mDialog;
    private ChartListRsp mChartListRsp;
    protected LayoutInflater mInflater;
    AsyncTask<Void, Void, ChartListRsp> mInitTask;
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
        super.onActivityCreated(savedInstanceState);
        Toast.makeText(getActivity().getApplicationContext(), "HotSongsFragment", Toast.LENGTH_LONG).show();
        mInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mDialog = LightProgressDialog.create(getActivity(),
                R.string.loading_list);
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDialog.show();
        
        mInitTask = new AsyncTask<Void, Void, ChartListRsp>() {
            @Override
            protected ChartListRsp doInBackground(Void... params) {
                mChartListRsp = MusicQueryInterface.getChartInfo(getActivity().getApplicationContext(), 1, 10);
                return mChartListRsp;
            }
            @Override
            protected void onPostExecute(ChartListRsp chartListRsp) {
                // TODO Auto-generated method stub
                if (chartListRsp != null) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            (chartListRsp.getResMsg() != null ? chartListRsp.getResMsg().toString() : "msg is null"),
                            Toast.LENGTH_LONG).show();
                }
                mDialog.dismiss();
                List<ChartInfo> infos = null;
                if (mChartListRsp != null ) {
                    infos = mChartListRsp.getChartInfos();
                }
                if (infos != null && infos.size() > 0) {
                    for (ChartInfo chartInfo : infos) {
                        Log.d("Debug", chartInfo.getChartName() + ", " + chartInfo.getChartCode());
                    }
                    setListAdapter(new ChartListAdapter());
                } else {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            (chartListRsp != null && chartListRsp.getResMsg() != null ? chartListRsp
                                    .getResMsg().toString()
                                    : ""),
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        mInitTask.execute(null, null, null);

    }
    
    class ChartListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mChartListRsp.getChartInfos().size();
        }

        @Override
        public String[] getItem(int position) {
            String[] content = new String[2];
            content[0] = mChartListRsp.getChartInfos().get(position).getChartName();
            content[1] = mChartListRsp.getChartInfos().get(position).getChartCode();
            return content;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView != null) {
                view = convertView;
            }else {
                view = mInflater.inflate(R.layout.hotsongs_list_item, parent, false);
                TextView tv = (TextView) view.findViewById(R.id.title);
                tv.setText(mChartListRsp.getChartInfos().get(position).getChartName());
                tv.setTag(mChartListRsp.getChartInfos().get(position).getChartCode());
            }
            return view;
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
        mInitTask.cancel(true);
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
        Intent intent = new Intent(getActivity().getApplicationContext(), MusicList.class);
        String[] content = (String[]) l.getItemAtPosition(position);
        intent.putExtra("title", content[0]);
        intent.putExtra("code", content[1]);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
    }

}
