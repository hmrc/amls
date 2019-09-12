import sbt._

object MicroServiceBuild extends Build with MicroService {

  val appName = "amls"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  private val domainVersion = "5.2.0"
  private val playReactivemongoVersion = "6.2.0"
  private val authVersion = "2.21.0-play-25"


  val compile = Seq(
    "uk.gov.hmrc" %% "play-reactivemongo" % playReactivemongoVersion,
    ws,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "org.typelevel" %% "cats" % "0.9.0",
    "com.eclipsesource" %% "play-json-schema-validator" % "0.8.8",
    "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.50.5" % "test,it",
    "com.beachape" %% "enumeratum" % "1.5.10",
    "uk.gov.hmrc" %% "auth-client" % authVersion,
    "uk.gov.hmrc" %% "bootstrap-play-25" % "4.13.0"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  private val scalatestVersion = "3.0.5"
  private val scalatestPlusPlayVersion = "2.0.1"
  private val pegdownVersion = "1.6.0"
  private val scalacheckVersion = "1.13.4"

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest" %% "scalatest" % scalatestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
        "org.scalacheck" %% "scalacheck" % scalacheckVersion % scope,
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
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-core" % "1.9.5" % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}

