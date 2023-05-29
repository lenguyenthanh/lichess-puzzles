package puzzle

import cats.{ Eq, Show }

import chess.format.{ EpdFen, Uci }
import io.circe.Encoder
import io.circe.Decoder
import io.circe.DecodingFailure

object OrphanInstances:

  given Eq[Uci]   = Eq.by(_.uci)
  given Show[Uci] = Show.show(_.uci)

  given Eq[EpdFen]   = Eq.by[EpdFen, String](_.value)
  given Show[EpdFen] = Show.show[EpdFen](_.value)

  given Encoder[Uci] = Encoder[String].contramap(_.uci)

  given Decoder[Uci] = Decoder.instance: c =>
    summon[Decoder[String]](c) match
      case Right(t0) => Uci(t0) match
          case None        => Left(DecodingFailure(s"not an uci $t0", c.history))
          case r @ Some(t) => Right(t)
      case l @ Left(_) => l.asInstanceOf[Decoder.Result[Uci]]

  given Encoder[EpdFen] = Encoder[String].contramap(_.value)
  given Decoder[EpdFen] = Decoder[String].map(EpdFen(_))
