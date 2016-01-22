package com.jacobandersson.dodskrok.cast;

import android.app.Activity;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.DataCastConsumer;

public class CCConsumer implements DataCastConsumer {

    private Activity activity;
    private Listener listener;

    public CCConsumer(Activity a, Listener l) {
        activity = a;
        listener = l;
    }

    @Override
    public void onApplicationConnected(ApplicationMetadata appMetadata, String applicationStatus, String sessionId, boolean wasLaunched) {
    }

    @Override
    public void onApplicationDisconnected(int errorCode) {
    }

    @Override
    public void onApplicationStopFailed(int errorCode) {

    }

    @Override
    public void onApplicationConnectionFailed(int errorCode) {

    }

    @Override
    public void onApplicationStatusChanged(String appStatus) {

    }

    @Override
    public void onVolumeChanged(double value, boolean isMute) {

    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        listener.onMessageReceived(castDevice, namespace, message);
    }

    @Override
    public void onMessageSendFailed(Status status) {
    }

    @Override
    public void onRemoved(CastDevice castDevice, String namespace) {
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onDisconnected() {
        listener.onDisconnected();
    }

    @Override
    public void onDisconnectionReason(@BaseCastManager.DISCONNECT_REASON int reason) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onCastDeviceDetected(MediaRouter.RouteInfo info) {
    }

    @Override
    public void onCastAvailabilityChanged(boolean castPresent) {
    }

    @Override
    public void onConnectivityRecovered() {
    }

    @Override
    public void onUiVisibilityChanged(boolean visible) {

    }

    @Override
    public void onReconnectionStatusChanged(int status) {

    }

    @Override
    public void onDeviceSelected(CastDevice device) {
    }

    @Override
    public void onFailed(int resourceId, int statusCode) {
    }

    public interface Listener {
        void onDisconnected();
        void onMessageReceived(CastDevice castDevice, String namespace, String message);
    }

}
