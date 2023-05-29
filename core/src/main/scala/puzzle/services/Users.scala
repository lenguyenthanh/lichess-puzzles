package puzzle
package services

import eu.timepit.refined.types.string.NonEmptyString
import skunk.*
import skunk.implicits.*
import Codecs.*

trait Users[F[_]]:
  def create(id: UserId, name: NonEmptyString): F[User]

private object userSQL:

  val codec: Codec[User] = (userId *: nonEmptyString).to[User]

  val insertUser: Command[User] = sql"""
        INSERT INTO brands
        VALUES ($codec)
        """.command
