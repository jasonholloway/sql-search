package org.woodpigeon.sqlsearch

import cats.Monad
import cats.data.Kleisli
import cats.free.Free
import com.outr.lucene4s.Lucene
import com.outr.lucene4s.document.DocumentBuilder
import doobie.util.transactor.Transactor
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import com.outr.lucene4s._
import cats.instances.int._
import cats.syntax.applicative._
import doobie.imports._
// import doobie.syntax.stream._
import cats.effect.IO
import cats.effect.IO._
import fs2.Stream


trait Strategy {
  type Deps = (Transactor[IO], Lucene)

  def updateAll: Deps => Stream[IO, Any]
  def updateSome(ids: Set[Int]): Deps => Stream[IO, Any]
}

//again, here we are: LuceneIO
//is a clump of real activity,
//but taking some lucene kinda thing as its input

object companyStrategy extends Strategy {

  def updateAll(): Deps => Stream[IO, Any] = {
    case (xa, lucene) => 
      val name = lucene.create.field[String]("name")
      sql"SELECT Id, Name FROM laterooms.laterooms.Company"
        .query[(Int, String)]
        .stream.transact(xa)
        .flatMap {
          case (_, n) =>
            Stream.eval(IO { println(n); lucene.doc().fields(name(n)).index() })
        }
        .take(50)
  }

  def updateSome(ids: Set[Int]): Deps => Stream[IO, Any] = {
    case (xa, lucene) => 
      ???
  }
}


class SqlSearchSpec extends Specification {

  
  "Searching by name" >> {

    // import java.util.logging.{Logger, Level,ConsoleHandler}

    // val logger = Logger.getLogger("com.microsoft.sqlserver.jdbc");
    // logger.setLevel(Level.FINER);

    // val handler = new ConsoleHandler()
    // handler.setLevel(Level.FINER)
    // logger.addHandler(handler)


    "queries registered tables" >> {

      import cats.free.Free.catsFreeMonadForFree

      val xa = Transactor.fromDriverManager[IO](
        driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        url = "jdbc:sqlserver://dev-sql-02.laterooms.io;databaseName=laterooms",
        user = System.getenv("DB_USER"),
        pass = System.getenv("DB_PASS"))

      val lucene = new DirectLucene(List("wibble"))

      val out = companyStrategy
        .updateAll()(xa, lucene)
        .run.unsafeRunSync()

      val p = lucene.query().search()
      println(p.results)

      true
    }
  }

}



// class HelloWorldSpec extends org.specs2.mutable.Specification {

//   "HelloWorld" >> {
//     "return 200" >> {
//       uriReturns200()
//     }
//     "return hello world" >> {
//       uriReturnsHelloWorld()
//     }
//   }

//   private[this] val retHelloWorld: Response[IO] = {
//     val getHW = Request[IO](Method.GET, Uri.uri("/hello/world"))
//     new HelloWorldService[IO].service.orNotFound(getHW).unsafeRunSync()
//   }

//   private[this] def uriReturns200(): MatchResult[Status] =
//     retHelloWorld.status must beEqualTo(Status.Ok)

//   private[this] def uriReturnsHelloWorld(): MatchResult[String] =
//     retHelloWorld.as[String].unsafeRunSync() must beEqualTo("{\"message\":\"Hello, world\"}")
// }
