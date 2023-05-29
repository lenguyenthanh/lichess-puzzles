package puzzle.lichess

import cats.syntax.all.*
import cats.effect.Temporal
import fs2.{ Pipe, Stream }

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

  case class FetchGameError(message: String) extends NoStackTrace

  def make[F[_]: Temporal](client: Client[F]): Games[F] = new:

    override def fetch(ids: List[GameId]): Stream[F, Game] = client
      .stream(createRequest(ids))
      .through(handleStatusCode)
      .through(untilSuccess)
      .flatMap(_.bodyText)
      .through(tokens[F, String])
      .through(codec.deserialize[F, Game])

    def createRequest(ids: List[GameId]) = Request[F](
      method = Method.POST,
      uri = uri"https://lichess.org/api/games/export/_ids",
      headers = Headers(Accept(ndJson)),
    ).withEntity(ids.mkString(","))

    def untilSuccess[A]: Pipe[F, Option[A], A] = x =>
      (x ++ Stream.sleep(1.minute).as(none))
        .repeat
        .collectFirst:
          case Some(x) => x

    def handleStatusCode: Pipe[F, Response[F], Option[Response[F]]] = _.evalMap: response =>
      if response.status.isSuccess then response.some.pure[F]
      else if response.status == Status.TooManyRequests then none.pure[F]
      else FetchGameError(s"Unexpected status code: ${response.status}").raiseError

  private val ndJson = MediaType("application", "x-ndjson", true, false, List("ndjson"))
