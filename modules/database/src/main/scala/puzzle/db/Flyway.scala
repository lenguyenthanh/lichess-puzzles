package puzzle
package db

import cats.syntax.all.*
import cats.effect.*

import fly4s.core.*
import fly4s.core.data.*
import org.typelevel.log4cats.Logger
import org.flywaydb.core.api.output.ValidateOutput
import cats.data.NonEmptyList

trait Flyway[F[_]]:
  def migrate: F[Unit]

object Flyway:

  def instance[F[_]: Async: Logger](fly4s: Fly4s[F]): Flyway[F] = new:

    def migrate =
      for
        _      <- Logger[F].info("Running Flyway migration")
        result <- fly4s.migrate
        _      <- Logger[F].info(s"Flyway migration result: $result")
      yield ()

  def module[F[_]: Async: Logger](config: FlywayConfig): Resource[F, Flyway[F]] = Fly4s
    .make[F](
      url = config.url,
      user = config.user,
      password = config.password,
      config = Fly4sConfig(
        table = config.migrationsTable,
        locations = Location.of(config.migrationsLocations),
      ),
    )
    .map(instance[F])

  def showError: NonEmptyList[fly4s.core.data.ValidateOutput] => String = _.map(_.showError).toList.mkString("\n")

  extension (vo: ValidateOutput)
    def showError: String =
      s"Invalid migration: {version: ${vo.version}, description: ${vo.description}, script: ${vo.filepath}, details: ${vo.errorDetails.nn.errorMessage}"
