ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion  = "2.7.0"
val SlickVersion = "3.4.1"
val dependencies = Seq(
  "com.typesafe.akka"     %% "akka-actor-typed"            % AkkaVersion,
  "com.typesafe.akka"     %% "akka-cluster-typed"          % AkkaVersion,
  "com.typesafe.akka"     %% "akka-persistence-typed"      % AkkaVersion,
  "com.typesafe.akka"     %% "akka-persistence-cassandra"  % "1.1.0",
  "com.typesafe.akka"     %% "akka-persistence"            % AkkaVersion,
  "com.typesafe.akka"     %% "akka-persistence-query"      % AkkaVersion,
  "com.typesafe.akka"     %% "akka-cluster-tools"          % AkkaVersion,
  "com.typesafe.akka"     %% "akka-cluster-sharding-typed" % AkkaVersion,
  "com.typesafe.akka"     %% "akka-stream-kafka"           % "4.0.0",
  "com.typesafe.akka"     %% "akka-stream"                 % AkkaVersion,
  "com.lightbend.akka"    %% "akka-projection-cassandra"   % "1.3.1",
  "com.typesafe.slick"    %% "slick"                       % SlickVersion,
  "com.typesafe.slick"    %% "slick-hikaricp"              % SlickVersion,
  "com.github.pureconfig" %% "pureconfig"                  % "0.17.2",
  "ch.qos.logback"         % "logback-classic"             % "1.4.5"

  //  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
//  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test
)
lazy val core = (project in file("trading-core"))
  .settings(name := "trading-core", libraryDependencies ++= dependencies)

lazy val server =
  (project in file("trading-api-server")).dependsOn(core).settings(name := "trading-api-server")
