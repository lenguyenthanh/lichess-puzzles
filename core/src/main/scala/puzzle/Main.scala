import cats.effect.*
import skunk.*
import skunk.implicits.*
import skunk.codec.all.*
import natchez.Trace.Implicits.noop

object Main extends IOApp:

  val session: Resource[IO, Session[IO]] = Session.single( // (2)
    host = "localhost",
    port = 5432,
    user = "jimmy",
    database = "world",
    password = Some("banana"),
  )

  def run(args: List[String]): IO[ExitCode] = session.use: s => // (3)
    for
      d <- s.unique(sql"select current_date".query(date)) // (4)
      _ <- IO.println(s"The current date is $d.")
    yield ExitCode.Success
