name := """profilescalaexample"""

organization := "fr.tszym"

scalaVersion := "2.11.7"

libraryDependencies += "nl.grons" %% "metrics-scala" % "3.5.2_a2.3"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.0.0"

scalacOptions in Test ++= Seq("-Yrangepos")
