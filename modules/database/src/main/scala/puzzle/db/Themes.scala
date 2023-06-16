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

trait Themes[F[_]]:
  def create(name: NonEmptyString): F[Unit]
  def byName(name: NonEmptyString): F[Option[Theme]]
  def list: F[List[Theme]]

case class ThemeExists(name: NonEmptyString) extends NoStackTrace

object Themes:
  def apply[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Themes[F] = new:
    import ThemeSQL.*

    def create(name: NonEmptyString): F[Unit] =
      postgres.use: session =>
        session.prepare(insert).flatMap: cmd =>
          cmd.execute(name).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            ThemeExists(name).raiseError

    def list: F[List[Theme]] = postgres.use: session =>
      session.execute(selectAll)

    def byName(name: NonEmptyString): F[Option[Theme]] = postgres.use: session =>
      session.prepare(selectByName).flatMap: cmd =>
        cmd.option(name)

private object ThemeSQL:
  val codec: Codec[Theme] = (themeId *: nonEmptyString).to[Theme]

  val insert: Command[NonEmptyString] =
    sql"""
         INSERT INTO theme (name)
         VALUES ($nonEmptyString)
       """.command

  val selectAll: Query[Void, Theme] =
    sql"""
         SELECT id, name FROM theme
       """.query(codec)

  val selectByName: Query[NonEmptyString, Theme] =
    sql"""
         SELECT id, name FROM theme
         WHERE name = $nonEmptyString
       """.query(codec)
