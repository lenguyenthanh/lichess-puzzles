package puzzle
package db
package tests

import weaver.*
import eu.timepit.refined.types.string.NonEmptyString

object OpeningsSuite extends SimpleIOSuite:

  val name1 = NonEmptyString.unsafeFrom("test1")
  val name2 = NonEmptyString.unsafeFrom("test2")

  private def resource =
    Fixture.createRepositoryResource.map(_.openings)

  test("create opening success"):
    resource
      .use(_.create(name1).map(_ => expect(true)))

  test("insert an opening twice throw OpeningExists"):
    resource.use: openings =>
      openings.create(name1) >> openings.create(name1).attempt.map:
        case Left(OpeningExists(_)) => expect(true)
        case _                      => expect(false)

  test("insert a two openings andThen get"):
    resource.use: openings =>
      for
        _  <- openings.create(name1)
        _  <- openings.create(name2)
        xs <- openings.list
      yield expect(xs.size == 2)
