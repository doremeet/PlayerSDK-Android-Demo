package iband.com.ibandplayerdemo;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iband.playersdk.IBandBasePlayer;
import com.iband.playersdk.IBandError;
import com.iband.playersdk.IBandPlayer;
import com.iband.playersdk.IBandPlayerVariant;
import com.iband.playersdk.IBandPlayerView;
import com.iband.playersdk.IBandSDK;
import com.iband.playersdk.IBandStream;

import java.util.ArrayList;

import iband.com.ibandplayerdemo.adapter.VariantAdapter;
import iband.com.ibandplayerdemo.adapter.VariantHolder;

public class PlayerActivity extends AppCompatActivity implements IBandPlayer.Listener, IBandStream.Listener {

    private static final String STREAM_ID = "PUT_HERE_STREAM_ID";
    private boolean mIsThreadRotationActive;
    private boolean mIsUserChangePosition;
    private int mDisplayMode;
    private long mLastDuration = 0;
    private long mLastPosition = 0;
    private View mBtnBack;
    private View mPositionActionBar;
    private View mViewLive;
    private View mViewDirection;
    private View mBtnCenter;
    private View mBottomActionBar;
    private TextView mTvDuration;
    private TextView mTvPosition;
    private ImageView mBtnVariant;
    private ImageView mBtnVrMode;
    private ImageView mBtnVideoScale;
    private ImageView mBtnPlayPause;
    private ProgressBar mProgressPlayer;
    private ProgressBar mProgressStream;
    private ListView mLvSettings;
    private SeekBar mSbPosition;
    private IBandPlayerView mViewPLayer;
    private Thread mThreadUpdateRotation;
    private VariantAdapter mVariantAdapter;
    private ArrayList<VariantHolder> mVariants;
    private IBandSDK mIBandSDK;
    private IBandPlayer mPlayer;
    private IBandStream mStream;


    @Override
    public void onBackPressed() {
        if (mViewPLayer.getVRMode()) {
            mViewPLayer.setVRMode(false);
            setDisplayMode();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPlayerStateChanged(IBandPlayer iBandPlayer, IBandBasePlayer.State state, IBandBasePlayer.State old) {
        if (state == IBandBasePlayer.State.READY) {
            updateViewsOnStateReady();
        } else if (state == IBandBasePlayer.State.BUFFER || state == IBandBasePlayer.State.INITIALIZE)
            mProgressPlayer.setVisibility(View.VISIBLE);
        if (old == IBandBasePlayer.State.INITIALIZE) {
            if (mStream.getStructure() == IBandStream.Structure.PLAIN) {
                updateViewsOnDisplayStatePlain();
            } else if (mStream.getStructure() == IBandStream.Structure.EQUIRECTANGULAR) {
                updateViewsOnDisplayStateEquirectangular();
            }
        }
    }


    @Override
    public void onPlayerCurrentPositionUpdate(IBandPlayer player, long currentPosition) {
        long duration = mPlayer.getDuration();
        if (duration >= 0 && mLastDuration != duration) {
            mLastDuration = duration;
            if (duration == 0)
                mPositionActionBar.setVisibility(View.GONE);
            else
                mPositionActionBar.setVisibility(View.VISIBLE);
            mTvDuration.setText(Utils.msToMinuteSec(duration));
            mSbPosition.setMax((int) duration);
        }
        if (!mIsUserChangePosition) {
            mSbPosition.setProgress((int) currentPosition);

            if (mLastPosition / 1000 != currentPosition / 1000) {
                mLastPosition = currentPosition;
                mTvPosition.setText(Utils.msToMinuteSec(currentPosition));
            }
        }
    }

    @Override
    public void onPlayerBufferPositionUpdate(IBandPlayer player, long bufferPosition) {
        mSbPosition.setSecondaryProgress((int) mPlayer.getBufferPosition());
    }

    @Override
    public void onStreamError(IBandStream iBandStream, IBandError iBandError) {
        toast(iBandError.getErrorMessage());
    }


    @Override
    public void onPlayerPlayableChanged(IBandPlayer iBandPlayer, boolean isPlayable) {
        if (!isPlayable) {
            mViewPLayer.setVRMode(false);
            setDisplayMode();
            mBtnPlayPause.setVisibility(View.GONE);
            mBtnVideoScale.setVisibility(View.GONE);
            mBtnVariant.setEnabled(false);
            mViewPLayer.setVisibility(View.GONE);
        } else {
            mBtnPlayPause.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onPlayerReachEnd(IBandPlayer iBandPlayer) {
        mProgressPlayer.setVisibility(View.GONE);
        mBtnPlayPause.setImageResource(R.drawable.ic_player_play);
    }

    @Override
    public void onPlayerVariantsCreated(IBandPlayer iBandPlayer, IBandPlayerVariant[] iBandPlayerVariants) {
        mVariants = new ArrayList<VariantHolder>();
        for (int i = 0; i < iBandPlayerVariants.length; i++) {
            mVariants.add(0, new VariantHolder(i, iBandPlayerVariants[i]));
        }
        mVariants.add(new VariantHolder(-1, null));
        mVariantAdapter = new VariantAdapter(PlayerActivity.this, mVariants);
        mVariantAdapter.setSelected(mVariants.size() - 1);
        mLvSettings.setVisibility(View.GONE);
        mLvSettings.setAdapter(mVariantAdapter);
        mBtnVariant.setEnabled(true);

    }

    @Override
    public void onPlayerCurrentVariantChanged(IBandPlayer iBandPlayer, int variantIndex, int oldIndex) {
        mVariantAdapter.setActive(variantIndex);
        mVariantAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayerError(IBandPlayer iBandPlayer, IBandError iBandError) {
        mProgressPlayer.setVisibility(View.GONE);
        mProgressStream.setVisibility(View.GONE);
        toast(iBandError.getErrorMessage());
    }

    @Override
    public void onStreamStateChanged(IBandStream iBandStream, IBandStream.State state, IBandStream.State old) {
        if (old == IBandStream.State.INITIALIZE) {
            updateViewsOnStreamInitialize();
        }
        if (iBandStream == mStream) {
            if (iBandStream.getState() == IBandStream.State.CLOSED) {
                updateViewOnStreamStateClosed();
            } else {
                updateViewsOnStreamStateNotClose();
            }
        }
    }

    private void updateViewsOnStreamInitialize() {
        mProgressStream.setVisibility(View.GONE);
    }

    private void updateViewsOnStreamStateNotClose() {
        if (mStream.getType() == IBandStream.Type.LIVE)
            mViewLive.setVisibility(View.VISIBLE);
        else
            mViewLive.setVisibility(View.GONE);
    }

    private void updateViewOnStreamStateClosed() {
        mLvSettings.setAdapter(null);
        mLvSettings.setVisibility(View.GONE);
        mBtnVariant.setEnabled(false);
        mBtnVariant.setVisibility(View.INVISIBLE);
        mProgressStream.setVisibility(View.GONE);
        mProgressPlayer.setVisibility(View.GONE);
        mViewLive.setVisibility(View.GONE);
        mBtnCenter.setVisibility(View.GONE);
        mBtnVrMode.setVisibility(View.GONE);
        mBtnVideoScale.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_player);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        initViews();
        initViewsListeners();
        initPlayer();

    }

    @Override
    protected void onStart() {
        super.onStart();
        startThreadUpdateRotation();
    }

    @Override
    protected void onResume() {
        mPlayer.setView(mViewPLayer);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mPlayer.removeView();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopThreadUpdateRotation();
    }

    @Override
    protected void onDestroy() {
        mStream.releaseStream();
        if (mPlayer != null)
            mPlayer.releasePlayer();
        super.onDestroy();
    }

    private void updateViewsOnDisplayStateEquirectangular() {
        mViewPLayer.setState(IBandPlayerView.DisplayState.EQUIRECTANGULAR);
        mBtnCenter.setVisibility(View.VISIBLE);
        mBtnVrMode.setVisibility(View.VISIBLE);
        mBtnVideoScale.setVisibility(View.GONE);
        startThreadUpdateRotation();
    }

    private void updateViewsOnDisplayStatePlain() {
        mViewPLayer.setState(IBandPlayerView.DisplayState.PLAIN);
        mBtnCenter.setVisibility(View.GONE);
        mBtnVrMode.setVisibility(View.GONE);
        mBtnVideoScale.setVisibility(View.VISIBLE);
        stopThreadUpdateRotation();
    }

    private void updateViewsOnStateReady() {
        mProgressPlayer.setVisibility(View.GONE);
        mViewPLayer.setVisibility(View.VISIBLE);
        mBtnVariant.setVisibility(View.VISIBLE);
        mViewPLayer.resetPanoramaPosition();
    }

    private void startThreadUpdateRotation() {
        stopThreadUpdateRotation();
        if (mStream == null || mStream.getStructure() != IBandStream.Structure.EQUIRECTANGULAR)
            return;
        mIsThreadRotationActive = true;
        mThreadUpdateRotation = new Thread() {
            public Runnable mRunnableUpdateRotation = new Runnable() {
                @Override
                public void run() {
                    mViewDirection.setRotation((float) Math.toDegrees(mViewPLayer.getPanoramaPositionAngle()));
                }
            };

            @Override
            public void run() {
                while (mIsThreadRotationActive) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mViewDirection.post(mRunnableUpdateRotation);
                }
            }
        };
        mThreadUpdateRotation.start();

    }

    private void stopThreadUpdateRotation() {
        mIsThreadRotationActive = false;
        if (mThreadUpdateRotation != null)
            mThreadUpdateRotation.interrupt();
        mThreadUpdateRotation = null;
    }

    private void updateViewPlayPause() {
        if (mPlayer.getPlayWhenReady())
            mBtnPlayPause.setImageResource(R.drawable.ic_player_pause);
        else
            mBtnPlayPause.setImageResource(R.drawable.ic_player_play);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }

    private void onVariantSelected(int position) {
        if (mVariants.get(position).isAuto()) {
            mVariantAdapter.setSelected(position);
            mPlayer.setAutoVariant();
        } else {
            mVariantAdapter.setSelected(position);
            mPlayer.setCurrentVariant(mVariants.get(position).getIndex());
        }
        mVariantAdapter.notifyDataSetChanged();
    }

    private void setDisplayMode() {
        if (mViewPLayer.getState() == IBandPlayerView.DisplayState.EQUIRECTANGULAR && mStream.getState() == IBandStream.State.OPEN) {
            if (mViewPLayer.getVRMode()) {
                mBottomActionBar.setVisibility(View.GONE);
                mBtnPlayPause.setVisibility(View.GONE);
                mLvSettings.setVisibility(View.GONE);
                mBtnVrMode.setVisibility(View.GONE);
                mBtnCenter.setVisibility(View.GONE);
            } else {
                mBottomActionBar.setVisibility(View.VISIBLE);
                mBtnPlayPause.setVisibility(View.VISIBLE);
                mBtnVrMode.setVisibility(View.VISIBLE);
                mBtnCenter.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initPlayer() {
        mIBandSDK = new IBandSDK(this);
        mStream = mIBandSDK.createStream(STREAM_ID);
        mStream.addListener(this);
        mPlayer = mIBandSDK.createPlayer();
        mPlayer.addListener(this);
        mPlayer.setStream(mStream);
        updateViewsOnStreamStateNotClose();
    }

    private void initViews() {
        mViewPLayer = (IBandPlayerView) findViewById(R.id.view_player);
        mSbPosition = (SeekBar) findViewById(R.id.sb_position);
        mTvPosition = (TextView) findViewById(R.id.tv_position);
        mTvDuration = (TextView) findViewById(R.id.tv_duration);
        mProgressPlayer = (ProgressBar) findViewById(R.id.progress_player);
        mViewDirection = findViewById(R.id.view_direction);
        mProgressStream = (ProgressBar) findViewById(R.id.progress_stream);
        mLvSettings = (ListView) findViewById(R.id.lv_settings);
        mBtnVariant = (ImageView) findViewById(R.id.btn_variant);
        mBtnVariant.setEnabled(false);
        mPositionActionBar = findViewById(R.id.position_action_bar);
        mViewLive = findViewById(R.id.vLive);
        mBtnBack = findViewById(R.id.btnBack);
        mBtnVrMode = (ImageView) findViewById(R.id.btn_vr_mode);
        mBtnCenter = findViewById(R.id.btn_center);
        mBtnVideoScale = (ImageView) findViewById(R.id.btn_video_scale);
        mBtnPlayPause = (ImageView) findViewById(R.id.btn_play_pause);
        mBottomActionBar = findViewById(R.id.bottom_action_bar);

    }

    private void initViewsListeners() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBtnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.getDuration() > 0 && Math.abs(mPlayer.getCurrentPosition() - mPlayer.getDuration()) < 200) {
                    mPlayer.seekTo(0);
                    mPlayer.setPlayWhenReady(true);
                } else {
                    mPlayer.setPlayWhenReady(!mPlayer.getPlayWhenReady());
                }
                updateViewPlayPause();
            }
        });
        mBtnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPLayer.resetPanoramaPosition();
            }
        });
        mBtnVrMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPLayer.setVRMode(true);
                setDisplayMode();
            }
        });
        mBtnVideoScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisplayMode++;
                mDisplayMode %= 3;
                mViewPLayer.setVideoScale(IBandPlayerView.VideoScale.values()[mDisplayMode]);
            }
        });

        mBtnVariant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLvSettings.getVisibility() == View.VISIBLE) {
                    mLvSettings.setVisibility(View.GONE);
                } else {
                    mLvSettings.setVisibility(View.VISIBLE);
                }
            }
        });
        mLvSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onVariantSelected(position);
            }
        });
        mSbPosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mTvPosition.setText(Utils.msToMinuteSec((long) progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserChangePosition = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserChangePosition = false;
                if (mPlayer.isPlayable()) {
                    updateViewPlayPause();
                    mPlayer.seekTo((long) seekBar.getProgress());
                }
            }
        });
    }
}
