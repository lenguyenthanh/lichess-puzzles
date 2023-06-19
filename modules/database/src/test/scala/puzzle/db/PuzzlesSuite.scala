package puzzle
package db
package tests

import chess.format.{ EpdFen, Uci }
import weaver.*
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.numeric.NonNegInt

object PuzzlesSuite extends SimpleIOSuite:

  val theme1 = NonEmptyString.unsafeFrom("theme1")
  val name2  = NonEmptyString.unsafeFrom("test2")

  val puzzle = NewPuzzle(
    id = PuzzleId(NonEmptyString.unsafeFrom("id")),
    fen = EpdFen("fen"),
    moves = List("e2e4", "e7e5").flatMap(Uci.apply),
    rating = NonNegInt.unsafeFrom(1990),
    ratingDeviation = 50,
    popularity = NonNegInt.unsafeFrom(90),
    nbPlays = NonNegInt.unsafeFrom(100),
  )

  private def resource =
    Fixture.createRepositoryResource.map(_.puzzles)

  test("create success"):
    resource.use(_.create(puzzle).map(_ => expect(true)))
