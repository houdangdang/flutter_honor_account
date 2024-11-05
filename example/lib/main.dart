import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_honor_account/flutter_honor_account.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  String _logMsg = '';
  String appId = '104437927';

  @override
  void initState() {
    super.initState();
  }

  void _isLogin() async {
    try {
      final result = await HonorAccountClient.isLogin();
      setState(() => _logMsg = '是否登录：${result ? "是": "否"}');
      debugPrint('$result');
    } on PlatformException catch (e) {
      setState(() => _logMsg = e?.message ?? '获取登录信息失败');
    }
  }

  void _silentSignIn() async {
    try {
      final result = await HonorAccountClient.silentSignIn(appId);
      setState(() => _logMsg = result.toJson());
      debugPrint(result.toJson());
    } on PlatformException catch (e) {
      setState(() => _logMsg = e?.message ?? '静默登录失败');
    }
  }

  void _authorization() async {
    try {
      final result = await HonorAccountClient.authorization(appId);
      setState(() => _logMsg = result.toJson());
      debugPrint(result.toJson());
    } on PlatformException catch (e) {
      setState(() => _logMsg = e?.message ?? '荣耀账号认证失败');
    }
  }

  void _cancelAuthorization() async {
    try {
      final result = await HonorAccountClient.cancelAuthorization(appId);
      setState(() => _logMsg = '取消荣耀账号认证：${result ? '成功': '失败'}');
      debugPrint('$result');
    } on PlatformException catch (e) {
      setState(() => _logMsg = e?.message ?? '取消荣耀账号认证失败');
    }
  }

  void _addAuthScopes() async {
    try {
      final result = await HonorAccountClient.addAuthScopes(appId, [
        HonorAccountClient.SCOPE_EMAIL,
        HonorAccountClient.SCOPE_OPENID,
        HonorAccountClient.SCOPE_PROFILE,
      ]);
      setState(() => _logMsg = result.toJson());
      debugPrint(result.toJson());
    } on PlatformException catch (e) {
      setState(() => _logMsg = e?.message ?? '添加授权范围失败');
    }
  }


  @override
  Widget build(BuildContext context) {
    final normalTextStyle = TextStyle(
      fontSize: 16,
      fontWeight: FontWeight.w500,
      color: Colors.blue[900],
    );
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter Honor Account Plugin Demo'),
        ),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(20),
          child: Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Visibility(
                  visible: _logMsg.isNotEmpty,
                  child: Container(
                    padding: const EdgeInsets.all(15),
                    color: Colors.grey[100],
                    child: Text(
                      _logMsg,
                      style: const TextStyle(
                        fontSize: 14,
                        fontWeight: FontWeight.w400,
                        color: Colors.black,
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                TextButton(
                  onPressed: () => _isLogin(),
                  child: Text(
                    'isLogin',
                    style: normalTextStyle,
                    textAlign: TextAlign.center,
                  ),
                ),
                const SizedBox(height: 20),
                TextButton(
                  onPressed: () => _silentSignIn(),
                  child: Text(
                    'silentSignIn',
                    style: normalTextStyle,
                    textAlign: TextAlign.center,
                  ),
                ),
                const SizedBox(height: 20),
                TextButton(
                  onPressed: () => _authorization(),
                  child: Text(
                    'authorization',
                    style: normalTextStyle,
                    textAlign: TextAlign.center,
                  ),
                ),
                const SizedBox(height: 20),
                TextButton(
                  onPressed: () => _cancelAuthorization(),
                  child: Text(
                    'cancelAuthorization',
                    style: normalTextStyle,
                    textAlign: TextAlign.center,
                  ),
                ),
                const SizedBox(height: 20),
                TextButton(
                  onPressed: () => _addAuthScopes(),
                  child: Text(
                    'addAuthScopes',
                    style: normalTextStyle,
                    textAlign: TextAlign.center,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
