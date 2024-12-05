package com.steelkiwi.cropiwa.image;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

/**
 * @author yarolegovich
 * 25.02.2017.
 */
public class CropIwaResultReceiver extends BroadcastReceiver {

    private static final String ACTION_CROP_COMPLETED = "cropIwa_action_crop_completed";
    private static final String EXTRA_ERROR = "extra_error";
    private static final String EXTRA_URI = "extra_uri";

    public static void onCropCompleted(Context context, Uri croppedImageUri) {
        Intent intent = new Intent(ACTION_CROP_COMPLETED)
                .setPackage(context.getPackageName());
        intent.putExtra(EXTRA_URI, croppedImageUri);
        context.sendBroadcast(intent);
    }

    public static void onCropFailed(Context context, Throwable e) {
        Intent intent = new Intent(ACTION_CROP_COMPLETED)
                .setPackage(context.getPackageName());
        intent.putExtra(EXTRA_ERROR, e);
        context.sendBroadcast(intent);
    }

    private Listener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) extras = new Bundle();
        if (listener != null) {
            if (extras.containsKey(EXTRA_ERROR)) {
                listener.onCropFailed((Throwable) extras.getSerializable(EXTRA_ERROR));
            } else if (extras.containsKey(EXTRA_URI)) {
                listener.onCropSuccess(extras.getParcelable(EXTRA_URI));
            }
        }
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter(ACTION_CROP_COMPLETED);
        ContextCompat.registerReceiver(context, this, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    public void unregister(Context context) {
        try {
            context.unregisterReceiver(this);
        } catch (Exception exception) {
            // Fail silently
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onCropSuccess(Uri croppedUri);

        void onCropFailed(Throwable e);
    }
}
