import uk.gov.hmrc.DefaultBuildSettings.itSettings

val appName = "if-proxy"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always // Resolves versions conflict

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"
ThisBuild / scalafixScalaBinaryVersion := "2.13"

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
