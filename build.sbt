val Http4sVersion = "0.18.16"
val Specs2Version = "4.2.0"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "org.woodpigeon",
    name := "sql-search",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    scalacOptions += "-Ypartial-unification",
    libraryDependencies ++= Seq(
      "org.typelevel"   %% "cats-core" % "1.2.0",
      "org.typelevel"   %% "mouse"     % "0.18", 
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.specs2"      %% "specs2-core"          % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.tpolecat"    %% "doobie-core"         % "0.5.3",
      "com.outr" %% "lucene4s" % "1.8.1",
      "com.microsoft.sqlserver" % "mssql-jdbc" % "7.0.0.jre8"
    )
  )

