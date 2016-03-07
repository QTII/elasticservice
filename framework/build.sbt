val scalaVersionMajor = "2.11"

lazy val commonSettings = Seq(
  organization := "kr.co.qtii",
  name := "elasticservice",
  version := "0.1.1",
  scalaVersion := "2.11.7"
)

scalacOptions += "-feature"
//scalacOptions += "-unchecked"
scalacOptions += "-deprecation"

testOptions in Test +=
  Tests.Argument(
    TestFrameworks.ScalaCheck,
    "-maxDiscardRatio", "10",
    "-minSuccessfulTests", "100"
  )

lazy val elasticservice = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
        "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
        "com.typesafe.play" %% "play" % "2.4.6",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
        // "ch.qos.logback" % "logback-classic" % "1.1.5",
        
        "org.scalacheck" %% "scalacheck" % "1.12.5" % Test,
        
        "javax.servlet" % "javax.servlet-api" % "4.0.0-b01",
        jdbc
    )
  )
  
//retrieveManaged := true

lazy val cpJar = TaskKey[Unit]("cpJar")
def cpJarDef = cpJar <<= (crossTarget, scalaVersion, name, version) map {
  (out, scalaVer, n, v) =>
    val fname = (n+"_" + scalaVersionMajor + "-" + v + ".jar")
    val jarFile = out / fname
    IO.copyFile(jarFile, file("../sample-play-dev/lib") / fname, preserveLastModified = true)
}
cpJarDef
