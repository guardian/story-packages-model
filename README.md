# story-packages-model

This repository contains the Thrift schema required for displaying story packages.

## Downloading

To download you should be able to simply add a dependency similar to the following (with your desired version):

`"com.gu" %% "story-packages-model" % "x.y.z"`

## How to release

Ensure you have the following installed on your machine:
 - `tsc` (`brew install typescript`)
 - `npm` (not sure! there are so many ways to install it)
 
Ensure you have an NPM account, part of the [@guardian](https://www.npmjs.com/org/guardian) org with a [configured token](https://docs.npmjs.com/creating-and-viewing-authentication-tokens)

You'll also need a sonatype account to publish the library to Maven.

```sbtshell
release // will release the scala / thrift projects
project typescriptClasses
releaseNpm 1.0.0 // you have to specify the version again
```
