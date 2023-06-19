package puzzle
package db

import scala.util.control.NoStackTrace
import cats.syntax.all.*
import cats.effect.kernel.Resource
import cats.effect.MonadCancelThrow
import eu.timepit.refined.types.string.NonEmptyString

import skunk.*
import skunk.implicits.*

import Codecs.*

trait Games[F[_]]:
  def create(name: NonEmptyString): F[Unit]
  def byName(name: NonEmptyString): F[Option[Theme]]
  def list: F[List[Theme]]
