import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings, targetJvm}

val appName = "if-proxy"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always // Resolves versions conflict

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(scalaSettings)
  .settings(defaultSettings())
  .settings(
    majorVersion        := 0,
    scalaVersion        := "2.13.12",
    targetJvm           := "jvm-11",
    libraryDependencies ++= AppDependencies.appDependencies,
    scalacOptions += "-Wconf:src=routes/.*:s",
    PlayKeys.playDefaultPort := 8882
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings())
  .settings(CodeCoverageSettings.settings)
