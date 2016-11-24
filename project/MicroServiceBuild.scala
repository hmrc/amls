import _root_.sbt._

object MicroServiceBuild extends Build with MicroService {

  val appName = "amls"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  private val playHealthVersion = "2.0.0"
  private val microserviceBootstrapVersion = "5.7.0"
  private val playAuthorisationVersion = "4.2.0"
  private val playUrlBindersVersion = "1.0.0"
  private val playConfigVersion = "3.0.0"
  private val playJsonLoggerVersion = "3.0.0"
  private val domainVersion = "4.0.0"
  private val metricsGraphiteVersion = "3.0.2"
  private val validationVersion = "1.1"
  private val playReactivemongoVersion = "5.1.0"
  private val playJars = ExclusionRule(organization = "com.typesafe.play")

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "play-health" % playHealthVersion,
    "uk.gov.hmrc" %% "microservice-bootstrap" % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "play-authorisation" % playAuthorisationVersion,
    "uk.gov.hmrc" %% "play-url-binders" % playUrlBindersVersion,
    "uk.gov.hmrc" %% "play-config" % playConfigVersion,
    "uk.gov.hmrc" %% "play-json-logger" % playJsonLoggerVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "com.codahale.metrics" % "metrics-graphite" % metricsGraphiteVersion,
    "uk.gov.hmrc" %% "play-reactivemongo" % playReactivemongoVersion,

    "io.github.jto" %% "validation-core" % validationVersion excludeAll playJars,
    "io.github.jto" %% "validation-json" % validationVersion excludeAll playJars,
    "io.github.jto" %% "validation-form" % validationVersion excludeAll playJars
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  private val scalatestVersion = "2.2.6"
  private val scalatestPlusPlayVersion = "1.5.1"
  private val pegdownVersion = "1.6.0"
  private val hmrctestVersion = "2.1.0"

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest" %% "scalatest" % scalatestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
        "uk.gov.hmrc" %% "hmrctest" % hmrctestVersion % scope,
         "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-core" % "1.9.5" % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "org.scalatest" %% "scalatest" % scalatestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "uk.gov.hmrc" %% "hmrctest" % hmrctestVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-core" % "1.9.5" % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}

