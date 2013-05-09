
package com.gzoneandroid.ringtonetool.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.FullSongManagerInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.DownloadResult;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicListRsp;
import com.gzoneandroid.ringtonetool.R;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

/**
 * This example shows a expandable listview with a more button per list item
 * which expands the expandable area. In the expandable area there are two
 * buttons A and B which can be click. The events for these buttons are handled
 * here in this Activity.
 * 
 * @author tjerk
 * @date 6/13/12 7:33 AM
 */
public class MusicList extends Activity {
    private AlertDialog mDialog;
    private MusicListRsp mMusicListRsp;
    private String mCode;
    private String mTitle;
    ActionSlideExpandableListView mList;
    public LayoutInflater mInflater;
    AsyncTask<Void, Void, MusicListRsp> mInitTask;
    private String mMusicId;

    @Override
    public void onCreate(Bundle savedData) {

        super.onCreate(savedData);
        // set the content view for this activity, check the content view xml
        // file
        // to see how it refers to the ActionSlideExpandableListView view.
        this.setContentView(R.layout.single_expandable_list);
        // get a reference to the listview, needed in order
        // to call setItemActionListener on it
        mList = (ActionSlideExpandableListView) this
                .findViewById(R.id.list);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mTitle = getIntent().getStringExtra("title");
        mCode = getIntent().getStringExtra("code");
        Toast.makeText(getApplicationContext(), "SongsList: " + mTitle + ", " + mCode,
                Toast.LENGTH_LONG).show();
        mDialog = LightProgressDialog.create(this,
                R.string.loading_list);
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDialog.show();

        mInitTask = new AsyncTask<Void, Void, MusicListRsp>() {
            @Override
            protected MusicListRsp doInBackground(Void... params) {
                mMusicListRsp = MusicQueryInterface
                        .getMusicsByChartId(
                                getApplicationContext(), mCode, 1,
                                20);
                return mMusicListRsp;
            }

            @Override
            protected void onPostExecute(MusicListRsp musicListRsp) {
                // TODO Auto-generated method stub
                if (musicListRsp == null) {
                    Toast.makeText(getApplicationContext(),
                            musicListRsp.getResMsg().toString(),
                            Toast.LENGTH_LONG).show();
                }
                mDialog.dismiss();

                List<MusicInfo> musics = musicListRsp.getMusics();
                if (musics != null && musics.size() > 0) {
                    for (MusicInfo musicsInfo : musics) {
                        Log.d("Debug", musicsInfo.getSongName() + ", " + musicsInfo.getMusicId());
                    }
                    mList.setAdapter(new MusicsListAdapter());
                } else {
                    Toast.makeText(getApplicationContext(),
                            musicListRsp.getResMsg().toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        mInitTask.execute(null, null, null);

        // fill the list with data
        // list.setAdapter(buildDummyData());

        // listen for events in the two buttons for every list item.
        // the 'position' var will tell which list item is clicked
        mList.setItemActionListener(new ActionSlideExpandableListView.OnActionClickListener() {

            @Override
            public void onClick(View listView, View buttonview, int position) {

                /**
                 * Normally you would put a switch statement here, and depending
                 * on view.getId() you would perform a different action.
                 */
                String actionName = "";
                mMusicId = ((String[]) (mList.getItemAtPosition(position)))[1];
                if (buttonview.getId() == R.id.buttonA) {
                    actionName = getString(R.string.download);
                    
                    showParameterDialog(mMusicId, new ParameterCallback() {

                        @Override
                        public void callback(final String musicId) {
                            Log.i("TAG", "musicId = " + musicId);
                            FullSongManagerInterface.getFullSongDownloadUrlByNet(MusicList.this,
                                    musicId, false,
                                    new CMMusicCallback<DownloadResult>() {
                                        @Override
                                        public void operationResult(final DownloadResult downloadResult) {
                                            if (null != downloadResult) {
                                                new AlertDialog.Builder(MusicList.this)
                                                        .setTitle("getFullSongDownloadUrlByNet")
                                                        .setMessage(downloadResult.toString())
                                                        .setPositiveButton(R.string.confirm, null)
                                                        .show();
                                            }

                                            Log.d("Debug", "FullSong Download result is "
                                                    + downloadResult);
                                        }
                                    });
                        }
                    });
                    
                } else if (buttonview.getId() == R.id.buttonB){
                    actionName = getString(R.string.set_as_ringtone);
                }
                /**
                 * For testing sake we just show a toast
                 */
                Toast.makeText(
                        MusicList.this,
                        "Clicked Action: " + actionName + " in list item " + position,
                        Toast.LENGTH_SHORT
                        ).show();
            }

            // note that we also add 1 or more ids to the setItemActionListener
            // this is needed in order for the listview to discover the buttons
        }, R.id.buttonA, R.id.buttonB);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mInitTask.cancel(true);
    }
    /**
     * Builds dummy data for the test. In a real app this would be an adapter
     * for your data. For example a CursorAdapter
     */
    public ListAdapter buildDummyData() {
        final int SIZE = 20;
        String[] values = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            values[i] = "Item " + i;
        }
        return new ArrayAdapter<String>(
                this,
                R.layout.expandable_list_item,
                R.id.text,
                values);
    }

    class MusicsListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mMusicListRsp.getMusics().size();
        }

        @Override
        public String[] getItem(int position) {
            String[] content = new String[2];
            content[0] = mMusicListRsp.getMusics().get(position).getSongName();
            content[1] = mMusicListRsp.getMusics().get(position).getMusicId();
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
            } else {
                view = mInflater.inflate(R.layout.expandable_list_item, parent, false);
                TextView tv = (TextView) view.findViewById(R.id.text);
                tv.setText(mMusicListRsp.getMusics().get(position).getSongName());
                tv.setTag(mMusicListRsp.getMusics().get(position).getMusicId());
            }
            return view;
        }
    }
    
    void showParameterDialog(String musicId, final ParameterCallback callback) {
        View view = View.inflate(getApplicationContext(), R.layout.parameter_dialog, null);
        final EditText edt = (EditText) view.findViewById(R.id.editText1);
//        new AlertDialog.Builder(MusicList.this)
//                .setTitle(title)
//                .setView(view)
//                .setMessage("请输入参数:" + title)
//                .setNegativeButton("取消", null)
//                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String parameter = edt.getText().toString();
                        if (callback != null) {
                            callback.callback(musicId);
                        }
//                    }
//                }).show();
    }
    
    interface ParameterCallback {
        void callback(String parameter);
    }
}
