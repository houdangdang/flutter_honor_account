// ignore_for_file: constant_identifier_names

part of flutter_honor_account;

/// Provides all APIs of Flutter Honor Account Plugin.
class HonorAccountClient {
  static const String SCOPE_OPENID = "openid";
  static const String SCOPE_PROFILE = "profile";
  static const String SCOPE_EMAIL = "email";

  static const MethodChannel _channel =
      MethodChannel('FlutterHonorAccountClient');

  /// 检测荣耀账号是否登录
  static Future<bool> isLogin() async {
    bool result = await _channel.invokeMethod('isLogin');
    return result;
  }

  /// 静默登录
  /// appId: 荣耀应用ID
  static Future<SignInAccountInfo> silentSignIn(String appId) async {
    final result = await _channel.invokeMethod(
      'silentSignIn',
      <String, String>{'appId': appId},
    );
    return SignInAccountInfo.fromJson(result);
  }

  /// 荣耀账号认证
  /// appId: 荣耀应用ID
  static Future<SignInAccountInfo> authorization(String appId) async {
    final result = await _channel.invokeMethod(
      'authorization',
      <String, String>{'appId': appId},
    );
    return SignInAccountInfo.fromJson(result);
  }

  /// 取消荣耀账号认证
  /// appId: 荣耀应用ID
  static Future<bool> cancelAuthorization(String appId) async {
    final result = await _channel.invokeMethod(
      'cancelAuthorization',
      <String, String>{'appId': appId},
    );
    return result;
  }


  /// 添加授权范围
  /// appId: 荣耀应用ID
  /// scopes: 范围列表【SCOPE_OPENID，SCOPE_PROFILE，SCOPE_EMAIL】只能为括号范围内的值
  static Future<SignInAccountInfo> addAuthScopes(
      String appId, List<String> scopes) async {
    final result = await _channel.invokeMethod(
      'addAuthScopes',
      <String, dynamic>{
        'appId': appId,
        'scopes': scopes,
      },
    );
    return SignInAccountInfo.fromJson(result);
  }
}
