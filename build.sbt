import sbt.Keys.{libraryDependencies, _}
import sbt.Tests.{Group, SubProcess}
import sbt._
import uk.gov.hmrc._
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
  import scoverage.ScoverageKeys
  Seq(
    ScoverageKeys.coverageExcludedPackages :=
      "<empty>;Reverse.*;.*AuthService.*;models/.data/..*;view.*;config.*;app;prod;testOnlyDoNotUseInAppConf;uk.gov.hmrc.BuildInfo;repositories.*",
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    Test / parallelExecution := false
  )
}

lazy val IntegrationTest = config("it") extend Test

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala,  SbtDistributablesPlugin) ++ plugins: _*)
  .disablePlugins(JUnitXmlReportPlugin)
   .settings(scalacOptions += "-Wconf:src=routes/.*:s")
   .settings(scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s")
   .settings(scalacOptions ++= Seq("-Ypatmat-exhaust-depth", "40"))
  .settings(
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes ++= Seq("org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always)
  )
  .settings(majorVersion := 4)
  .settings(playSettings ++ scoverageSettings: _*)
  .settings(scalaSettings: _*)
  .settings(scalaVersion := "2.13.16")
  .settings(defaultSettings(): _*)
  .settings(
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    PlayKeys.playDefaultPort := 8940
  )

  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.testSettings): _*)
  .settings(
    IntegrationTest / Keys.fork := false,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    IntegrationTest /testGrouping := oneForkedJvmPerTest((IntegrationTest / definedTests).value),
    IntegrationTest / parallelExecution := false
   )

def oneForkedJvmPerTest(tests: Seq[TestDefinition]) = {
  tests.map { test =>
    new Group(test.name, Seq(test), SubProcess(ForkOptions().withRunJVMOptions(Vector(s"-Dtest.name=${test.name}"))))
  }
}