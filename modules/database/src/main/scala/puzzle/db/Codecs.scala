package puzzle
package db

import skunk.*
import skunk.codec.all.*

import eu.timepit.refined.collection.NonEmpty

import chess.format.*
import Syntax.*
import eu.timepit.refined.collection.Empty
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.numeric.NonNegInt
import eu.timepit.refined.numeric.NonNegative

object Codecs:

  val nonEmptyString: Codec[NonEmptyString] = text.refine[NonEmpty]
  val nonNegInt: Codec[NonNegInt]           = int4.refine[NonNegative]
  val userId: Codec[UserId]                 = nonEmptyString.imap(UserId(_))(_.value)
  val themeId: Codec[ThemeId]               = nonNegInt.imap(ThemeId(_))(_.value)
  val puzzleId: Codec[PuzzleId]             = nonEmptyString.imap(PuzzleId(_))(_.value)
  val openingId: Codec[OpeningId]           = nonEmptyString.imap(OpeningId(_))(_.value)

  val epdFen: Codec[EpdFen] = text.imap(EpdFen(_))(_.value)

  // TODO: find a better way to encode/decode moves
  val moves: Codec[List[Uci]] = text.imap(_.split(' ').toList.flatMap(Uci.apply))(_.map(_.uci).mkString(" "))
