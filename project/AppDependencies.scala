import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.2.0"

  private val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion
  )

  private val testScope = "test, it"

  private def test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30"    % bootstrapVersion % testScope
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}
