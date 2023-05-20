addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.21")

addSbtPlugin("com.github.sbt" % "sbt-ci-release"     % "1.5.11")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"       % "2.5.0")
addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.14.2")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
