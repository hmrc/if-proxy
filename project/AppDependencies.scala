import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.0.0"

  private val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion
  )

  private val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}
