package com.honor.flutter_honor_account;

import android.app.Activity;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;

/**
 * FlutterHonorAccountPlugin
 */
public class FlutterHonorAccountPlugin implements FlutterPlugin, ActivityAware {

    private MethodChannel mMethodChannel;
    private MethodCallHandlerImpl mMethodCallHandler;

    private void onAttachedToEngine(@NonNull BinaryMessenger messenger) {
        mMethodChannel = new MethodChannel(messenger, "FlutterHonorAccountClient");
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        onAttachedToEngine(binding.getBinaryMessenger());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        mMethodChannel = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        final Activity activity = binding.getActivity();
        mMethodCallHandler = new MethodCallHandlerImpl(activity);
        mMethodChannel.setMethodCallHandler(mMethodCallHandler);
        binding.addActivityResultListener(mMethodCallHandler);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        mMethodChannel.setMethodCallHandler(null);
        mMethodCallHandler = null;
    }
}