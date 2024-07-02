import uk.gov.hmrc.DefaultBuildSettings.itSettings

val appName = "if-proxy"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always // Resolves versions conflict

ThisBuild / scalaVersion := "3.3.3"
ThisBuild / majorVersion := 1

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(
    PlayKeys.playDefaultPort := 8882,
    scalacOptions += "-Wconf:src=routes/.*:s",
    libraryDependencies ++= AppDependencies.appDependencies
  )

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings())
