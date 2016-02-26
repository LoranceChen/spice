name := "spice"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing" % "2.0.0-M2",
  "com.netflix.rxjava" % "rxjava-scala" % "0.20.7",
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.typesafe.akka" %% "akka-remote" % "2.4.1",
  "org.scala-lang" % "scala-actors" % "2.11.7",

  "org.scala-lang" % "scala-reflect" % "2.11.7",

  "net.liftweb" %% "lift-json" % "3.0-M8"
)