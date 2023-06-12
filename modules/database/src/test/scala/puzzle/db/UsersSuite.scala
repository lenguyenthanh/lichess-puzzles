package puzzle
package db
package tests

import weaver.*
import eu.timepit.refined.types.string.NonEmptyString

object UsersSuite extends SimpleIOSuite:

  val name1 = NonEmptyString.unsafeFrom("test1")
  val name2 = NonEmptyString.unsafeFrom("test2")
  val user1 = User(UserId(name1), name1)
  val user2 = User(UserId(name2), name2)

  private def resouce =
    Fixture.createRepositoryResource.map(_.users)

  test("create user success"):
    resouce
      .use(_.create(user1).map(_ => expect(true)))

  test("insert a user twice throw UserIdExist"):
    resouce.use: users =>
      users.create(user1) >> users.create(user1).attempt.map:
        case Left(UserIdExist(user1.id)) => expect(true)
        case _                           => expect(false)
