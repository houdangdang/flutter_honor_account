package com.honor.flutter_honor_account;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import java.security.PublicKey;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.hihonor.cloudservice.common.ApiException;
import com.hihonor.cloudservice.support.account.HonorIdSignInManager;
import com.hihonor.cloudservice.support.account.request.SignInOptionBuilder;
import com.hihonor.cloudservice.support.account.request.SignInOptions;
import com.hihonor.cloudservice.support.account.result.SignInAccountInfo;
import com.hihonor.cloudservice.support.api.entity.auth.Scope;
import com.hihonor.cloudservice.tasks.OnFailureListener;
import com.hihonor.cloudservice.tasks.OnSuccessListener;
import com.hihonor.cloudservice.tasks.Task;
import com.hihonor.honorid.core.helper.handler.ErrorStatus;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;

public class MethodCallHandlerImpl implements MethodCallHandler, ActivityResultListener {

    private static final String TAG = "MethodCallHandlerImpl";

    private static final int REQUEST_CODE_ACCOUNT = 1002;

    private final Gson mGson;

    private final Activity mActivity;

    private Result mAuthResult;

    public MethodCallHandlerImpl(Activity activity) {
        this.mActivity = activity;
        mGson = new GsonBuilder().create();
    }

    @Override
    public void onMethodCall(@NonNull final MethodCall call, @NonNull final Result result) {
        switch (call.method) {
            case "isLogin":
                isLogin(call, result);
                break;
            case "silentSignIn":
                silentSignIn(call, result);
                break;
            case "authorization":
                authorization(call, result);
                break;
            case "cancelAuthorization":
                cancelAuthorization(call, result);
                break;
            case "addAuthScopes":
                addAuthScopes(call, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public boolean onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_ACCOUNT) {
            // parse auth result of the callback after authorization
            Task<SignInAccountInfo> accountTask = HonorIdSignInManager.parseAuthResultFromIntent(resultCode, data);
            if (accountTask.isSuccessful()) {
                SignInAccountInfo signInAccountInfo = accountTask.getResult();
                mAuthResult.success(mGson.toJson(signInAccountInfo));
            } else {
                Exception exception = accountTask.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    mAuthResult.error(String.valueOf(apiException.getStatusCode()), apiException.getMessage(), null);
                }
            }
        }
        return false;
    }


    /**
     * Whether to log in
     *
     * @param call
     * @param result
     */
    private void isLogin(@NonNull final MethodCall call, @NonNull final Result result) {
        try {
            boolean login = HonorIdSignInManager.isLogin(mActivity);
            result.success(login);
        } catch (Exception e) {
            result.error(String.valueOf(ErrorStatus.ACCOUNT_NON_LOGIN), e.getMessage(), null);
        }
    }

    /**
     * silent sign in
     *
     * @param call
     * @param result
     */
    private void silentSignIn(@NonNull final MethodCall call, @NonNull final Result result) {
        final String appId = ValueGetter.getString("appId", call);

        SignInOptions signInOptions = new SignInOptionBuilder(SignInOptions.DEFAULT_AUTH_REQUEST_PARAM)
                .setClientId(appId)
                .createParams();

        HonorIdSignInManager.getService(mActivity, signInOptions)
                .silentSignIn()
                .addOnSuccessListener(new OnSuccessListener<SignInAccountInfo>() {
                    @Override
                    public void onSuccess(SignInAccountInfo signInAccountInfo) {
                        Log.d(TAG, "silent sign in success: " + mGson.toJson(signInAccountInfo));
                        result.success(mGson.toJson(signInAccountInfo));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        ApiException exception = (ApiException) e;
                        if ((exception.getStatusCode() == ErrorStatus.ERROR_SCOPES_NOT_AUTHORIZE)
                                || exception.getStatusCode() == ErrorStatus.ACCOUNT_NON_LOGIN) {
                            // If the silentSignIn interface return "55: scopes not authorize"
                            // or "31: Account hasnot login", then jump to the authorization page;
                            authorization(call, result);
                            return;
                        }
                        String message = exception.getMessage();
                        Log.e(TAG, "silent sign in error : " + message);
                        result.error(String.valueOf(exception.getStatusCode()), message, null);
                    }
                });
    }

    /**
     * jump to authorization page
     *
     * @param call
     * @param result
     */
    public void authorization(@NonNull final MethodCall call, @NonNull final Result result) {
        final String appId = ValueGetter.getString("appId", call);
        SignInOptions signInOptions = new SignInOptionBuilder(SignInOptions.DEFAULT_AUTH_REQUEST_PARAM)
                .setClientId(appId)
                .createParams();
        Intent signInIntent = HonorIdSignInManager.getService(mActivity, signInOptions).getSignInIntent();
        if (null == signInIntent) {
            result.error(String.valueOf(ErrorStatus.ERROR_HNID_IS_LOW_VERSION), "Honor version too low", null);
            return;
        }
        mAuthResult = result;
        mActivity.startActivityForResult(signInIntent, REQUEST_CODE_ACCOUNT);
    }

    /**
     * cancel authorization
     *
     * @param call
     * @param result
     */
    private void cancelAuthorization(@NonNull final MethodCall call, @NonNull final Result result) {

        final String appId = ValueGetter.getString("appId", call);

        SignInOptions signInOptions = new SignInOptionBuilder()
                .setClientId(appId)
                .createParams();

        HonorIdSignInManager.getService(mActivity, signInOptions)
                .cancelAuthorization()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "cancelAuthorization Success");
                        result.success(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "cancelAuthorization fail");
                        result.success(false);
                    }
                });
    }

    /**
     * add auth scopes
     *
     * @param call
     * @param result
     */
    private void addAuthScopes(@NonNull final MethodCall call, @NonNull final Result result) {
        mAuthResult = result;

        final String appId = ValueGetter.getString("appId", call);
        final List<String> inputScopes = call.argument("scopes");

        List<Scope> allowedScopes = Arrays.asList(SignInOptions.EMAIL, SignInOptions.OPENID, SignInOptions.PROFILE);
        List<Scope> filteredScopes = new ArrayList<>();

        for (String scope : inputScopes) {
            Scope currentScope = new Scope(scope);
            if (allowedScopes.contains(currentScope)) {
                filteredScopes.add(currentScope);
            }
        }
        SignInOptions accountAuthParams = new SignInOptionBuilder(SignInOptions.DEFAULT_AUTH_REQUEST_PARAM)
                .setClientId(appId)
                .setScopeList(filteredScopes)
                .createParams();
        HonorIdSignInManager.addAuthScopes(mActivity, REQUEST_CODE_ACCOUNT, accountAuthParams);
    }
}