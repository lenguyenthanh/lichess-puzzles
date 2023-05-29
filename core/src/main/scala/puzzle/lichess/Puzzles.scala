package puzzle.lichess

import cats.effect.Concurrent
import fs2.{ Pipe, Stream }
import fs2.text.utf8

import org.http4s.{ Method, Request }
import org.http4s.client.Client
import org.http4s.implicits.*

import fs2.data.csv.decodeSkippingHeaders
import de.lhns.fs2.compress.Decompressor

trait Puzzles[F[_]]:
  def fetchAll: Stream[F, Puzzle]

object Puzzles:

  import Puzzle.given

  def make[F[_]: Concurrent](client: Client[F], decompressor: Decompressor[F]): Puzzles[F] = new:

    override def fetchAll: Stream[F, Puzzle] = client
      .stream(request)
      .switchMap(_.body)
      .through(convert)

    val request = Request[F](
      method = Method.GET,
      uri = uri"https://database.lichess.org/lichess_db_puzzle.csv.zst",
    )

    val defaultChunkSize = 1024 * 4

    def convert: Pipe[F, Byte, Puzzle] = _.through(decompressor.decompress)
      .through(utf8.decode)
      .through(decodeSkippingHeaders[Puzzle]())
