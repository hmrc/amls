import sbt.Keys.{libraryDependencies, *}
import sbt.*
import uk.gov.hmrc.*
import DefaultBuildSettings.{addTestReportOption, defaultSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName: String = "amls"

lazy val appDependencies: Seq[ModuleID] = AppDependencies()
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

def makeExcludedFiles(rootDir: File): Seq[String] = {
  val excluded = findPlayConfFiles(rootDir) ++ findSbtFiles(rootDir)
  excluded
}
def findSbtFiles(rootDir: File): Seq[String] = {
  if (rootDir.getName == "project") {
    rootDir.listFiles().map(_.getName).toSeq
  } else {
    Seq()
  }
}
def findPlayConfFiles(rootDir: File): Seq[String] = {
  Option {
    new File(rootDir, "conf").listFiles()
  }.fold(Seq[String]()) { confFiles =>
    confFiles
      .map(_.getName.replace(".routes", ".Routes"))
  }
}

lazy val scoverageSettings = {
  Seq(
    coverageExcludedPackages :=
      """
        |<empty>;
        |Reverse.*;
        |.*AuthService.*;
        |models/.data/..*;
        |view.*;
        |config.*;
        |app;
        |prod;
        |testOnlyDoNotUseInAppConf;
        |uk.gov.hmrc.BuildInfo;
        |repositories.*;
        |.*metrics.*;
        |.*exceptions.*;
        |.*SubscriptionFees.*
        |.*CorpBodyOrUnInCorpBodyOrLlp.*
      """.stripMargin.replaceAll("\\s", ""),
    coverageMinimumStmtTotal := 90,
    coverageFailOnMinimum := false,
    coverageHighlighting := true,
    Test / parallelExecution := false
  )
}

lazy val IntegrationTest = config("it") extend Test

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala,  SbtDistributablesPlugin) ++ plugins: _*)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    scalacOptions ++= Seq(
      "-Wconf:msg=unused import&src=html/.*:s",
      "-Wconf:src=routes/.*:s"
    )
  )
  .settings(
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes ++= Seq("org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always)
  )
  .settings(majorVersion := 4)
  .settings(playSettings ++ scoverageSettings: _*)
  .settings(scalaSettings: _*)
  .settings(scalaVersion := "3.3.7")
  .settings(defaultSettings(): _*)
  .settings(
    libraryDependencies ++= appDependencies,
    excludeDependencies += ExclusionRule("org.lz4", "lz4-java"),
    retrieveManaged := true,
    PlayKeys.playDefaultPort := 8940
  )

  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.testSettings): _*)
  .settings(
    IntegrationTest / Keys.fork := false,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    IntegrationTest / parallelExecution := false
   )

