package puzzle
package db

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

class DbResources[F[_]] private (
    val postgres: Resource[F, Session[F]],
    val flyway: Flyway[F],
)

object DbResources:

  def instance[F[_]: Async: Network: Console: Logger](postgresConf: PostgresConfig): Resource[F, DbResources[F]] =

    def checkPostgresConnection(postgres: Resource[F, Session[F]]): F[Unit] =
      postgres.use: session =>
        session.unique(sql"select version();".query(text)).flatMap: v =>
          Logger[F].info(s"Connected to Postgres $v")

    def mkPostgresResource(c: PostgresConfig): SessionPool[F] =
      Session
        .pooled[F](
          host = c.host.value,
          port = c.port.value,
          user = c.user.value,
          password = Some(c.password.value),
          database = c.database.value,
          max = c.max.value,
        ).evalTap(checkPostgresConnection)

    (mkPostgresResource(postgresConf), Flyway.module[F](postgresConf.toFlywayConfig)).parMapN(DbResources(_, _))
