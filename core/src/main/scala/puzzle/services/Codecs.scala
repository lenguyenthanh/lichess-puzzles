package puzzle
package services

import skunk.*
import skunk.codec.all.*

import eu.timepit.refined.collection.NonEmpty

import Syntax.*
import eu.timepit.refined.collection.Empty
import eu.timepit.refined.types.string.NonEmptyString

object Codecs:

  val nonEmptyString: Codec[NonEmptyString] = varchar.refine[NonEmpty]
  val userId: Codec[UserId]                 = nonEmptyString.imap(UserId(_))(_.value)
