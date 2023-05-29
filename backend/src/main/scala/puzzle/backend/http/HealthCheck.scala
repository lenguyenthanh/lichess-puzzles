package puzzle
package backend.http

import cats.{ Eq, Order, Show }
import cats.derived.*
import cats.syntax.all.*
import cats.effect.{ Resource, Temporal }
import cats.effect.syntax.all.*

import io.circe.{ Codec, Decoder, Encoder }
import monocle.Iso

import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import scala.concurrent.duration.*
import scala.compiletime.summonAll
import scala.deriving.Mirror

//https://stackoverflow.com/questions/70802124/deserialize-enum-as-string-in-scala-3
inline def stringEnumDecoder[T](using m: Mirror.SumOf[T]): Decoder[T] =
  val elemInstances = summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]]
    .productIterator.asInstanceOf[Iterator[ValueOf[T]]].map(_.value)
  val elemNames = summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]]
    .productIterator.asInstanceOf[Iterator[ValueOf[String]]].map(_.value)
  val mapping = (elemNames zip elemInstances).toMap
  Decoder[String].emap { name =>
    mapping.get(name).fold(Left(s"Name $name is invalid value"))(Right(_))
  }

inline def stringEnumEncoder[T](using m: Mirror.SumOf[T]): Encoder[T] =
  val elemInstances = summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]]
    .productIterator.asInstanceOf[Iterator[ValueOf[T]]].map(_.value)
  val elemNames = summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]]
    .productIterator.asInstanceOf[Iterator[ValueOf[String]]].map(_.value)
  val mapping = (elemInstances zip elemNames).toMap
  Encoder[String].contramap[T](mapping.apply)

import HealthCheck.*
trait HealthCheck[F[_]]:
  def status: F[AppStatus]

object HealthCheck:
  def instance[F[_]: Temporal](postgres: Resource[F, Session[F]]): HealthCheck[F] = new:
    def status =
      val q = sql"SELECT pid FROM pg_stat_activity".query(int4)

      val postgresHealth: F[PostgresStatus] =
        postgres
          .use(_.execute(q))
          .map(_.nonEmpty)
          .timeout(1.second)
          .map(Status._Bool.reverseGet)
          .orElse(Status.Unreachable.pure[F].widen)
          .map(PostgresStatus(_))

      postgresHealth.map(AppStatus(_))

  enum Status derives Codec.AsObject, Eq, Order, Show:
    case Okay, Unreachable

  object Status:
    val _Bool: Iso[Status, Boolean] =
      Iso[Status, Boolean] {
        case Okay        => true
        case Unreachable => false
      }(if _ then Okay else Unreachable)

  given decoder: Decoder[Status] = stringEnumDecoder[Status]
  given encoder: Encoder[Status] = stringEnumEncoder[Status]

  type PostgresStatus = PostgresStatus.Type
  object PostgresStatus extends Newtype[Status]

  case class AppStatus(postgres: PostgresStatus) derives Codec.AsObject, Eq, Show
