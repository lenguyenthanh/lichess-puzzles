package puzzle
package db
package tests

import weaver.*
import eu.timepit.refined.types.string.NonEmptyString
import cats.kernel.Eq

object ThemesSuite extends SimpleIOSuite:

  given Eq[NonEmptyString] = Eq.fromUniversalEquals
  val name1                = NonEmptyString.unsafeFrom("test1")
  val name2                = NonEmptyString.unsafeFrom("test2")

  private def resource =
    Fixture.createRepositoryResource.map(_.themes)

  test("create theme success"):
    resource
      .use(_.create(name1).map(_ => expect(true)))

  test("insert a theme twice throw ThemExists"):
    resource.use: themes =>
      themes.create(name1) >> themes.create(name1).attempt.map:
        case Left(ThemeExists(x)) => expect.eql(x, name1)
        case _                    => expect(false)

  test("insert.get == Some"):
    resource.use: themes =>
      for
        _ <- themes.create(name1)
        o <- themes.byName(name1)
      yield matches(o):
        case Some(x) => expect.eql(x.name, name1)

  test("insert a two theme andThen list"):
    resource.use: themes =>
      for
        _      <- themes.create(name1)
        _      <- themes.create(name2)
        themes <- themes.list
      yield expect(themes.size == 2)
