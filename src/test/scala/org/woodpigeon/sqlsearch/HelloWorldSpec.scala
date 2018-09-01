package org.woodpigeon.sqlsearch

import cats.effect.IO
import com.outr.lucene4s.Lucene
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

import doobie.imports._
import cats.instances.int._
import cats.syntax.applicative._

class SqlSearchSpec extends Specification {

  "Searching by name" >> {

    import java.util.logging.{Logger, Level,ConsoleHandler}

    val logger = Logger.getLogger("com.microsoft.sqlserver.jdbc");
    logger.setLevel(Level.INFO);

    val handler = new ConsoleHandler()
    handler.setLevel(Level.INFO)
    logger.addHandler(handler)


    "queries registered tables" >> {
      val xa = Transactor.fromDriverManager[IO](
        driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        url = "jdbc:sqlserver://dev-sql-02.laterooms.io;databaseName=laterooms",
        user = "developer",
        pass = "")

      val query = sql"SELECT TOP 10000 Id, Name FROM laterooms.laterooms.Company".query[(Int, String)]
      val res = query.process.take(100).transact(xa).compile.toList.unsafeRunSync()
      println(res)


      import com.outr.lucene4s._

      val lucene = new DirectLucene(List("wibble"))



      val name = lucene.create.field[String]("name")

      res.foreach(r => lucene.doc().fields(name(r._2)).index())

      val p = lucene.query().search()
      println(p)


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
