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

crossScalaVersions := Seq("2.10.4", "2.11.7")

// Publish settings

scmInfo := Some(ScmInfo(url("https://github.com/guardian/story-packages-model"),
    "scm:git:git@github.com:guardian/story-packages-model.git"))

description := "Java library built from Content-atom thrift definition"

pomExtra := (
    <url>https://github.com/guardian/content-atom</url>
    <developers>
        <developer>
            <id>paulmr</id>
            <name>Paul Roberts</name>
            <url>https://github.com/paulmr</url>
        </developer>
    </developers>
    )

licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

    releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepTask(PgpKeys.publishSigned),
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll")
    //pushChanges
)
