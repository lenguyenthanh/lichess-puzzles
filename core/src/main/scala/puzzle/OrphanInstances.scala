package puzzle

import cats.{Eq, Order, Show}

import chess.format.Uci
import io.circe.Codec

object OrphanInstances:

  given Eq[Uci]    = Eq.by(_.uci)
  given Show[Uci]  = Show.show(_.uci)

  given Eq[EpdFen]    = Eq.by(_.value)
  given Show[EpdFen]    = Eq.by(_.value)

  // given Codec[Uci] = Codec.from(Codec[String], Codec[String].map(Uci.apply))
