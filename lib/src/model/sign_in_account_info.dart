part of flutter_honor_account;

class SignInAccountInfo {
  final int? accountFlag;
  final int? expirationTimeSecs;
  final List<dynamic>? extensionScopes;
  final String? idToken;
  final String? openId;
  final String? photoUriString;
  final String? serverAuthCode;
  final String? unionId;
  final String? email;

  SignInAccountInfo({
    this.accountFlag,
    this.expirationTimeSecs,
    this.extensionScopes,
    this.idToken,
    this.openId,
    this.photoUriString,
    this.serverAuthCode,
    this.unionId,
    this.email,
  });

  factory SignInAccountInfo.fromJson(String str) =>
      SignInAccountInfo.fromMap(json.decode(str));

  String toJson() => json.encode(toMap());

  factory SignInAccountInfo.fromMap(Map<String, dynamic> json) => SignInAccountInfo(
    accountFlag: json["accountFlag"],
    expirationTimeSecs: json["expirationTimeSecs"],
    extensionScopes: json["extensionScopes"] == null ? [] : List<dynamic>.from(json["extensionScopes"]!.map((x) => x)),
    idToken: json["idToken"],
    openId: json["openId"],
    photoUriString: json["photoUriString"],
    serverAuthCode: json["serverAuthCode"],
    unionId: json["unionId"],
    email: json["email"],
  );

  Map<String, dynamic> toMap() => {
    "accountFlag": accountFlag,
    "expirationTimeSecs": expirationTimeSecs,
    "extensionScopes": extensionScopes == null ? [] : List<dynamic>.from(extensionScopes!.map((x) => x)),
    "idToken": idToken,
    "openId": openId,
    "photoUriString": photoUriString,
    "serverAuthCode": serverAuthCode,
    "unionId": unionId,
    "email": email,
  };
}
