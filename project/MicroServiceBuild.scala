import sbt._

object MicroServiceBuild extends Build with MicroService {

  val appName = "amls"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  private val microserviceBootstrapVersion = "6.9.0"
  private val playUrlBindersVersion = "2.1.0"
  private val domainVersion = "4.1.0"
  private val playReactivemongoVersion = "6.1.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "play-reactivemongo" % playReactivemongoVersion,
    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "play-url-binders" % playUrlBindersVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "org.typelevel" %% "cats" % "0.9.0",
    "com.eclipsesource" %% "play-json-schema-validator" % "0.8.8",
    "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.50.5" % "test,it",
    "com.beachape" %% "enumeratum" % "1.5.10"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  private val scalatestVersion = "2.2.6"
  private val scalatestPlusPlayVersion = "1.5.1"
  private val pegdownVersion = "1.6.0"
  private val hmrctestVersion = "2.3.0"

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest" %% "scalatest" % scalatestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
        "org.scalacheck" %% "scalacheck" % "1.12.5" % scope,
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

