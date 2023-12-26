addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.10.0")
addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.2.1")

addSbtPlugin("com.github.sbt" % "sbt-ci-release"     % "1.5.12")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"       % "2.5.0")
addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.22.0")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix"       % "0.11.1")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
