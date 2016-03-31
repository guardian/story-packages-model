import sbtrelease._
import ReleaseStateTransformations._

name := "story-packages-model"
organization := "com.gu"
scalaVersion := "2.11.7"

lazy val root = (project in file(".")).settings(
  description := "Story package model",
  scroogeThriftSourceFolder in Compile := baseDirectory.value / "thrift/src/main/thrift",
  scroogeThriftOutputFolder in Compile := sourceManaged.value,
  libraryDependencies ++= Seq(
      "org.apache.thrift" % "libthrift" % "0.9.3",
      "com.twitter" %% "scrooge-core" % "4.5.0"
  ),
  crossScalaVersions := Seq("2.10.6", "2.11.7"),
  managedSourceDirectories in Compile += (scroogeThriftOutputFolder in Compile).value,
  // Include the Thrift file in the published jar
  scroogePublishThrift in Compile := true
)

lazy val thrift = (project in file("thrift"))
.disablePlugins(ScroogeSBT)
.settings(
  name := "story-packages-model-thrift",
  description := "Story package model Thrift files",
  crossPaths := false,
  publishArtifact in packageDoc := false,
  publishArtifact in packageSrc := false,
  unmanagedResourceDirectories in Compile += { baseDirectory.value / "src/main/thrift" }
)


// Publish settings
scmInfo := Some(ScmInfo(url("https://github.com/guardian/story-packages-model"),
    "scm:git:git@github.com:guardian/story-packages-model.git"))

pomExtra := (
    <url>https://github.com/guardian/story-packages-model</url>
    <developers>
        <developer>
            <id>Reettaphant</id>
            <name>Reetta Vaahtoranta</name>
            <url>https://github.com/guardian</url>
        </developer>
    </developers>
    )

licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

releaseCrossBuild := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll"),
    pushChanges
)
