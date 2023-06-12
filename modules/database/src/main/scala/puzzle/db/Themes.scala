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
  def list: F[List[Theme]]

case class ThemeNameExists(name: NonEmptyString) extends NoStackTrace

object Themes:
  def instance[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Themes[F] = new:
    import ThemeSQL.*

    def create(name: NonEmptyString): F[Unit] =
      postgres.use: session =>
        session.prepare(insertTheme).flatMap: cmd =>
          cmd.execute(name).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            ThemeNameExists(name).raiseError

    def list: F[List[Theme]] = postgres.use: session =>
      session.execute(selectThemes)

private object ThemeSQL:
  val codec: Codec[Theme] = (themeId *: nonEmptyString).to[Theme]

  val insertTheme: Command[NonEmptyString] =
    sql"""
         INSERT INTO themes (name)
         VALUES ($nonEmptyString)
         ON CONFLICT DO NOTHING
       """.command

  val selectThemes: Query[Void, Theme] =
    sql"""
         SELECT * FROM themes
       """.query(codec)
