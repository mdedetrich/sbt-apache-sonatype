addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.12.2")
addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.3.1")

addSbtPlugin("com.github.sbt" % "sbt-ci-release"     % "1.11.2")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"       % "2.5.2")
addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.24.0")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix"       % "0.12.1")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
