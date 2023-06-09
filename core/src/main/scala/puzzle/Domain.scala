package puzzle

import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.cats.*
import io.circe.refined.*

import cats.{ Eq, Show }
import cats.derived.*
import cats.syntax.all.*
import io.circe.Codec

import chess.*
import chess.format.*

import OrphanInstances.given
import eu.timepit.refined.types.numeric.NonNegInt

case class Puzzle(
    id: PuzzleId,
    fen: EpdFen,
    moves: List[Uci],
    rating: Int,
    ratingDeviation: Int,
    popularity: Int,
    nbPlays: Int,
    themes: List[NonEmptyString],
    openings: List[NonEmptyString],
) derives Codec.AsObject, Eq, Show:

  def toNewPuzzle: NewPuzzle =
    NewPuzzle(
      id = id,
      fen = fen,
      moves = moves,
      rating = NonNegInt.unsafeFrom(rating),
      ratingDeviation = ratingDeviation,
      popularity = NonNegInt.unsafeFrom(popularity),
      nbPlays = NonNegInt.unsafeFrom(nbPlays),
    )

  def puzzleOpenings: List[(PuzzleId, OpeningId)] =
    openings.map(id -> OpeningId(_))

object Puzzle:
  def apply(p: NewPuzzle, o: List[OpeningId], t: List[NonEmptyString]): Puzzle =
    Puzzle(
      id = p.id,
      fen = p.fen,
      moves = p.moves,
      rating = p.rating.value,
      ratingDeviation = p.ratingDeviation,
      popularity = p.popularity.value,
      nbPlays = p.nbPlays.value,
      themes = t,
      openings = o.map(_.value),
    )

case class NewPuzzle(
    id: PuzzleId,
    fen: EpdFen,
    moves: List[Uci],
    rating: NonNegInt,
    ratingDeviation: Int,
    popularity: NonNegInt,
    nbPlays: NonNegInt,
) derives Eq, Show

case class Game(
    id: GameId,
    fen: EpdFen,
    moves: List[Uci],
    whiteId: UserId,
    blackId: UserId,
    winner: Option[Color],
    themes: List[NonEmptyString],
    openings: List[NonEmptyString],
) derives Codec.AsObject, Eq, Show

case class User(id: UserId, name: NonEmptyString) derives Codec.AsObject, Eq, Show

case class Theme(id: ThemeId, name: NonEmptyString) derives Codec.AsObject, Eq, Show

case class Opening(id: OpeningId, name: NonEmptyString) derives Codec.AsObject, Eq, Show

type PuzzleId = PuzzleId.Type
object PuzzleId extends Newtype[NonEmptyString]

type UserId = UserId.Type
object UserId extends Newtype[NonEmptyString]

// TODO: fixed length
type GameId = GameId.Type
object GameId extends Newtype[NonEmptyString]

type ThemeId = ThemeId.Type
object ThemeId extends Newtype[NonNegInt]

type OpeningId = OpeningId.Type
object OpeningId extends Newtype[NonEmptyString]
