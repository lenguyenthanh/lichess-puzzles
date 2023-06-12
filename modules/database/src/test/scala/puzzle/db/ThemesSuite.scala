package puzzle
package db
package tests

import weaver.*
import eu.timepit.refined.types.string.NonEmptyString

object ThemesSuite extends SimpleIOSuite:

  val name1 = NonEmptyString.unsafeFrom("test1")
  val name2 = NonEmptyString.unsafeFrom("test2")

  test("create theme success"):
    Fixture.createRepositoryResource.map(_.themes)
      .use(_.create(name1).map(_ => expect(true)))

  test("insert a theme twice throw ThemExists"):
    Fixture.createRepositoryResource.map(_.themes).use: themes =>
      themes.create(name1) >> themes.create(name1).attempt.map:
        case Left(ThemeExists(themeName)) => expect(true)
        case _                            => expect(false)

  test("insert a two theme andThen get"):
    Fixture.createRepositoryResource.map(_.themes).use: themes =>
      for
        _      <- themes.create(name1)
        _      <- themes.create(name2)
        themes <- themes.list
      yield expect(themes.size == 2)
