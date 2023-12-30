import sbtrelease._
import ReleaseStateTransformations._

val thriftVersion = "0.15.0"
val scroogeVersion = "22.1.0"

val snapshotReleaseType = "snapshot"
val snapshotReleaseSuffix = "-SNAPSHOT"

val betaReleaseType = "beta"
val betaReleaseSuffix = "-BETA"

lazy val versionSettingsMaybe = {
  sys.props.get("RELEASE_TYPE").map {
    case v if v == snapshotReleaseType => snapshotReleaseSuffix
    case _ => ""
  }.map { suffix =>
    releaseVersion := {
      ver => Version(ver).map(_.withoutQualifier.string).map(_.concat(suffix)).getOrElse(versionFormatError(ver))
    }
  }.toSeq
}

lazy val mavenSettings = Seq(
  pomExtra := (
    <url>https://github.com/guardian/story-packages-model</url>
    <developers>
      <developer>
        <id>Reettaphant</id>
        <name>Reetta Vaahtoranta</name>
        <url>https://github.com/guardian</url>
      </developer>
      <developer>
        <id>justinpinner</id>
        <name>Justin Pinner</name>
        <url>https://github.com/justinpinner</url>
      </developer>
      </developers>
    ),
  licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  publishTo := sonatypePublishToBundle.value,
  publishConfiguration := publishConfiguration.value.withOverwrite(true)
)

lazy val commonReleaseProcess = Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  setReleaseVersion,
  runClean,
  runTest,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned")
)

lazy val productionReleaseProcess = commonReleaseProcess ++ Seq[ReleaseStep](
  releaseStepCommand("sonatypeBundleRelease")
)

lazy val snapshotReleaseProcess = commonReleaseProcess

val commonSettings = Seq(
  organization := "com.gu",
  scalaVersion := "2.13.2",
  crossScalaVersions := Seq("2.12.11", scalaVersion.value),
  releaseCrossBuild := true,
  scmInfo := Some(ScmInfo(url("https://github.com/guardian/story-packages-model"),
      "scm:git:git@github.com:guardian/story-packages-model.git")),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value
) ++ mavenSettings ++ versionSettingsMaybe

lazy val root = (project in file("."))
  .aggregate(thrift, scalaClasses)
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    releaseProcess := {
      sys.props.get("RELEASE_TYPE") match {
        case Some("production") => productionReleaseProcess
        case _ => snapshotReleaseProcess
      }
    }
  )

lazy val scalaClasses = (project in file("scala"))
  .settings(commonSettings)
  .settings(
    name := "story-packages-model",
    description := "Story package model",
    Compile / scroogeThriftSourceFolder := baseDirectory.value / "../thrift/src/main/thrift",
    Compile / scroogeThriftOutputFolder := sourceManaged.value,
    libraryDependencies ++= Seq(
        "org.apache.thrift" % "libthrift" % thriftVersion,
        "com.twitter" %% "scrooge-core" % scroogeVersion,
        "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
    ),
    // Include the Thrift file in the published jar
    Compile / scroogePublishThrift := true,
    Compile / scroogeThriftIncludeRoot := false,
    Global / excludeLintKeys += scroogeThriftIncludeRoot
  )

lazy val thrift = (project in file("thrift"))
  .disablePlugins(ScroogeSBT)
  .settings(commonSettings)
  .settings(
    name := "story-packages-model-thrift",
    description := "Story package model Thrift files",
    crossPaths := false,
    packageDoc / publishArtifact := false,
    packageSrc / publishArtifact := false,
    Compile / unmanagedResourceDirectories += { baseDirectory.value / "src/main/thrift" }
  )

lazy val npmBetaReleaseTagMaybe =
  sys.props.get("RELEASE_TYPE").map {
    case v if v == betaReleaseType =>
      // Why hard-code "beta" instead of using the value of the variable? That's to ensure it's always presented as
      // --tag beta to the npm release process provided by the ScroogeTypescriptGen plugin regardless of how we identify
      // a beta release here
      scroogeTypescriptPublishTag := "beta"
  }.toSeq

lazy val typescriptClasses = (project in file("ts"))
  .enablePlugins(ScroogeTypescriptGen)
  .settings(commonSettings)
  .settings(npmBetaReleaseTagMaybe)
  .settings(
    publishArtifact := false,
    name := "story-packages-typescript",
    scroogeTypescriptNpmPackageName := "@guardian/story-packages-model",
    Compile / scroogeDefaultJavaNamespace := scroogeTypescriptNpmPackageName.value,
    Test / scroogeDefaultJavaNamespace := scroogeTypescriptNpmPackageName.value,
    description := "Typescript library built from the story packages thrift definition",
    Compile / scroogeLanguages := Seq("typescript"),
    Compile / scroogeThriftSourceFolder := baseDirectory.value / "../thrift/src/main/thrift",
    scroogeTypescriptPackageLicense := "Apache-2.0"
  )
