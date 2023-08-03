addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.21")
addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.2.1")

addSbtPlugin("com.github.sbt" % "sbt-ci-release"     % "1.5.11")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"       % "2.5.0")
addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.15.0")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
