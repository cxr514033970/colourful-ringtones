
package com.gzoneandroid.ringtonetool;

import java.net.URLEncoder;
import java.util.Hashtable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.FullSongManagerInterface;
import com.cmsc.cmmusic.common.InitCmmInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.OnlineListenerMusicInterface;
import com.cmsc.cmmusic.common.RingbackManagerInterface;
import com.cmsc.cmmusic.common.UserManagerInterface;
import com.cmsc.cmmusic.common.VibrateRingManagerInterface;
import com.cmsc.cmmusic.common.data.AlbumListRsp;
import com.cmsc.cmmusic.common.data.ChartListRsp;
import com.cmsc.cmmusic.common.data.CrbtListRsp;
import com.cmsc.cmmusic.common.data.CrbtOpenCheckRsp;
import com.cmsc.cmmusic.common.data.CrbtPrelistenRsp;
import com.cmsc.cmmusic.common.data.DownloadResult;
import com.cmsc.cmmusic.common.data.MusicInfoResult;
import com.cmsc.cmmusic.common.data.MusicListRsp;
import com.cmsc.cmmusic.common.data.Result;
import com.cmsc.cmmusic.common.data.SingerInfoRsp;
import com.cmsc.cmmusic.common.data.StreamRsp;
import com.cmsc.cmmusic.common.data.TagListRsp;

public class CMMusicDemo extends Activity implements OnClickListener {
    private final static String LOG_TAG = "CMMusicDemo";

    private ProgressDialog dialog;

    private long requestTime;

    private UIHandler mUIHandler = new UIHandler();

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            long responseTime = System.currentTimeMillis() - requestTime;

            switch (msg.what) {
                case 0:
                    if (msg.obj == null) {
                        hideProgressBar();
                        Toast.makeText(CMMusicDemo.this, R.string.result_equals_null,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new AlertDialog.Builder(CMMusicDemo.this).setTitle(R.string.result)
                            .setMessage((msg.obj).toString())
                            .setPositiveButton(R.string.confirm, null).show();
                    break;
            }
            hideProgressBar();
            if (null != dialog) {
                dialog.dismiss();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmmusic_demo);

        Button initButton = (Button) this.findViewById(R.id.initButton);
        initButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (!InitCmmInterface.initCheck(CMMusicDemo.this)) {
                dialog = ProgressDialog.show(CMMusicDemo.this, null,
                        getString(R.string.please_wait), true, false);
                requestTime = System.currentTimeMillis();
                new Thread(new T1()).start();
                // } else {
                // new
                // AlertDialog.Builder(CMMusicDemo.this).setTitle("init").setMessage("已初始化过")
                // .setPositiveButton("确认", null).show();
                // }
            }
        });
        // 短信验证码登陆 login by sms verification code
        Button login = (Button) this.findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagerInterface.smsAuthLogin(CMMusicDemo.this, new CMMusicCallback<Result>() {
                    @Override
                    public void operationResult(Result result) {
                        if (null != result) {
                            new AlertDialog.Builder(CMMusicDemo.this).setTitle("login")
                                    .setMessage(result.toString())
                                    .setPositiveButton(R.string.confirm, null)
                                    .show();
                        }

                        Log.d(LOG_TAG, "ret is " + result);
                    }
                });
            }
        });
        // 根据手机号查询是否开通彩铃 check if order multimedia ringtone by phone number
        Button crbtOpenCheck = (Button) this.findViewById(R.id.crbtOpenCheck);
        crbtOpenCheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showParameterDialog(getString(R.string.phone_number), new ParameterCallback() {

                    @Override
                    public void callback(final String phoneNum) {
                        Log.i("TAG", "phoneNum = " + phoneNum);
                        showProgressBar(getString(R.string.data_is_loading));
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                CrbtOpenCheckRsp result = RingbackManagerInterface.crbtOpenCheck(
                                        CMMusicDemo.this, phoneNum);

                                mUIHandler.obtainMessage(0, result).sendToTarget();

                            }
                        }.start();
                    }
                });
            }
        });
        // 查询歌手信息 query singer informantion
        Button singerInfo = (Button) this.findViewById(R.id.singerInfo);
        singerInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showParameterDialog(getString(R.string.singer_id), new ParameterCallback() {

                    @Override
                    public void callback(final String singerId) {
                        Log.i("TAG", "singerId = " + singerId);
                        showProgressBar(getString(R.string.data_is_loading));
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                SingerInfoRsp result = MusicQueryInterface.getSingerInfo(
                                        CMMusicDemo.this, singerId);

                                mUIHandler.obtainMessage(0, result).sendToTarget();

                            }
                        }.start();
                    }
                });
            }
        });
        // 歌曲ID查询专辑信息 query album by song id
        Button albumListbymusic = (Button) this.findViewById(R.id.albumListbymusic);
        albumListbymusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showParameterDialog(getString(R.string.song_id), new ParameterCallback() {

                    @Override
                    public void callback(final String musicId) {
                        Log.i("TAG", "musicId = " + musicId);
                        showProgressBar(getString(R.string.data_is_loading));
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                AlbumListRsp result = MusicQueryInterface.getAlbumsByMusicId(
                                        CMMusicDemo.this,
                                        musicId, 1, 5);

                                mUIHandler.obtainMessage(0, result).sendToTarget();

                            }
                        }.start();
                    }
                });
            }
        });
        // 歌曲ID查询歌曲信息 query song informantion by song id
        Button musicQuerybymusic = (Button) this.findViewById(R.id.musicQuerybymusic);
        musicQuerybymusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showParameterDialog(getString(R.string.song_id), new ParameterCallback() {

                    @Override
                    public void callback(final String musicId) {
                        Log.i("TAG", "musicId = " + musicId);
                        showProgressBar(getString(R.string.data_is_loading));
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                MusicInfoResult result = MusicQueryInterface
                                        .getMusicInfoByMusicId(CMMusicDemo.this, musicId);

                                mUIHandler.obtainMessage(0, result).sendToTarget();

                            }
                        }.start();
                    }
                });
            }
        });

        Button btnDel = (Button) this.findViewById(R.id.deletesong);
        btnDel.setOnClickListener(this);

        Button btnFull = (Button) this.findViewById(R.id.fullsong);
        btnFull.setOnClickListener(this);

        Button btnFullSms = (Button) this.findViewById(R.id.fullsongsms);
        btnFullSms.setOnClickListener(this);
        // 开通彩铃 order multimedia ringtone
        Button openRingback = (Button) this.findViewById(R.id.openRingback);
        openRingback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // RingbackManagerInterface.openRingback(CMMusicDemo.this,
                // new CMMusicCallback<Result>() {
                // @Override
                // public void operationResult(Result result) {
                // if (null != result) {
                // new AlertDialog.Builder(CMMusicDemo.this)
                // .setTitle("openRingback").setMessage(result.toString())
                // .setPositiveButton("确认", null).show();
                // }
                //
                // Log.d(LOG_TAG, "ret is " + result);
                // }
                // });
                Toast.makeText(CMMusicDemo.this, R.string.api_is_hiden, 0).show();
            }
        });
        // 赠送彩铃 give multimedia ringtone as a gift
        Button giveRingback = (Button) this.findViewById(R.id.giveRingback);
        giveRingback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showParameterDialog("musicId", new ParameterCallback() {

                    @Override
                    public void callback(final String musicId) {
                        Log.i("TAG", "musicId = " + musicId);
                        RingbackManagerInterface.giveRingback(CMMusicDemo.this, musicId,
                                new CMMusicCallback<Result>() {
                                    @Override
                                    public void operationResult(Result result) {
                                        if (null != result) {
                                            new AlertDialog.Builder(CMMusicDemo.this)
                                                    .setTitle("openRingback")
                                                    .setMessage(result.toString())
                                                    .setPositiveButton(R.string.confirm, null)
                                                    .show();
                                        }

                                        Log.d(LOG_TAG, "ret is " + result);
                                    }
                                });
                    }
                });
            }
        });

        // 歌曲查詢类 Song Query Class
        Button musicQuery = (Button) this.findViewById(R.id.musicQuery);
        musicQuery.setOnClickListener(new OnClickListener() {
            String[] strs = new String[] {
                    "获取榜单信息", "获取榜单音乐信息", "获取专辑信息", "获取专辑音乐信息", "获取歌手音乐信息",
                    "获取标签信息", "获取标签音乐信息", "关键字搜索歌曲"
            };

            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(CMMusicDemo.this)
                        .setTitle("歌曲查询类")
                        .setItems(getResources().getStringArray(R.array.songslist_type),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case 0:
                                                showProgressBar(getString(R.string.data_is_loading));
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        super.run();

                                                        ChartListRsp c = MusicQueryInterface
                                                                .getChartInfo(
                                                                        CMMusicDemo.this, 1, 10);

                                                        mUIHandler.obtainMessage(0, c)
                                                                .sendToTarget();

                                                    }
                                                }.start();

                                                break;
                                            case 1:
                                                showProgressBar(getString(R.string.data_is_loading));
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        MusicListRsp m = MusicQueryInterface
                                                                .getMusicsByChartId(
                                                                        CMMusicDemo.this, "101", 1,
                                                                        5);

                                                        mUIHandler.obtainMessage(0, m)
                                                                .sendToTarget();
                                                    }
                                                }.start();
                                                break;
                                            case 2:
                                                showProgressBar(getString(R.string.data_is_loading));
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        AlbumListRsp a = MusicQueryInterface
                                                                .getAlbumsBySingerId(
                                                                        CMMusicDemo.this, "235",
                                                                        1, 5);

                                                        mUIHandler.obtainMessage(0, a)
                                                                .sendToTarget();
                                                    }
                                                }.start();
                                                break;
                                            case 3:
                                                showParameterDialog(getString(R.string.album_id),
                                                        new ParameterCallback() {

                                                            @Override
                                                            public void callback(
                                                                    final String albumId) {
                                                                Log.w("TAG", "albumId = " + albumId);
                                                                showProgressBar(getString(R.string.data_is_loading));
                                                                new Thread() {
                                                                    @Override
                                                                    public void run() {
                                                                        MusicListRsp m = MusicQueryInterface
                                                                                .getMusicsByAlbumId(
                                                                                        CMMusicDemo.this,
                                                                                        albumId, 1,
                                                                                        5);

                                                                        mUIHandler.obtainMessage(0,
                                                                                m).sendToTarget();
                                                                    }
                                                                }.start();
                                                            }
                                                        });
                                                break;
                                            case 4:
                                                showProgressBar(getString(R.string.data_is_loading));
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        MusicListRsp m = MusicQueryInterface
                                                                .getMusicsBySingerId(
                                                                        CMMusicDemo.this, "235",
                                                                        1, 5);

                                                        mUIHandler.obtainMessage(0, m)
                                                                .sendToTarget();

                                                    }
                                                }.start();
                                                break;
                                            case 5:
                                                showProgressBar(getString(R.string.data_is_loading));
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        TagListRsp t = MusicQueryInterface.getTags(
                                                                CMMusicDemo.this, "10", 1, 5);

                                                        mUIHandler.obtainMessage(0, t)
                                                                .sendToTarget();
                                                    }
                                                }.start();
                                                break;
                                            case 6:
                                                showProgressBar(getString(R.string.data_is_loading));
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        MusicListRsp t = MusicQueryInterface
                                                                .getMusicsByTagId(
                                                                        CMMusicDemo.this, "100", 1,
                                                                        5);

                                                        mUIHandler.obtainMessage(0, t)
                                                                .sendToTarget();

                                                    }
                                                }.start();
                                                break;
                                            case 7:
                                                showProgressBar(getString(R.string.data_is_loading));
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        MusicListRsp t = null;
                                                        t = MusicQueryInterface.getMusicsByKey(
                                                                CMMusicDemo.this,
                                                                URLEncoder
                                                                        .encode(getString(R.string.youdiantian)),
                                                                1,
                                                                5);
                                                        mUIHandler.obtainMessage(0, t)
                                                                .sendToTarget();
                                                    }
                                                }.start();
                                                break;

                                        }

                                        // hideProgressBar();
                                    }
                                }).create().show();
            }
        });
        // 彩铃订购 Order multimedia ringtone by network
        Button buyRingback = (Button) this.findViewById(R.id.buyRingback);
        buyRingback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showParameterDialog("musicId", new ParameterCallback() {

                    @Override
                    public void callback(final String musicId) {
                        Log.i("TAG", "musicId = " + musicId);
                        RingbackManagerInterface.buyRingbackByNet(CMMusicDemo.this, musicId, false,
                                new CMMusicCallback<Result>() {
                                    @Override
                                    public void operationResult(Result ret) {
                                        if (null != ret) {
                                            new AlertDialog.Builder(CMMusicDemo.this)
                                                    .setTitle("buyRingback")
                                                    .setMessage(ret.toString())
                                                    .setPositiveButton(R.string.confirm, null)
                                                    .show();
                                        }
                                    }
                                });
                    }
                });
            }
        });
        // 短信彩铃订购 Order multimedia ringtone by sms
        Button buyRingbackSms = (Button) this.findViewById(R.id.buyRingbackSms);
        buyRingbackSms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RingbackManagerInterface.buyRingbackBySms(CMMusicDemo.this, "60078701600",
                        getString(R.string.youdiantian), "BY2");

            }
        });
        // 振铃下载 download Vibrate Ring
        Button vRing = (Button) this.findViewById(R.id.vRing);
        vRing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showParameterDialog("musicId", new ParameterCallback() {

                    @Override
                    public void callback(final String musicId) {
                        Log.i("TAG", "musicId = " + musicId);
                        VibrateRingManagerInterface.queryVibrateRingDownloadUrlByNet(
                                CMMusicDemo.this,
                                musicId, false, new CMMusicCallback<DownloadResult>() {
                                    @Override
                                    public void operationResult(DownloadResult downloadResult) {
                                        if (null != downloadResult) {
                                            new AlertDialog.Builder(CMMusicDemo.this)
                                                    .setTitle("queryVibrateRingDownloadUrl")
                                                    .setMessage(downloadResult.toString())
                                                    .setPositiveButton(R.string.confirm, null)
                                                    .show();
                                        }

                                        Log.d(LOG_TAG, "vRing Download result is " + downloadResult);
                                    }
                                });
                    }
                });

            }
        });
        // 短信振铃下载
        Button vRingSms = (Button) this.findViewById(R.id.vRingsms);
        vRingSms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateRingManagerInterface.queryVibrateRingDownloadUrlBySms(CMMusicDemo.this,
                        "600902000009331935", null, getString(R.string.youdiantian), "BY2");
            }
        });
        // 开通会员 register open music member by network
        Button openMem = (Button) this.findViewById(R.id.openMem);
        openMem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagerInterface.openMemberByNet(CMMusicDemo.this, false,
                        new CMMusicCallback<Result>() {
                            @Override
                            public void operationResult(Result ret) {
                                if (null != ret) {
                                    new AlertDialog.Builder(CMMusicDemo.this)
                                            .setTitle("openMember").setMessage(ret.toString())
                                            .setPositiveButton(R.string.confirm, null).show();
                                }
                            }
                        });
            }
        });
        // 全曲包月
        Button openFullSong = (Button) this.findViewById(R.id.openSongMonth);
        openFullSong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // FullSongManagerInterface.openSongMonthByNet(CMMusicDemo.this,
                // false,
                // new CMMusicCallback<Result>() {
                // @Override
                // public void operationResult(Result ret) {
                // if (null != ret) {
                // new AlertDialog.Builder(CMMusicDemo.this)
                // .setTitle("openSongMonth").setMessage(ret.toString())
                // .setPositiveButton("确认", null).show();
                // }
                // }
                // });
                Toast.makeText(CMMusicDemo.this, R.string.api_is_hiden, 0).show();
            }
        });
        // 短信全曲 包月
        Button smsSongMonth = (Button) this.findViewById(R.id.smsSongMonth);
        smsSongMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // FullSongManagerInterface.openSongMonthBySms(CMMusicDemo.this);
                Toast.makeText(CMMusicDemo.this, R.string.api_is_hiden, 0).show();
            }
        });
        // 短信开通会员
        Button smsOpenMem = (Button) this.findViewById(R.id.smsOpenMem);
        smsOpenMem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagerInterface.openMemberBySms(CMMusicDemo.this);
            }
        });

        // 获取在线听歌地址 get music listen url online
        Button onlineLse = (Button) this.findViewById(R.id.onlineLse);
        onlineLse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showParameterDialog("musicId", new ParameterCallback() {
                    @Override
                    public void callback(final String musicId) {
                        Log.i("TAG", "musicId = " + musicId);
                        showProgressBar(getString(R.string.data_is_loading));
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                StreamRsp s = OnlineListenerMusicInterface.getStream(
                                        CMMusicDemo.this,
                                        musicId);
                                mUIHandler.obtainMessage(0, s).sendToTarget();
                            }
                        }.start();
                    }
                });
            }
        });

        // 获取彩铃试听地址 get media ring listen url online
        Button crbtPrelisten = (Button) this.findViewById(R.id.crbtPrelisten);
        crbtPrelisten.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showParameterDialog("musicId", new ParameterCallback() {

                    @Override
                    public void callback(final String musicId) {
                        Log.i("TAG", "musicId = " + musicId);
                        showProgressBar(getString(R.string.data_is_loading));
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                CrbtPrelistenRsp c = RingbackManagerInterface.getCrbtPrelisten(
                                        CMMusicDemo.this, musicId);

                                mUIHandler.obtainMessage(0, c).sendToTarget();

                            }
                        }.start();
                    }
                });
            }
        });

        // 查询个人铃音库 get self ringtone list
        Button crbtBox = (Button) this.findViewById(R.id.crbtBox);
        crbtBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(getString(R.string.data_is_loading));
                new Thread() {
                    @Override
                    public void run() {
                        CrbtListRsp c = RingbackManagerInterface.getCrbtBox(CMMusicDemo.this);

                        mUIHandler.obtainMessage(0, c).sendToTarget();

                    }
                }.start();
                // hideProgressBar();
            }
        });

        // 设置默认铃音 set default ringtone
        Button setDefaultCrbt = (Button) this.findViewById(R.id.setDefaultCrbt);
        setDefaultCrbt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(getString(R.string.data_is_loading));
                new Thread() {
                    @Override
                    public void run() {
                        Result c = RingbackManagerInterface.setDefaultCrbt(CMMusicDemo.this,
                                "60078701600");

                        mUIHandler.obtainMessage(0, c).sendToTarget();
                    }
                }.start();
                // hideProgressBar();
            }
        });

        // 手机号查询默认铃音 query default ringtone by phone number
        Button getDefaultCrbt = (Button) this.findViewById(R.id.getDefaultCrbt);
        getDefaultCrbt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(getString(R.string.data_is_loading));
                new Thread() {
                    @Override
                    public void run() {
                        Result c = RingbackManagerInterface.getDefaultCrbt(CMMusicDemo.this,
                                "13551372840");

                        mUIHandler.obtainMessage(0, c).sendToTarget();
                    }
                }.start();
                // hideProgressBar();
            }
        });

        // 振铃试听地址 get vibrate ring listen url online
        Button ringPrelisten = (Button) this.findViewById(R.id.ringPrelisten);
        ringPrelisten.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(getString(R.string.data_is_loading));
                new Thread() {
                    @Override
                    public void run() {
                        DownloadResult c = VibrateRingManagerInterface.getRingPrelisten(
                                CMMusicDemo.this, "60078701600");

                        mUIHandler.obtainMessage(0, c).sendToTarget();
                    }
                }.start();
                // hideProgressBar();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_cmmusic_demo, menu);
        return true;
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.deletesong:
                RingbackManagerInterface.deletePersonRing(this, "60078701600",
                        new CMMusicCallback<Result>() {
                            @Override
                            public void operationResult(Result result) {
                                if (null != result) {
                                    new AlertDialog.Builder(CMMusicDemo.this)
                                            .setTitle("deletePersonRing")
                                            .setMessage(result.toString())
                                            .setPositiveButton(R.string.confirm, null).show();
                                }

                                Log.d(LOG_TAG, "ret is " + result);
                            }
                        });

                break;
            // 全曲下载
            case R.id.fullsong:

                showParameterDialog("musicId", new ParameterCallback() {

                    @Override
                    public void callback(final String musicId) {
                        Log.i("TAG", "musicId = " + musicId);
                        FullSongManagerInterface.getFullSongDownloadUrlByNet(CMMusicDemo.this,
                                musicId, false,
                                new CMMusicCallback<DownloadResult>() {
                                    @Override
                                    public void operationResult(final DownloadResult downloadResult) {
                                        if (null != downloadResult) {
                                            new AlertDialog.Builder(CMMusicDemo.this)
                                                    .setTitle("getFullSongDownloadUrlByNet")
                                                    .setMessage(downloadResult.toString())
                                                    .setPositiveButton(R.string.confirm, null)
                                                    .show();
                                        }

                                        Log.d(LOG_TAG, "FullSong Download result is "
                                                + downloadResult);
                                    }
                                });
                    }
                });

                break;

            case R.id.fullsongsms:
                FullSongManagerInterface.getFullSongDownloadUrlBySms(this, "600902000009331936",
                        null, getString(R.string.youdiantian), "BY2");

                break;
        }
    }

    class T1 extends Thread {
        @Override
        public void run() {
            super.run();
            Looper.prepare();
            if (!InitCmmInterface.initCheck(CMMusicDemo.this)) {
                Hashtable<String, String> b = InitCmmInterface.initCmmEnv(CMMusicDemo.this);
                Message m = new Message();
                m.what = 0;
                m.obj = b;
                mUIHandler.sendMessage(m);
            } else {
                if (null != dialog) {
                    dialog.dismiss();
                }

                Toast.makeText(CMMusicDemo.this, "已初始化过",
                        Toast.LENGTH_LONG).show();
            }
            Looper.loop();
        }
    }

    private ProgressDialog mProgress = null;

    void showProgressBar(final String msg) {
        Log.d(LOG_TAG, "showProgressBar invoked!");

        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgress == null) {
                    mProgress = new ProgressDialog(CMMusicDemo.this);
                    mProgress.setMessage(msg);
                    mProgress.setIndeterminate(false);
                    mProgress.setCancelable(false);
                    mProgress.show();
                }
            }
        });
    }

    void hideProgressBar() {
        Log.d(LOG_TAG, "hideProgressBar invoked!");
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

    void showParameterDialog(String title, final ParameterCallback callback) {
        View view = View.inflate(CMMusicDemo.this, R.layout.parameter_dialog, null);
        final EditText edt = (EditText) view.findViewById(R.id.editText1);
        new AlertDialog.Builder(CMMusicDemo.this)
                .setTitle(title)
                .setView(view)
                .setMessage("请输入参数:" + title)
                .setNegativeButton("取消", null)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String parameter = edt.getText().toString();
                        if (callback != null) {
                            callback.callback(parameter);
                        }
                    }
                }).show();
    }

    interface ParameterCallback {
        void callback(String parameter);
    }
}
