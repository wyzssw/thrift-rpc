include "user.thrift"

namespace java com.justdebugit.thrift.generated

const i32 INT32CONSTANT = 9853
const list<uid> BLACK_LIST = {10,11,12}

enum Category {
  COLLEAGUE = 1,
  CLASSMATE = 2,
  FAMILY = 3
}

struct Friend {
  1: i32 uid,
  2: optional user.UserInfo userInfo,
  3: optional Category cat = Category.CLASSMATE,
  4: optional string   beizhu
}

exception DifferentSourceDetectedException {
  1: i32   uid1,
  2: i32   uid2,
  2: string msg
}

service UserManagerService extends user.UserRepoService {

   //获取我的好友列表
   list<UserInfo> getMyFriends(1:i32 uid),
   
   //拉黑
   oneway void defriend(1:i32 uid,2:i32 uid),

   //添加好友
   bool addfriend(1:i32 uid, 2:i32 uid),

   //好友PK
   i32 compare(1:i32 uid, 2:i32 uid) throws (1:DifferentSourceDetectedException dsde)
   
}

