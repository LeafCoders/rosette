// Adds a user with username "admin" and password "password" with full permissions.

db.users.insert({
  "_id" : "4711",
  "username" : "admin",
  "hashedPassword" : "$shiro1$SHA-256$1$+0r1YFBHm551/AhX82PyRw==$tL6Nr6lTqnRCX7Tg4V6OMmNLESoLhdzkqW9OklXcKPk=",
  "status" : "active",
  "firstName" : "Admin",
  "lastName" : "Admin",
  "email" : ""
});

db.permissions.insert({
  "user" : { "id" : "4711" },
  "patterns" : [ "*" ]
});
