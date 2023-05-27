package puzzle.lichess

import chess.*
import chess.format.*

type GameId = String

case class Clock(
    initial: Int,
    increment: Int,
    totalTime: Int
)

case class Players(
    white: Player,
    black: Player
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
    clock: Clock
)

case class User(
    name: String,
    id: String
)

case class Player(
    user: User,
    rating: Int
)


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
    openingTags: List[String]
)
