package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Surface;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.FileDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.extractor.mkv.MatroskaExtractor;

import java.io.File;
import java.io.FileDescriptor;

public class EdgeMediaExoPlayerDelegate implements EdgeMediaBridgeDelegate {

    private static final String TAG = "EdgeEXOPlayerBridge";
    private ExoPlayer mExoPlayer;
    private Context mContext;

    public EdgeMediaExoPlayerDelegate(Context context) {
        mContext = context;
    }

    @Override
    public void destroy() {
        getExoPlayer().setPlayWhenReady(false);
        getExoPlayer().release();
    }

    @Override
    public void setSurface(Surface surface) {
        getExoPlayer().setVideoSurface(surface);
    }

    @Override
    public void setPlaybackRate(double speed) {
        try {
            ExoPlayer player = getExoPlayer();
            PlaybackParameters parameters = player.getPlaybackParameters();
            player.setPlaybackParameters(new PlaybackParameters((float) speed, parameters.pitch));
        } catch (IllegalStateException | IllegalArgumentException ise) {
            Log.e(TAG, "Unable to set playback rate", ise);
        }
    }

    @Override
    public boolean prepareAsync() {
        try {
            getExoPlayer().prepare();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exo player prepare error: ", e);
            return false;
        }
    }

    @Override
    public boolean isPlaying() {
        return getExoPlayer().isPlaying();
    }

    @Override
    public long getCurrentPosition() {
        return getExoPlayer().getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return getExoPlayer().getDuration();
    }

    @Override
    public void release() {
        getExoPlayer().setPlayWhenReady(false);
        getExoPlayer().release();
    }

    @Override
    public void setVolume(double volume) {
        getExoPlayer().setVolume((float) volume);
    }

    @Override
    public void start() {
        getExoPlayer().setPlayWhenReady(true);
        getExoPlayer().play();
    }

    @Override
    public void pause() {
        getExoPlayer().pause();
    }

    @Override
    public void seekTo(int msec) {
        getExoPlayer().seekTo(msec);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public boolean setDataSource(String url, String cookies, String userAgent, boolean hideUrlLog) {
        MediaSource mediaSource =
                new ProgressiveMediaSource.Factory(
                        new DefaultDataSource.Factory(mContext))
                        .createMediaSource(MediaItem.fromUri(url));
        getExoPlayer().setMediaSource(mediaSource);
        return true;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public boolean setDataSourceFromFd(FileDescriptor fd, long offset, long length) {
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
//                Util.getUserAgent(mContext, "YourAppName"));
//
//        FileDataSource.Factory fileDataSourceFactory = new FileDataSource.Factory();
//        dataSourceFactory = new DefaultDataSourceFactory(mContext, null, fileDataSourceFactory);
//
//
//        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(fileDescriptor.getUri());
//
//
//        FileDescriptor fileDescriptor = ...; // Obtain your FileDescriptor
//        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(FileDataSource.buildFileDescriptorUri(fileDescriptor));
//
//
//        ParcelFileDescriptor parcelFileDescriptor = ...; // Obtain your FileDescriptor
//        Uri fileUri = Uri.fromFile(new File(parcelFileDescriptor.getFileDescriptor()));
//        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(fileUri);
//
//
//        MediaItem mediaItem = new MediaItem.Builder()
//                .setUri(Uri.EMPTY)
////                .setFileDescriptor(fd)
//                .setMimeType(MimeTypes.APPLICATION_MPD) // 根据媒体类型设置适当的MIME类型
//                .build();
        return false;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public boolean setDataUriDataSource(String url) {
        MediaSource mediaSource =
                new ProgressiveMediaSource.Factory(
                        new DefaultDataSource.Factory(mContext), MatroskaExtractor.FACTORY)
                        .createMediaSource(MediaItem.fromUri(url));
        getExoPlayer().setMediaSource(mediaSource);
        return true;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    private ExoPlayer getExoPlayer() {
        if (mExoPlayer == null) {
            mExoPlayer = new ExoPlayer.Builder(mContext).build();
        }
        return mExoPlayer;
    }
}
