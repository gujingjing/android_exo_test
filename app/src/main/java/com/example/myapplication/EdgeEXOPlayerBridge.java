package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;

import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.FileDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.extractor.mkv.MatroskaExtractor;

import java.io.FileDescriptor;

public class EdgeEXOPlayerBridge {

    private static final String TAG = "EdgeEXOPlayerBridge";
    private SimpleExoPlayer mSimpleExoPlayer;
    private ExoPlayer mExoPlayer;
    private Context mContext;

    public EdgeEXOPlayerBridge(Context context) {
        mContext = context;
    }

    public void destroy() {
    }

    public void setSurface(Surface surface) {
        getExoPlayer().setVideoSurface(surface);
    }

    public void setPlaybackRate(double speed) {
        try {
            ExoPlayer player = getExoPlayer();
            PlaybackParameters parameters = player.getPlaybackParameters();
            player.setPlaybackParameters(new PlaybackParameters((float) speed, parameters.pitch));
        } catch (IllegalStateException ise) {
            Log.e(TAG, "Unable to set playback rate", ise);
        } catch (IllegalArgumentException iae) {
            Log.e(TAG, "Unable to set playback rate", iae);
        }
    }

    public boolean prepareAsync() {
        try {
            getExoPlayer().prepare();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exo player prepare error: ", e);
            return false;
        }
    }

    public boolean isPlaying() {
        return getExoPlayer().isPlaying();
    }

    public long getCurrentPosition() {
        return getExoPlayer().getCurrentPosition();
    }

    public long getDuration() {
        return getExoPlayer().getDuration();
    }

    public void release() {
        getExoPlayer().release();
    }

    public void setVolume(double volume) {
        getExoPlayer().setVolume((float) volume);
    }

    public void start() {
        getExoPlayer().play();
    }

    public void pause() {
        getExoPlayer().pause();
    }

    public void seekTo(int msec) {
        getExoPlayer().seekTo(msec);
    }

    @SuppressLint("UnsafeOptInUsageError")
    public boolean setDataSource(String url, String cookies, String userAgent, boolean hideUrlLog) {
        MediaSource mediaSource =
                new ProgressiveMediaSource.Factory(
                        new DefaultDataSource.Factory(mContext), MatroskaExtractor.FACTORY)
                        .createMediaSource(MediaItem.fromUri(url));
        getExoPlayer().setMediaSource(mediaSource);
        return true;
    }

    @SuppressLint("UnsafeOptInUsageError")
    public boolean setDataSourceFromFd(FileDescriptor fd, long offset, long length) {

//        DataSource dataSource = new FileDataSource();
//
//        DataSpec dataSpec = new DataSpec(Uri.EMPTY, offset, length);
//
//        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSource)
//                .createMediaSource(dataSpec);
//        getExoPlayer().setMediaSource(mediaSource);
        return true;
    }

    @SuppressLint("UnsafeOptInUsageError")
    public boolean setDataUriDataSource(final String url) {
        MediaSource mediaSource =
                new ProgressiveMediaSource.Factory(
                        new DefaultDataSource.Factory(mContext), MatroskaExtractor.FACTORY)
                        .createMediaSource(MediaItem.fromUri(url));
        getExoPlayer().setMediaSource(mediaSource);
        return true;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        getExoPlayer().addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED && listener != null) {
                    listener.onCompletion(null);
                }
            }
        });
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        getExoPlayer().addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                if (listener != null) {
                    listener.onError(null, 0, 0);
                }
            }
        });
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        getExoPlayer().addListener(new Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY && listener != null) {
                    listener.onPrepared(null);
                }
            }
        });
    }

    public void setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener listener) {
        getExoPlayer().addListener(
                new Player.Listener() {
                    @Override
                    public void onVideoSizeChanged(VideoSize videoSize) {
                        if (listener != null) {
                            listener.onVideoSizeChanged(null, videoSize.width, videoSize.height);
                        }
                    }
                });
    }

//    public MediaPlayerBridge.AllowedOperations getAllowedOperations() {
//        ExoPlayer exoPlayer = getExoPlayer();
//        int playbackState = exoPlayer.getPlaybackState();
//
//        int currentWindowIndex = exoPlayer.getCurrentMediaItemIndex();
//
//        long currentPosition = exoPlayer.getCurrentPosition();
//
//        long duration = exoPlayer.getDuration();
//
//        boolean canSeekForward = false;
//
//        if (playbackState == Player.STATE_READY) {
//            if (currentWindowIndex < exoPlayer.getCurrentTimeline().getWindowCount() - 1) {
//                canSeekForward = true;
//            }
//        }
//
//        boolean canSeekBackward = false;
//
//        if (playbackState == Player.STATE_READY) {
//            if (currentWindowIndex > 0) {
//                canSeekBackward = true;
//            }
//        }
//
//        return null;
//    }

    private ExoPlayer getExoPlayer() {
        if (mExoPlayer == null) {
            mExoPlayer = new ExoPlayer.Builder(mContext).build();
        }
        return mExoPlayer;
    }
}