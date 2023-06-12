package puzzle.backend

import org.typelevel.log4cats.Logger
import cats.effect.*

import skunk.*
import skunk.codec.text.*
import skunk.implicits.*
import cats.*
import cats.syntax.all.*
import cats.effect.std.Console
import fs2.io.net.Network
import natchez.Trace.Implicits.noop
import puzzle.db.DbResources
import puzzle.db.PostgresConfig

class AppResources[F[_]] private (val db: DbResources[F])

object AppResources:

  def instance[F[_]: Async: Network: Console: Logger](postgresConf: PostgresConfig): Resource[F, AppResources[F]] =
    DbResources.instance(postgresConf).map(AppResources(_))
