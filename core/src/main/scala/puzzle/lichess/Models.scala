package puzzle.lichess

import chess.*
import chess.format.*

type GameId = String

case class Clock(
    initial: Int,
    increment: Int,
    totalTime: Int,
)

case class Players(
    white: Player,
    black: Player,
)

case class Game(
    id: String,
    rated: Boolean,
    variant: String,
    speed: String,
    perf: String,
    createdAt: Long,
    lastMoveAt: Long,
    status: String,
    players: Players,
    winner: String,
    moves: String,
    tournament: String,
    clock: Clock,
)

case class User(
    name: String,
    id: String,
)

case class Player(
    user: User,
    rating: Int,
)

type PuzzleId = String

case class Puzzle(
    id: String,
    fen: EpdFen,
    moves: List[Uci],
    rating: Int,
    ratingDeviation: Int,
    popularity: Int,
    nbPlays: Int,
    themes: List[String],
    gameUrl: String,
    openingTags: List[String],
)

object Puzzle:

  import fs2.data.csv.*
  import fs2.data.csv.generic.semiauto.*
  import cats.syntax.all.*

  given CellDecoder[EpdFen]          = CellDecoder[String].map(EpdFen(_))
  given CellDecoder[List[String]]    = CellDecoder[String].map(_.split(' ').toList)
  given ucis: CellDecoder[List[Uci]] = CellDecoder[String].emap(Uci.readList(_).liftTo(DecoderError("Invalid ucis")))
  given RowDecoder[Puzzle]           = deriveRowDecoder
