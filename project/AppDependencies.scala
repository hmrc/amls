import sbt._

private object AppDependencies {
  import play.sbt.PlayImport._

  val bootstrapVersion = "9.11.0"

  private val playV = "play-30"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo"    %% s"hmrc-mongo-$playV"         % "2.6.0",
    "org.typelevel"        %% "cats-core"                  % "2.10.0",
    "com.github.fge"       %  "json-schema-validator"      % "2.2.14",
    "com.beachape"         %% "enumeratum"                 % "1.7.3",
    "uk.gov.hmrc"          %% s"bootstrap-backend-$playV"  % bootstrapVersion
  )

  trait TestDependencies {
    val scope: String = "test"
    val test : Seq[ModuleID]
  }

  object Test {
    def apply(): Seq[sbt.ModuleID] = new TestDependencies {
      override val test: Seq[sbt.ModuleID] = Seq(
        "org.mockito"             %% "mockito-scala"             % "1.17.30"                % scope,
        "org.scalatestplus"       %% "scalacheck-1-17"           % "3.2.17.0"               % scope,
        "uk.gov.hmrc"             %% s"bootstrap-test-$playV"    % bootstrapVersion         % scope,
        "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test-$playV"   % "2.6.0"                  % scope,
      )
    }.test
  }

  def apply(): Seq[sbt.ModuleID] = compile ++ Test()
}
