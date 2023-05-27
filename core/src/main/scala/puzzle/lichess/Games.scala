package puzzle.lichess

import cats.syntax.all.*
import cats.MonadThrow
import fs2.{Pipe, Stream}
import cats.effect.Temporal
import org.http4s.client.Client
import org.http4s.*
import org.http4s.headers.*
import org.http4s.implicits.*

import io.circe.generic.auto.*
import fs2.data.json.*
import fs2.data.json.circe.*

import scala.concurrent.duration.*
import scala.util.control.NoStackTrace

trait Games[F[_]]:
  def fetch(ids: List[GameId]): Stream[F, Game]

object Games:

  case class GameError(message: String) extends NoStackTrace

  def make[F[_]: Temporal, MonadThrow](client: Client[F]): Games[F] = new:

    override def fetch(ids: List[GameId]): Stream[F, Game] = client
      .stream(createRequest(ids))
      .through(handle429)
      .through(untilSome)
      .flatMap(_.bodyText)
      .through(tokens[F, String])
      .through(codec.deserialize[F, Game])

    def createRequest(ids: List[GameId]) = Request[F](
      method = Method.POST,
      uri = uri"https://lichess.org/api/games/export/_ids",
      headers = Headers(Accept(ndJson)),
    ).withEntity(ids.mkString(","))

    def untilSome[A]: Pipe[F, Option[A], A] = x =>
      (x ++ Stream.sleep(1.minute).as(none))
        .repeat
        .collectFirst { case (Some(x)) => x }

    def handle429: Pipe[F, Response[F], Option[Response[F]]] = _.evalMap: response =>
      if response.status == Status.TooManyRequests then none.pure[F]
      else if response.status.isSuccess then response.some.pure[F]
      else MonadThrow[F].raiseError(GameError(s"Unexpected status code: ${response.status}"))

  private val ndJson = MediaType("application", "x-ndjson", true, false, List("ndjson"))
