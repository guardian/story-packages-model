import sbtrelease._
import ReleaseStateTransformations._

name := "story-packages-model"
organization := "com.gu"
scalaVersion := "2.11.7"

import com.twitter.scrooge._

seq(ScroogeSBT.newSettings: _*)

ScroogeSBT.scroogeThriftOutputFolder in Compile := sourceManaged.value / "thrift"

libraryDependencies ++= Seq(
    "org.apache.thrift" % "libthrift" % "0.9.2",
    "com.twitter" %% "scrooge-core" % "3.17.0"
)

crossScalaVersions := Seq("2.10.6", "2.11.7")

// Include the Thrift file in the published jar
unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main/thrift"

// Publish settings
scmInfo := Some(ScmInfo(url("https://github.com/guardian/story-packages-model"),
    "scm:git:git@github.com:guardian/story-packages-model.git"))

description := "Story package model"

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
