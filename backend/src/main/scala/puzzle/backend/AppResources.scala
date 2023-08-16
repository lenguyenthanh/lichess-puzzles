package puzzle.backend

import org.typelevel.log4cats.Logger
import cats.effect.*

import cats.effect.std.Console
import fs2.io.net.Network
import puzzle.db.DbResources
import puzzle.db.PostgresConfig

class AppResources[F[_]] private (val db: DbResources[F])

object AppResources:

  def instance[F[_]: Async: Network: Console: Logger](postgresConf: PostgresConfig): Resource[F, AppResources[F]] =
    DbResources.instance(postgresConf).map(AppResources(_))
