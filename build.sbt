

scalaVersion := "2.12.17"
lazy val root = (project in file("."))
  .settings(
    name := """weather-scraper""",
    version := "0.9-SNAPSHOT",
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    ),
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.18.1",
      "mysql" % "mysql-connector-java" % "8.0.33",
      "com.typesafe.akka" %% "akka-actor" % "2.8.6",
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    ),
    mainClass in assembly := Some("com.Main"),
    assemblyJarName in assembly := "weather-scraper.jar",
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )