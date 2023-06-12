package puzzle
package services

import skunk.*
import skunk.codec.all.*

import eu.timepit.refined.collection.NonEmpty

import Syntax.*
import eu.timepit.refined.collection.Empty
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.numeric.NonNegInt
import eu.timepit.refined.numeric.NonNegative

object Codecs:

  val nonEmptyString: Codec[NonEmptyString] = varchar.refine[NonEmpty]
  val nonNegInt: Codec[NonNegInt]           = int4.refine[NonNegative]
  val userId: Codec[UserId]                 = nonEmptyString.imap(UserId(_))(_.value)
  val themeId: Codec[ThemeId]               = nonNegInt.imap(ThemeId(_))(_.value)
