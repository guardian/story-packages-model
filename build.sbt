import sbtrelease._
import ReleaseStateTransformations._

val thriftVersion = "0.15.0"
val scroogeVersion = "22.1.0"

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

val snapshotReleaseType = "snapshot"

lazy val releaseProcessSteps: Seq[ReleaseStep] = {
  val commonSteps: Seq[ReleaseStep] = Seq(
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
  )

  val localExtraSteps: Seq[ReleaseStep] = Seq(
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion
  )

  val snapshotSteps: Seq[ReleaseStep] = Seq(
    publishArtifacts,
    releaseStepCommand("sonatypeReleaseAll")
  )

  val prodSteps: Seq[ReleaseStep] = Seq(
    releaseStepCommandAndRemaining("+publishSigned"),
    releaseStepCommand("sonatypeBundleRelease")
  )

  val localPostRelease: Seq[ReleaseStep] = Seq(
    pushChanges,
  )

  (sys.props.get("RELEASE_TYPE"), sys.env.get("CI")) match {
    case (Some(v), None) if v == snapshotReleaseType => commonSteps ++ localExtraSteps ++ snapshotSteps ++ localPostRelease
    case (_, None) => commonSteps ++ localExtraSteps ++ prodSteps ++ localPostRelease
    case (Some(v), _) if v == snapshotReleaseType => commonSteps ++ snapshotSteps
    case (_, _) => commonSteps ++ prodSteps
  }
}

val commonSettings = Seq(
  organization := "com.gu",
  scalaVersion := "2.13.2",
  crossScalaVersions := Seq("2.12.11", scalaVersion.value),
  releaseCrossBuild := true,
  scmInfo := Some(ScmInfo(url("https://github.com/guardian/story-packages-model"),
    "scm:git:git@github.com:guardian/story-packages-model.git")),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value
) ++ mavenSettings

lazy val root = (project in file("."))
  .aggregate(thrift, scalaClasses)
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    releaseProcess := releaseProcessSteps
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
    Compile / unmanagedResourceDirectories += {
      baseDirectory.value / "src/main/thrift"
    }
  )

lazy val typescriptClasses = (project in file("ts"))
  .enablePlugins(ScroogeTypescriptGen)
  .settings(commonSettings)
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
