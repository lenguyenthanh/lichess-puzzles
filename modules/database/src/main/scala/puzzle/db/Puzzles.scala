package puzzle
package db

import cats.syntax.all.*
import cats.effect.kernel.Resource
import cats.effect.MonadCancelThrow

import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import Codecs.*
import eu.timepit.refined.types.string.NonEmptyString

trait Puzzles[F[_]]:
  def create(puzzle: Puzzle): F[Unit]

object Puzzles:
  def apply[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Puzzles[F] = new:
    import PuzzleSql.*

    def insert(cmd: PreparedCommand[F, NewPuzzle])(puzzle: NewPuzzle) =
      cmd.execute(puzzle).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            PuzzleIdExists(puzzle.id).raiseError

    def insert(cmd: PreparedCommand[F, List[(PuzzleId, OpeningId)]])(xs: List[(PuzzleId, OpeningId)]) =
      cmd.execute(xs).void

    def insert(cmd: PreparedCommand[F, (PuzzleId, List[NonEmptyString])])(id: PuzzleId, theme: List[NonEmptyString]) =
      cmd.execute(id, theme).void

    def create(puzzle: Puzzle) =
      postgres.use: s =>
        for
          puzzleCmd   <- s.prepare(insertPuzzle)
          openingsCmd <- s.prepare(insertOpenings(puzzle.openings.size))
          themesCmd   <- s.prepare(insertThemes(puzzle.themes.size))
          _ <- s.transaction.use: _ =>
            insert(puzzleCmd)(puzzle.toNewPuzzle) >>
              insert(openingsCmd)(puzzle.puzzleOpenings)
              >> insert(themesCmd)(puzzle.id, puzzle.themes)
        yield ()

private object PuzzleSql:

  val codec: Codec[NewPuzzle] =
    (puzzleId *: epdFen *: moves *: nonNegInt *: int4 *: nonNegInt *: nonNegInt).to[NewPuzzle]

  val insertPuzzle: Command[NewPuzzle] =
    sql"""
        INSERT INTO puzzle (id, fen, moves, rating, rating_deviation, popularity, play_times)
        VALUES ($codec)
       """.command

  def insertOpenings(n: Int): Command[List[(PuzzleId, OpeningId)]] =
    val xs = (puzzleId *: openingId).values.list(n)
    sql"""
        INSERT INTO puzzle_opening
        VALUES $xs
       """.command

  def insertThemes(n: Int): Command[(PuzzleId, List[NonEmptyString])] =
    val xs = nonEmptyString.values.list(n)
    sql"""
        INSERT INTO puzzle_theme
        SELECT $puzzleId, id FROM theme WHERE name in $xs
       """.command
