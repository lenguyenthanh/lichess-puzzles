package puzzle.backend

import org.typelevel.log4cats.Logger
import cats.effect.*

sealed trait AppResources[F[_]](val flyway: Flyway[F])

object AppResources:

  def instance[F[_]: Async: Logger](flywayConfig: FlywayConfig): Resource[F, AppResources[F]] =
    Flyway.module[F](flywayConfig).map(new AppResources[F](_) {})
