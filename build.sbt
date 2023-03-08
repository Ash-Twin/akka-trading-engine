ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion = "2.7.0"
val SlickVersion = "3.4.1"
val dependencies = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.0",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.lightbend.akka" %% "akka-projection-cassandra" % "1.3.1",
  "com.typesafe.slick" %% "slick" % SlickVersion,
  "org.slf4j" % "slf4j-nop" % "2.0.5",
  "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion
//  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
//  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test
)
lazy val core = (project in file("trading-core"))
  .settings(
    name := "trading-core"
  )

lazy val server = (project in file("trading-api-server"))
  .settings(
    name := "trading-api-server"
  )