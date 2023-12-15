import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, itSettings, scalaSettings}

val appName = "if-proxy"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always // Resolves versions conflict

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(scalaSettings)
  .settings(defaultSettings())
  .settings(
    libraryDependencies ++= AppDependencies.appDependencies,
    scalacOptions += "-Wconf:src=routes/.*:s",
    PlayKeys.playDefaultPort := 8882
  )
  .settings(CodeCoverageSettings.settings)

lazy val it =
  (project in file("it"))
    .enablePlugins(PlayScala)
    .dependsOn(microservice % "test->test")
    .settings(itSettings)
