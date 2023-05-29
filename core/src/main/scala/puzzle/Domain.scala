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

case class Puzzle(
    id: PuzzleId,
    fen: EpdFen,
    moves: List[Uci],
    rating: Int,
    ratingDeviation: Int,
    popularity: Int,
    nbPlays: Int,
    themes: List[NonEmptyString],
    openingTags: List[NonEmptyString],
) derives Codec.AsObject, Eq, Show

case class User(id: UserId, name: NonEmptyString) derives Codec.AsObject, Eq, Show

type PuzzleId = PuzzleId.Type
object PuzzleId extends Newtype[NonEmptyString]

type UserId = UserId.Type
object UserId extends Newtype[NonEmptyString]
