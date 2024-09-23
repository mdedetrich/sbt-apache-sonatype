addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.11.3")
addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.2.1")

addSbtPlugin("com.github.sbt" % "sbt-ci-release"     % "1.6.1")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"       % "2.5.2")
addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.24.0")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix"       % "0.12.1")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
