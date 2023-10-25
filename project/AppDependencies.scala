import sbt.*

private object AppDependencies {
  import play.sbt.PlayImport.*

  val bootstrapVersion = "7.22.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-play-28"         % "1.3.0",
    "org.typelevel"        %% "cats-core"                  % "2.9.0",
    "com.github.fge"       %  "json-schema-validator"      % "2.2.14",
    "com.eclipsesource"    %% "play-json-schema-validator" % "0.9.5",
    "com.beachape"         %% "enumeratum"                 % "1.7.0",
    "uk.gov.hmrc"          %% "bootstrap-backend-play-28"  % bootstrapVersion,
    "com.github.ghik"      %  "silencer-lib"               % "1.7.11" % Provided cross CrossVersion.full,
    compilerPlugin("com.github.ghik" % "silencer-plugin"  % "1.7.11" cross CrossVersion.full)
  )

  trait TestDependencies {
    val scope: String = "test"
    val test : Seq[ModuleID]
  }

  object Test {
    def apply(): Seq[sbt.ModuleID] = new TestDependencies {
      override val test: Seq[sbt.ModuleID] = Seq(
        "org.mockito"             %% "mockito-scala"             % "1.17.12"                % scope,
        "org.scalatestplus"       %% "scalacheck-1-17"           % "3.2.15.0"               % scope,
        "uk.gov.hmrc"             %% "bootstrap-test-play-28"    % bootstrapVersion         % scope,
        "com.vladsch.flexmark"    %  "flexmark-all"              % "0.64.0"                 % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply(): Seq[sbt.ModuleID] = new TestDependencies {

      override val scope: String = "it"

      override val test: Seq[sbt.ModuleID] = Seq(
        "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28" % "1.3.0"                  % scope,
        "uk.gov.hmrc"             %% "bootstrap-test-play-28"  % bootstrapVersion         % scope
      )
    }.test
  }

  def apply(): Seq[sbt.ModuleID] = compile ++ Test() ++ IntegrationTest()
}
