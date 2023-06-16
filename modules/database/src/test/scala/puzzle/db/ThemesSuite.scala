package puzzle
package db
package tests

import weaver.*
import eu.timepit.refined.types.string.NonEmptyString

object ThemesSuite extends SimpleIOSuite:

  val name1 = NonEmptyString.unsafeFrom("test1")
  val name2 = NonEmptyString.unsafeFrom("test2")

  private def resource =
    Fixture.createRepositoryResource.map(_.themes)

  test("create theme success"):
    resource
      .use(_.create(name1).map(_ => expect(true)))

  test("insert a theme twice throw ThemExists"):
    resource.use: themes =>
      themes.create(name1) >> themes.create(name1).attempt.map:
        case Left(ThemeExists(_)) => expect(true)
        case _                    => expect(false)

  test("insert a two theme andThen get"):
    resource.use: themes =>
      for
        _      <- themes.create(name1)
        _      <- themes.create(name2)
        themes <- themes.list
      yield expect(themes.size == 2)
