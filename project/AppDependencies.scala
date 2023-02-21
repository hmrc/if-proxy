import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.13.0"

  // Test dependencies
  private val scalatestVersion = "3.2.15"
  private val flexMarkVersion = "0.64.0"

  private val compile = Seq(
    "uk.gov.hmrc"          %% "bootstrap-backend-play-28" % bootstrapVersion
  )

  private val testScope = "test, it"

  private def test = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-28"    % bootstrapVersion % testScope,
    "org.scalatest"        %% "scalatest"                 % scalatestVersion % testScope,
    "com.vladsch.flexmark" % "flexmark-all"               % flexMarkVersion  % testScope // for scalatest 3.2+
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}
