import sbt._

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  val compile = Seq(
    ws,
    "uk.gov.hmrc"         %% "simple-reactivemongo"       % "8.0.0-play-26",
    "uk.gov.hmrc"         %% "mongo-caching"              % "7.0.0-play-26",
    "uk.gov.hmrc"         %% "domain"                     % "5.11.0-play-26",
    "org.typelevel"       %% "cats"                       % "0.9.0",
    "com.eclipsesource"   %% "play-json-schema-validator" % "0.9.4",
    "de.flapdoodle.embed" %  "de.flapdoodle.embed.mongo"  % "2.2.0" % "test,it",
    "com.beachape"        %% "enumeratum"                 % "1.6.1",
    "uk.gov.hmrc"         %% "bootstrap-backend-play-26"  % "5.3.0",
    "com.typesafe.play"   %% "play-json-joda"             % "2.7.4",
    "com.github.kxbmap"   %% "configs"                    % "0.4.4",
    "com.github.ghik"     %  "silencer-lib"               % "1.7.5" % Provided cross CrossVersion.full,
    compilerPlugin("com.github.ghik" % "silencer-plugin"  % "1.7.5" cross CrossVersion.full)
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  private val scalatestVersion = "3.0.9"
  private val scalatestPlusPlayVersion = "3.1.2"
  private val pegdownVersion = "1.6.0"
  private val scalacheckVersion = "1.14.3"

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest"           %% "scalatest"          % scalatestVersion % scope,
        "org.scalatestplus.play"  %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
        "org.scalacheck"          %% "scalacheck"         % scalacheckVersion % scope,
        "org.pegdown"             %  "pegdown"            % pegdownVersion % scope,
        "com.typesafe.play"       %% "play-test"          % PlayVersion.current % scope,
        "org.mockito"             %  "mockito-core"       % "1.10.19" % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "org.scalatest"           %% "scalatest"          % scalatestVersion % scope,
        "org.scalatestplus.play"  %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
        "org.pegdown"             %  "pegdown"            % pegdownVersion % scope,
        "com.typesafe.play"       %% "play-test"          % PlayVersion.current % scope,
        "org.mockito"             %  "mockito-core"       % "1.10.19" % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}

