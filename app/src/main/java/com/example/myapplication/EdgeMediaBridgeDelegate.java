package com.example.myapplication;

import android.media.MediaPlayer;
import android.view.Surface;

import java.io.FileDescriptor;

public interface EdgeMediaBridgeDelegate {
    public void destroy();
    public void setSurface(Surface surface);
    public void setPlaybackRate(double speed);
    public boolean prepareAsync();
    public boolean isPlaying();
    public long getCurrentPosition();
    public long getDuration();
    public void release();
    public void setVolume(double volume);
    public void start();
    public void pause();
    public void seekTo(int msec);
    public boolean setDataSource(String url, String cookies, String userAgent, boolean hideUrlLog);
    public boolean setDataSourceFromFd(FileDescriptor fd, long offset, long length);
    public boolean setDataUriDataSource(final String url);
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener);
    public void setOnErrorListener(MediaPlayer.OnErrorListener listener);
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener);
    public void setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener listener);
}
