import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, itSettings, scalaSettings}

val appName = "if-proxy"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always // Resolves versions conflict

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"
ThisBuild / scalafixScalaBinaryVersion := "2.13"

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
  .settings(
    scalafmtFailOnErrors := true,
    Test / test := ((Test / test) dependsOn formatAll).value,
    formatAll := Def
      .sequential(
        scalafmtAll,
        Compile / scalafmtSbt,
        scalafixAll.toTask(""),
        (Compile / scalastyle).toTask("")
      )
      .value
  )
  .settings( // sbt-scalafix
    semanticdbEnabled := true, // enable SemanticDB
    semanticdbVersion := scalafixSemanticdb.revision, // only required for Scala 2.x
    scalacOptions += "-Ywarn-unused" // Scala 2.x only, required by `RemoveUnused`
  )

lazy val formatAll: TaskKey[Unit] = taskKey[Unit]("Run scalafmt for all files")

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings)
