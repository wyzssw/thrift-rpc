namespace java com.justdebugit.thrift.generated


enum UserSource {
  WEIBO = 1,
  QQ = 2,
  WEIXIN = 3,
  RENREN = 4
}

struct UserInfo {
  1: i32 uid，
  2: optional string name，
  3: optional double score，
  4: optional UserSource source
}

service UserRepoService {
  UserInfo get(1: i32 uid),
  //注册用户
  void     put(1: i32 uid,2: UserInfo info)
  
}