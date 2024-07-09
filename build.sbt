import uk.gov.hmrc.DefaultBuildSettings.itSettings

val appName = "if-proxy"

ThisBuild / scalaVersion := "3.4.2"
ThisBuild / majorVersion := 1

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(
    PlayKeys.playDefaultPort := 8882,
    libraryDependencies ++= AppDependencies.appDependencies
  )

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings())
