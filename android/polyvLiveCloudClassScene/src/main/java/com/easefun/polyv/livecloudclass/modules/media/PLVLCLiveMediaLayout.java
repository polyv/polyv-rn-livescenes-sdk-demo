package com.easefun.polyv.livecloudclass.modules.media;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easefun.polyv.businesssdk.api.auxiliary.PolyvAuxiliaryVideoview;
import com.easefun.polyv.businesssdk.api.common.player.PolyvBaseVideoView;
import com.easefun.polyv.businesssdk.api.common.player.PolyvPlayError;
import com.easefun.polyv.businesssdk.api.common.ppt.IPolyvPPTView;
import com.easefun.polyv.businesssdk.model.video.PolyvLiveMarqueeVO;
import com.easefun.polyv.businesssdk.model.video.PolyvMediaPlayMode;
import com.easefun.polyv.businesssdk.sub.marquee.PolyvMarqueeItem;
import com.easefun.polyv.businesssdk.sub.marquee.PolyvMarqueeUtils;
import com.easefun.polyv.businesssdk.sub.marquee.PolyvMarqueeView;
import com.easefun.polyv.livecloudclass.R;
import com.easefun.polyv.livecloudclass.modules.chatroom.chatlandscape.PLVLCChatLandscapeLayout;
import com.easefun.polyv.livecloudclass.modules.liveroom.IPLVLiveLandscapePlayerController;
import com.easefun.polyv.livecloudclass.modules.media.controller.IPLVLCLiveMediaController;
import com.easefun.polyv.livecloudclass.modules.media.danmu.IPLVLCDanmuController;
import com.easefun.polyv.livecloudclass.modules.media.danmu.IPLVLCLandscapeMessageSender;
import com.easefun.polyv.livecloudclass.modules.media.danmu.PLVLCDanmuFragment;
import com.easefun.polyv.livecloudclass.modules.media.danmu.PLVLCDanmuWrapper;
import com.easefun.polyv.livecloudclass.modules.media.danmu.PLVLCLandscapeMessageSendPanel;
import com.easefun.polyv.livecloudclass.modules.media.widget.PLVLCLightTipsView;
import com.easefun.polyv.livecloudclass.modules.media.widget.PLVLCLiveAudioModeView;
import com.easefun.polyv.livecloudclass.modules.media.widget.PLVLCPlaceHolderView;
import com.easefun.polyv.livecloudclass.modules.media.widget.PLVLCVideoLoadingLayout;
import com.easefun.polyv.livecloudclass.modules.media.widget.PLVLCVolumeTipsView;
import com.easefun.polyv.livecommon.module.data.IPLVLiveRoomDataManager;
import com.easefun.polyv.livecommon.module.data.PLVStatefulData;
import com.easefun.polyv.livecommon.module.modules.player.PLVPlayerState;
import com.easefun.polyv.livecommon.module.modules.player.live.contract.IPLVLivePlayerContract;
import com.easefun.polyv.livecommon.module.modules.player.live.presenter.PLVLivePlayerPresenter;
import com.easefun.polyv.livecommon.module.modules.player.live.view.PLVAbsLivePlayerView;
import com.easefun.polyv.livecommon.module.modules.player.playback.prsenter.data.PLVPlayInfoVO;
import com.easefun.polyv.livecommon.module.utils.listener.IPLVOnDataChangedListener;
import com.easefun.polyv.livecommon.module.utils.rotaion.PLVOrientationManager;
import com.easefun.polyv.livecommon.ui.widget.PLVPlayerLogoView;
import com.easefun.polyv.livecommon.ui.widget.PLVSwitchViewAnchorLayout;
import com.easefun.polyv.livescenes.model.PolyvChatFunctionSwitchVO;
import com.easefun.polyv.livescenes.model.PolyvLiveClassDetailVO;
import com.easefun.polyv.livescenes.video.PolyvLiveVideoView;
import com.easefun.polyv.livescenes.video.api.IPolyvLiveListenerEvent;
import com.plv.foundationsdk.log.PLVCommonLog;
import com.plv.foundationsdk.utils.PLVScreenUtils;
import com.plv.thirdpart.blankj.utilcode.util.ScreenUtils;
import com.plv.thirdpart.blankj.utilcode.util.StringUtils;
import com.plv.thirdpart.blankj.utilcode.util.TimeUtils;
import com.plv.thirdpart.blankj.utilcode.util.ToastUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * ??????????????????????????????????????????????????? IPLVLCMediaLayout ??????
 */
public class PLVLCLiveMediaLayout extends FrameLayout implements IPLVLCMediaLayout {
    // <editor-fold defaultstate="collapsed" desc="??????">
    private static final String TAG = "PLVLCLiveVideoLayout";
    private static final float RATIO_WH = 16f / 9;//???????????????????????????16:9??????
    //????????????????????????
    private IPLVLiveRoomDataManager liveRoomDataManager;

    //?????????SwitchView??????????????????View????????????
    private PLVSwitchViewAnchorLayout playerSwitchAnchor;
    private FrameLayout flLivePlayerSwitchView;
    private View playerView;
    //????????????????????????view
    private PolyvLiveVideoView videoView;
    //????????????????????????view
    private PolyvAuxiliaryVideoview subVideoView;

    private TextView tvCountDown;
    private LinearLayout llAuxiliaryCountDown;

    //????????????view
    private PLVLCLiveAudioModeView audioModeView;
    // Logo
    private PLVPlayerLogoView logoView;
    //?????????????????????
    private IPLVLCLiveMediaController mediaController;

    //????????????????????????view
    private PLVLCVideoLoadingLayout loadingView;
    //???????????????????????????view
    private PLVLCPlaceHolderView noStreamView;
    //????????????????????????view
    private View stopStreamView;

    //tips view
    private PLVLCLightTipsView lightTipsView;
    private PLVLCVolumeTipsView volumeTipsView;

    //???????????????
    private PLVLCChatLandscapeLayout chatLandscapeLayout;

    //??????
    private IPLVLCDanmuController danmuController;
    //???????????????
    private PLVLCDanmuWrapper danmuWrapper;
    //???????????????????????????
    private IPLVLCLandscapeMessageSender landscapeMessageSender;

    //?????????
    private PolyvMarqueeView marqueeView;
    private PolyvMarqueeItem marqueeItem;
    private PolyvMarqueeUtils marqueeUtils;

    //????????????????????????????????????????????????
    private ImageView screenshotIV;

    //?????????
    private TextView timeCountDownTv;
    //????????????????????????
    private CountDownTimer startTimeCountDown;
    //??????????????????
    private String liveStartTime;

    //???????????????????????????????????????
    private int landscapeMarginRightForLinkMicLayout;
    //???????????????RTC
    private boolean isJoinRTC;
    //ppt?????????????????????????????????????????????????????????????????????ppt????????????????????????
    private boolean isClickShowSubTab = true;
    private boolean isShowLandscapeRTCLayout = false;
    private boolean isLandscape;

    //?????????presenter
    private IPLVLivePlayerContract.ILivePlayerPresenter livePlayerPresenter;

    //Listener
    private OnViewActionListener onViewActionListener;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="?????????">
    public PLVLCLiveMediaLayout(@NonNull Context context) {
        this(context, null);
    }

    public PLVLCLiveMediaLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PLVLCLiveMediaLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="?????????view">
    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.plvlc_live_player_layout, this);
        playerSwitchAnchor = findViewById(R.id.plvlc_playback_switch_anchor_player);
        flLivePlayerSwitchView = findViewById(R.id.plvlc_playback_fl_player_switch_view_parent);
        videoView = findViewById(R.id.live_video_view);
        playerView = videoView.findViewById(PolyvBaseVideoView.IJK_VIDEO_ID);
        subVideoView = findViewById(R.id.sub_video_view);
        tvCountDown = findViewById(R.id.auxiliary_tv_count_down);
        llAuxiliaryCountDown = findViewById(R.id.polyv_auxiliary_controller_ll_tips);
        llAuxiliaryCountDown.setVisibility(GONE);
        audioModeView = findViewById(R.id.audio_mode_ly);
        logoView = findViewById(R.id.live_logo_view);
        loadingView = findViewById(R.id.video_loading_view);
        noStreamView = findViewById(R.id.no_stream_ly);
        stopStreamView = findViewById(R.id.stop_stream_ly);
        lightTipsView = findViewById(R.id.light_view);
        volumeTipsView = findViewById(R.id.volume_view);
        screenshotIV = findViewById(R.id.screenshot_iv);
        timeCountDownTv = findViewById(R.id.time_count_down_tv);
        mediaController = findViewById(R.id.controller_view);
        chatLandscapeLayout = findViewById(R.id.chat_landscape_ly);

        // ???????????????
        PLVLCPlaceHolderView placeHolderView = new PLVLCPlaceHolderView(getContext());
        placeHolderView.setVisibility(VISIBLE);
        placeHolderView.setPlaceHolderText("");
        addView(placeHolderView, 0);

        initVideoView();
        initDanmuView();
        initMediaController();
        initAudioModeView();
        initLoadingView();
        initSwitchView();
        initLayoutWH();
    }

    private void initVideoView() {
        //??????noStreamView
        noStreamView.setPlaceHolderImg(R.drawable.plvlc_bg_player_no_stream);
        noStreamView.setPlaceHolderText(getResources().getString(R.string.plv_player_video_live_no_stream));

        videoView.setSubVideoView(subVideoView);
        videoView.setAudioModeView(audioModeView);
        videoView.setPlayerBufferingIndicator(loadingView);
        videoView.setNoStreamIndicator(noStreamView);
        videoView.setStopStreamIndicator(stopStreamView);
        videoView.setMediaController(mediaController);
        //???????????????
        videoView.post(new Runnable() {
            @Override
            public void run() {
                marqueeView = ((Activity) getContext()).findViewById(R.id.plvlc_marquee_view);//after videoLayout add, post find
                marqueeItem = new PolyvMarqueeItem();
                videoView.setMarqueeView(marqueeView, marqueeItem);
            }
        });
    }

    private void initDanmuView() {
        danmuController = new PLVLCDanmuFragment();
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.danmu_ly, (Fragment) danmuController, "danmuFragment").commitAllowingStateLoss();

        danmuWrapper = new PLVLCDanmuWrapper(this);
        danmuWrapper.setDanmuController(danmuController);

        landscapeMessageSender = new PLVLCLandscapeMessageSendPanel((AppCompatActivity) getContext(), this);
        landscapeMessageSender.setOnSendMessageListener(new IPLVLCLandscapeMessageSender.OnSendMessageListener() {
            @Override
            public void onSend(String message) {
                if (onViewActionListener != null) {
                    //????????????????????????
                    Pair<Boolean, Integer> result = onViewActionListener.onSendChatMessageAction(message);
                    if (!result.first) {
                        ToastUtils.showShort(getResources().getString(R.string.plv_chat_toast_send_msg_failed) + ": " + result.second);
                    }
                }
            }
        });
    }

    private void initMediaController() {
        mediaController.setOnViewActionListener(new IPLVLCLiveMediaController.OnViewActionListener() {
            @Override
            public void onStartSendMessageAction() {
                landscapeMessageSender.openMessageSender();
            }

            @Override
            public void onClickShowOrHideSubTab(boolean toShow) {
                isClickShowSubTab = toShow;
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (isJoinRTC && PLVScreenUtils.isLandscape(getContext())) {
                    if (toShow) {
                        //switch anchor
                        showLandscapeRTCLayout(true);
                    } else {
                        //switch anchor
                        showLandscapeRTCLayout(false);
                    }
                }
                if (onViewActionListener != null) {
                    onViewActionListener.onClickShowOrHideSubTab(toShow);
                }
            }

            @Override
            public void onShowBulletinAction() {
                if (onViewActionListener != null) {
                    onViewActionListener.onShowBulletinAction();
                }
            }

            @Override
            public void onSendLikesAction() {
                if (onViewActionListener != null) {
                    onViewActionListener.onSendLikesAction();
                }
            }

            @Override
            public void onShow(boolean show) {
                if (onViewActionListener != null) {
                    onViewActionListener.onShowMediaController(show);
                }
            }

        });
    }

    private void initAudioModeView() {
        audioModeView.setOnChangeVideoModeListener(new PLVLCLiveAudioModeView.OnChangeVideoModeListener() {
            @Override
            public void onClickPlayVideo() {
                livePlayerPresenter.changeMediaPlayMode(PolyvMediaPlayMode.MODE_VIDEO);
            }
        });
    }

    private void initLoadingView() {
        loadingView.bindVideoView(videoView);
    }

    private void initSwitchView() {
        playerSwitchAnchor.setOnSwitchListener(new PLVSwitchViewAnchorLayout.IPLVSwitchViewAnchorLayoutListener() {
            @Override
            protected void onSwitchElsewhereBefore() {
                View childOfAnchor;
                try {
                    childOfAnchor = playerSwitchAnchor.getSwitchView();
                } catch (IllegalAccessException e) {
                    PLVCommonLog.exception(e);
                    return;
                }
                PLVCommonLog.d(TAG, "onSwitchElsewhereBefore-> childOfAnchor= " + childOfAnchor);
                //????????????switch view anchor??????switch view?????????????????????PPT?????????item?????????????????????????????????
                //?????????????????????switch??????
                if (childOfAnchor == flLivePlayerSwitchView) {
                    videoView.removeView(playerView);
                    videoView.removeView(screenshotIV);
                    videoView.removeView(audioModeView);
                    videoView.removeView(logoView);
                    videoView.removeView(loadingView);
                    videoView.removeView(noStreamView);
                    videoView.removeView(stopStreamView);

                    flLivePlayerSwitchView.addView(playerView);
                    flLivePlayerSwitchView.addView(screenshotIV);
                    flLivePlayerSwitchView.addView(audioModeView);
                    flLivePlayerSwitchView.addView(logoView);
                    flLivePlayerSwitchView.addView(loadingView);
                    flLivePlayerSwitchView.addView(noStreamView);
                    flLivePlayerSwitchView.addView(stopStreamView);
                }
            }

            @Override
            protected void onSwitchBackAfter() {
                View childOfAnchor;
                try {
                    childOfAnchor = playerSwitchAnchor.getSwitchView();
                } catch (IllegalAccessException e) {
                    PLVCommonLog.exception(e);
                    return;
                }
                PLVCommonLog.d(TAG, "onSwitchBackAfter-> childOfAnchor= " + childOfAnchor);
                //????????????switch view anchor??????switch view?????????????????????PPT?????????item?????????????????????????????????
                //?????????????????????switch??????
                if (childOfAnchor == flLivePlayerSwitchView) {
                    flLivePlayerSwitchView.removeAllViews();
                    videoView.addView(playerView, 0);
                    videoView.addView(screenshotIV);
                    videoView.addView(audioModeView);
                    videoView.addView(logoView);
                    videoView.addView(loadingView);
                    videoView.addView(noStreamView);
                    videoView.addView(stopStreamView);
                }
            }
        });
    }

    private void initLayoutWH() {
        //??????????????????????????????
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams vlp = getLayoutParams();
                vlp.width = -1;
                vlp.height = ScreenUtils.isPortrait() ? (int) (getWidth() / RATIO_WH) : -1;
                setLayoutParams(vlp);
            }
        });
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="??????API - ??????IPLVLCMediaLayout?????????common??????">
    @Override
    public void init(@NonNull IPLVLiveRoomDataManager liveRoomDataManager) {
        this.liveRoomDataManager = liveRoomDataManager;

        observeLiveRoomData();

        livePlayerPresenter = new PLVLivePlayerPresenter(liveRoomDataManager);
        livePlayerPresenter.registerView(livePlayerView);
        livePlayerPresenter.init();

        mediaController.setLivePlayerPresenter(livePlayerPresenter);
    }

    @Override
    public void startPlay() {
        livePlayerPresenter.startPlay();
    }

    @Override
    public void pause() {
        livePlayerPresenter.pause();
    }

    @Override
    public void resume() {
        livePlayerPresenter.resume();
    }

    @Override
    public void stop() {
        livePlayerPresenter.stop();
    }

    @Override
    public boolean isPlaying() {
        return livePlayerPresenter.isPlaying();
    }

    @Override
    public void setVolume(int volume) {
        livePlayerPresenter.setVolume(volume);
    }

    @Override
    public int getVolume() {
        return livePlayerPresenter.getVolume();
    }

    @Override
    public void sendDanmaku(CharSequence message) {
        danmuController.sendDanmaku(message);
    }

    @Override
    public void updateOnClickCloseFloatingView() {
        mediaController.show();
        mediaController.updateOnClickCloseFloatingView();
    }

    @Override
    public PLVSwitchViewAnchorLayout getPlayerSwitchView() {
        return playerSwitchAnchor;
    }

    @Override
    public PLVLCChatLandscapeLayout getChatLandscapeLayout() {
        return chatLandscapeLayout;
    }

    @Override
    public void setOnViewActionListener(OnViewActionListener listener) {
        this.onViewActionListener = listener;
    }

    @Override
    public void addOnPlayerStateListener(IPLVOnDataChangedListener<PLVPlayerState> listener) {
        livePlayerPresenter.getData().getPlayerState().observe((LifecycleOwner) getContext(), listener);
    }

    @Override
    public void addOnPPTShowStateListener(IPLVOnDataChangedListener<Boolean> listener) {
        livePlayerPresenter.getData().getPPTShowState().observe((LifecycleOwner) getContext(), listener);
    }

    @Override
    public boolean onBackPressed() {
        if (ScreenUtils.isLandscape()) {
            PLVOrientationManager.getInstance().setPortrait((Activity) getContext());
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        if (livePlayerPresenter != null) {
            livePlayerPresenter.destroy();
        }

        if (mediaController != null) {
            mediaController.clean();
        }

        if (danmuWrapper != null) {
            danmuWrapper.release();
        }

        if (danmuController != null) {
            danmuController.release();
        }

        if (landscapeMessageSender != null) {
            landscapeMessageSender.dismiss();
        }

        stopLiveCountDown();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="??????API - ??????IPLVLCMediaLayout?????????live??????">
    @Override
    public void setLandscapeControllerView(@NonNull IPLVLiveLandscapePlayerController landscapeControllerView) {
        mediaController.setLandscapeController(landscapeControllerView);
        danmuWrapper.setDanmuSwitchLandView(landscapeControllerView.getDanmuSwitchView());
    }

    @Override
    public void updateViewerCount(long viewerCount) {
        mediaController.updateViewerCount(viewerCount);
    }

    @Override
    public void updateWhenJoinRTC(int linkMicLayoutLandscapeWidth) {
        isJoinRTC = true;
        landscapeMarginRightForLinkMicLayout = linkMicLayoutLandscapeWidth;

        mediaController.updateWhenJoinLinkMic(liveRoomDataManager.isSupportRTC());

        if (liveRoomDataManager.isSupportRTC()) {
            //????????????RTC??????????????????????????????????????????rtc?????????+rtc?????????
            stop();
        } else {
            //???????????????RTC????????????????????????????????????????????????cdn?????????+rtc?????????
            livePlayerPresenter.setPlayerVolume(0);
        }
        //?????????????????????
        livePlayerPresenter.setNeedGestureDetector(false);

        mediaController.show();

        if (PLVScreenUtils.isPortrait(getContext())) {
            setPortrait();
        } else {
            setLandscape();
        }
    }

    @Override
    public void updateWhenLeaveRTC() {
        isJoinRTC = false;
        landscapeMarginRightForLinkMicLayout = 0;

        mediaController.updateWhenLeaveLinkMic();

        if (liveRoomDataManager.isSupportRTC()) {
            startPlay();
        }
        //?????????????????????
        livePlayerPresenter.setNeedGestureDetector(true);
        //?????????????????????
        livePlayerPresenter.setPlayerVolume(100);

        if (PLVScreenUtils.isPortrait(getContext())) {
            setPortrait();
        } else {
            setLandscape();
        }
    }

    @Override
    public void notifyRTCPrepared() {
        videoView.rtcPrepared();
    }

    @Override
    public void addOnLinkMicStateListener(IPLVOnDataChangedListener<Pair<Boolean, Boolean>> listener) {
        livePlayerPresenter.getData().getLinkMicState().observe((LifecycleOwner) getContext(), listener);
    }

    @Override
    public void addOnSeiDataListener(IPLVOnDataChangedListener<Long> listener) {
        livePlayerPresenter.getData().getSeiData().observe((LifecycleOwner) getContext(), listener);
    }

    @Override
    public void setOnRTCPlayEventListener(IPolyvLiveListenerEvent.OnRTCPlayEventListener listener) {
        videoView.setOnRTCPlayEventListener(listener);
    }

    @Override
    public void setShowLandscapeRTCLayout() {
        isShowLandscapeRTCLayout = true;
        if (isLandscape) {
            //???????????????????????????????????????RTC layout
            showLandscapeRTCLayout(true);
        } else {
            //?????????????????????????????????????????????????????????????????????????????????????????????
            PLVCommonLog.d(TAG, "PLVLCLiveMediaLayout.setShowLandscapeRTCLayout-->isLandscape=false. We'll wait for portrait to show landscape rtc layout");
        }
    }

    @Override
    public void setHideLandscapeRTCLayout() {
        isShowLandscapeRTCLayout = false;
        if (isLandscape) {
            //????????????????????????????????????RTC layout
            showLandscapeRTCLayout(false);
        } else {
            //??????????????????????????????????????????0???????????????????????????
            PLVCommonLog.d(TAG, "PLVLCLiveMediaLayout.setHideLandscapeRTCLayout-->isLandscape=false. We do noting when it is portrait");
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="??????API - ??????IPLVLCMediaLayout?????????playback??????????????????">
    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public void seekTo(int progress, int max) {

    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public void setPPTView(IPolyvPPTView pptView) {

    }

    @Override
    public void addOnPlayInfoVOListener(IPLVOnDataChangedListener<PLVPlayInfoVO> listener) {

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="????????? - MVP?????????view??????">
    private IPLVLivePlayerContract.ILivePlayerView livePlayerView = new PLVAbsLivePlayerView() {
        @Override
        public PolyvLiveVideoView getLiveVideoView() {
            return videoView;
        }

        @Override
        public PolyvAuxiliaryVideoview getSubVideoView() {
            return subVideoView;
        }

        @Override
        public View getBufferingIndicator() {
            return loadingView;
        }

        @Override
        public View getNoStreamIndicator() {
            return noStreamView;
        }

        @Override
        public PLVPlayerLogoView getLogo() {
            return logoView;
        }

        @Override
        public void onSubVideoViewClick(boolean mainPlayerIsPlaying) {
            super.onSubVideoViewClick(mainPlayerIsPlaying);

            if (!mainPlayerIsPlaying) {
                mediaController.updateWhenSubVideoViewClick();
                if (mediaController.isShowing()) {
                    mediaController.hide();
                } else {
                    mediaController.show();
                }
            }
        }

        @Override
        public void onSubVideoViewCountDown(boolean isOpenAdHead, int totalTime, int remainTime, int adStage) {
            if (isOpenAdHead) {
                llAuxiliaryCountDown.setVisibility(VISIBLE);
                tvCountDown.setText("?????????" + remainTime + "s");
            }
        }

        @Override
        public void onSubVideoViewVisiblityChanged(boolean isOpenAdHead, boolean isShow) {
            if (isOpenAdHead) {
                if (!isShow) {
                    llAuxiliaryCountDown.setVisibility(GONE);
                }
            } else {
                llAuxiliaryCountDown.setVisibility(GONE);
            }
        }

        @Override
        public void onPlayError(PolyvPlayError error, String tips) {
            super.onPlayError(error, tips);
            ToastUtils.showLong(tips);
            PLVCommonLog.e(TAG, tips);
        }

        @Override
        public void onNoLiveAtPresent() {
            super.onNoLiveAtPresent();
            ToastUtils.showShort(R.string.plv_player_toast_no_live);
        }

        @Override
        public void onLiveEnd() {
            super.onLiveEnd();
            startLiveTimeCountDown(liveStartTime);
        }

        @Override
        public void onPrepared(int mediaPlayMode) {
            super.onPrepared(mediaPlayMode);
            hideScreenShotView();
            stopLiveCountDown();
            if (!isJoinRTC) {
                mediaController.updateWhenVideoViewPrepared();
            }
            mediaController.show();
        }

        @Override
        public void onGetMarqueeVo(PolyvLiveMarqueeVO marqueeVo, String viewerName) {
            super.onGetMarqueeVo(marqueeVo, viewerName);
            if (marqueeUtils == null) {
                marqueeUtils = new PolyvMarqueeUtils();
            }
            // ???????????????????????????????????????
            marqueeUtils.updateMarquee((Activity) getContext(), marqueeVo, marqueeItem, viewerName);
        }

        @Override
        public void onRestartPlay() {
            super.onRestartPlay();
            showScreenShotView();
        }

        @Override
        public boolean onLightChanged(int changeValue, boolean isEnd) {
            lightTipsView.setLightPercent(changeValue, isEnd);
            return true;
        }

        @Override
        public boolean onVolumeChanged(int changeValue, boolean isEnd) {
            volumeTipsView.setVolumePercent(changeValue, isEnd);
            return true;
        }

        @Override
        public void onServerDanmuOpen(boolean isServerDanmuOpen) {
            super.onServerDanmuOpen(isServerDanmuOpen);
            danmuWrapper.setOnServerDanmuOpen(isServerDanmuOpen);
        }

        @Override
        public void onShowPPTView(int visible) {
            super.onShowPPTView(visible);
            mediaController.setServerEnablePPT(visible == View.VISIBLE);
        }

        @Override
        public boolean onNetworkRecover() {
            //???????????????????????????????????????????????????????????????????????????????????????
            return isJoinRTC;
        }
    };
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="????????? - ??????view???????????????">
    private void hideScreenShotView() {
        screenshotIV.setVisibility(GONE);
    }

    private void showScreenShotView() {
        Bitmap screenshot = livePlayerPresenter.screenshot();
        screenshotIV.setImageBitmap(screenshot);
        screenshotIV.setVisibility(VISIBLE);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="????????? - ??????RTC?????????????????????---??????????????????????????????">
    private void showLandscapeRTCLayout(boolean show) {
        MarginLayoutParams switchAnchorLp = (MarginLayoutParams) playerSwitchAnchor.getLayoutParams();
        switchAnchorLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        switchAnchorLp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        switchAnchorLp.rightMargin = show ? landscapeMarginRightForLinkMicLayout : 0;
        playerSwitchAnchor.setLayoutParams(switchAnchorLp);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="????????? - ??????????????????????????????">
    private void startLiveTimeCountDown(String startTime) {
        this.liveStartTime = startTime;
        //2019/08/01 12:22:00

        if (TextUtils.isEmpty(startTime)) {
            timeCountDownTv.setVisibility(View.GONE);
            return;
        }

        //????????????
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        long startTimeMillis = TimeUtils.string2Millis(startTime, dateFormat);
        long timeSpanMillis = startTimeMillis - System.currentTimeMillis();

        //??????????????????
        startTimeCountDown = new CountDownTimer(timeSpanMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeText = "????????????" + TimeUtils.toCountDownTime(millisUntilFinished);
                timeCountDownTv.setText(timeText);
                timeCountDownTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                timeCountDownTv.setVisibility(View.GONE);
            }
        };
        startTimeCountDown.start();
    }

    private void stopLiveCountDown() {
        if (startTimeCountDown != null) {
            startTimeCountDown.cancel();
        }
        if (timeCountDownTv != null) {
            timeCountDownTv.setVisibility(GONE);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="????????????">
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandscape();
            isLandscape = true;
        } else {
            setPortrait();
            isLandscape = false;
        }
    }

    private void setLandscape() {
        //videoLayout root
        MarginLayoutParams vlp = (MarginLayoutParams) getLayoutParams();
        vlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        vlp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(vlp);
        //switch anchor
        if (isJoinRTC && isClickShowSubTab && isShowLandscapeRTCLayout) {
            //???????????????????????????????????????????????????????????????????????????????????????????????????
            showLandscapeRTCLayout(true);
        } else {
            //????????????????????????
            showLandscapeRTCLayout(false);
        }
    }

    private void setPortrait() {
        //videoLayout root
        MarginLayoutParams vlp = (MarginLayoutParams) getLayoutParams();
        vlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //?????????????????????videoLayout.getWidth()??????????????????????????????????????????????????????????????????margin?????????
        int portraitWidth = Math.min(ScreenUtils.getScreenHeight(), ScreenUtils.getScreenWidth());
        vlp.height = (int) (portraitWidth / RATIO_WH);
        setLayoutParams(vlp);
        //switch anchor
        MarginLayoutParams switchAnchorLp = (MarginLayoutParams) playerSwitchAnchor.getLayoutParams();
        switchAnchorLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //?????????????????????videoLayout.getWidth()??????????????????????????????????????????????????????????????????margin?????????
        switchAnchorLp.height = (int) (portraitWidth / RATIO_WH);
        switchAnchorLp.rightMargin = 0;
        playerSwitchAnchor.setLayoutParams(switchAnchorLp);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="???????????? - ?????????????????????????????????????????????">
    private void observeLiveRoomData() {
        //?????? ????????????????????????????????????????????????????????????
        liveRoomDataManager.getClassDetailVO().observe((LifecycleOwner) getContext(), new Observer<PLVStatefulData<PolyvLiveClassDetailVO>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(@Nullable PLVStatefulData<PolyvLiveClassDetailVO> liveClassDetailVO) {
                liveRoomDataManager.getClassDetailVO().removeObserver(this);
                if (liveClassDetailVO == null || !liveClassDetailVO.isSuccess()) {
                    return;
                }
                PolyvLiveClassDetailVO liveClassDetail = liveClassDetailVO.getData();
                if (liveClassDetail == null || liveClassDetail.getData() == null) {
                    return;
                }
                //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (!liveClassDetail.getData().isLiveStatus()) {
                    String startTime = liveClassDetail.getData().getStartTime();
                    //startTime???????????????????????????????????????
                    if (!StringUtils.isEmpty(startTime)) {
                        startLiveTimeCountDown(startTime);
                    }
                }
                //??????????????????
                mediaController.setVideoName(liveClassDetail.getData().getName());
            }
        });

        //?????? ??????????????????????????????????????????????????????
        liveRoomDataManager.getFunctionSwitchVO().observe(((LifecycleOwner) getContext()), new Observer<PLVStatefulData<PolyvChatFunctionSwitchVO>>() {
            @Override
            public void onChanged(@Nullable PLVStatefulData<PolyvChatFunctionSwitchVO> chatFunctionSwitchStateData) {
                liveRoomDataManager.getFunctionSwitchVO().removeObserver(this);
                if (chatFunctionSwitchStateData == null || !chatFunctionSwitchStateData.isSuccess()) {
                    return;
                }
                PolyvChatFunctionSwitchVO functionSwitchVO = chatFunctionSwitchStateData.getData();
                if (functionSwitchVO == null || functionSwitchVO.getData() == null) {
                    return;
                }
                List<PolyvChatFunctionSwitchVO.DataBean> dataBeanList = functionSwitchVO.getData();
                if (dataBeanList == null) {
                    return;
                }
                for (PolyvChatFunctionSwitchVO.DataBean dataBean : dataBeanList) {
                    boolean isSwitchEnabled = dataBean.isEnabled();
                    switch (dataBean.getType()) {
                        //??????/????????????
                        case PolyvChatFunctionSwitchVO.TYPE_SEND_FLOWERS_ENABLED:
                            mediaController.setOnLikesSwitchEnabled(isSwitchEnabled);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }
    // </editor-fold>
}
