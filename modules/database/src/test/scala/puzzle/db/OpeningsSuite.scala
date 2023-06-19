package puzzle
package db
package tests

import weaver.*
import eu.timepit.refined.types.string.NonEmptyString

object OpeningsSuite extends SimpleIOSuite:

  val name1    = NonEmptyString.unsafeFrom("test1")
  val opening1 = Opening(OpeningId(name1), name1)
  val name2    = NonEmptyString.unsafeFrom("test2")
  val opening2 = Opening(OpeningId(name2), name2)

  private def resource =
    Fixture.createRepositoryResource.map(_.openings)

  test("create opening success"):
    resource
      .use(_.create(opening1).map(_ => expect(true)))

  test("insert an opening twice throw OpeningExists"):
    resource.use: openings =>
      openings.create(opening1) >> openings.create(opening1).attempt.map:
        case Left(OpeningIdExists(id)) => expect.eql(id, opening1.id)
        case _                         => expect(false)

  test("insert a two openings andThen get"):
    resource.use: openings =>
      for
        _  <- openings.create(opening1)
        _  <- openings.create(opening2)
        xs <- openings.list
      yield expect(xs.size == 2)
