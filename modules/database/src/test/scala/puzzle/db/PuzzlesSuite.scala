package puzzle
package db
package tests

import chess.format.{ EpdFen, Uci }
import weaver.*
import eu.timepit.refined.types.string.NonEmptyString

object PuzzlesSuite extends SimpleIOSuite:

  val theme1  = NonEmptyString.unsafeFrom("theme1")
  val name2   = NonEmptyString.unsafeFrom("test2")
  val opening = OpeningId(NonEmptyString.unsafeFrom("French Defense"))

  val puzzle = Puzzle(
    id = PuzzleId(NonEmptyString.unsafeFrom("id")),
    fen = EpdFen("fen"),
    moves = List("e2e4", "e7e5").flatMap(Uci.apply),
    rating = 1990,
    ratingDeviation = 50,
    popularity = 90,
    nbPlays = 100,
    themes = List(theme1),
    openings = List(opening.value),
  )

  private def resource =
    Fixture.createRepositoryResource

  test("create success"):
    resource.use: resource =>
      for
        _ <- resource.openings.create(Opening(opening, name2))
        _ <- resource.themes.create(theme1)
        _ <- resource.puzzles.create(puzzle)
      yield expect(true)

  test("create and get"):
    resource.use: resource =>
      for
        _ <- resource.openings.create(Opening(opening, name2))
        _ <- resource.themes.create(theme1)
        _ <- resource.puzzles.create(puzzle)
        p <- resource.puzzles.byId(puzzle.id)
      yield expect.eql(p, puzzle)
