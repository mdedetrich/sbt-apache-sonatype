package org.mdedetrich.apache.sonatype

import sbt.Keys._
import sbt._
import sbt.util.Level
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.SonatypeKeys._

object ApacheSonatypePlugin extends AutoPlugin {

  object autoImport extends ApacheSonatypeKeys

  import autoImport._

  /** Adds a file to the a specified resourceManaged `META-INF` folder so that it will get included by sbt when
    * generating artifacts
    * @see
    *   https://www.scala-sbt.org/1.x/docs/Howto-Generating-Files.html#Generate+resources
    * @param resourceManagedDir
    *   sbt's resource managed directory typically derived by using `resourcedManaged.value`, the file will get copied
    *   in this location under the `META-INF` folder.
    * @param file
    *   The file you want to add to the META-INF folder
    * @param targetFileName
    *   The target file name, if not specified then it uses the same filename specified in `file`.
    * @return
    *   The resulting [[File]] which was added
    */
  final def addFileToMetaInf(resourceManagedDir: File, file: File, targetFileName: Option[String] = None): File = {
    val toFile = resourceManagedDir / "META-INF" / targetFileName.getOrElse(file.getName)
    IO.copyFile(file, toFile)
    toFile
  }

  final private[sonatype] def processArtifactName(artifactId: String) = {
    val prettified = artifactId
      .replaceAll("-", " ")
      .replaceAll("_", " ")
      .split(" ")
      .map(_.capitalize)
      .mkString(" ")
    if (prettified.startsWith("Apache ")) prettified else s"Apache $prettified"
  }

  private[sonatype] lazy val apacheSonatypeGlobalSettings: Seq[Setting[_]] = Seq(
    apacheSonatypeBaseRepo                  := "repository.apache.org",
    apacheSonatypeCredentialsUserEnvVar     := "NEXUS_USER",
    apacheSonatypeCredentialsPasswordEnvVar := "NEXUS_PW",
    apacheSonatypeCredentialsHost           := "Sonatype Nexus Repository Manager",
    apacheSonatypeOrganizationName          := "Apache Software Foundation",
    apacheSonatypeOrganizationHomePage      := url("https://www.apache.org"),
    apacheSonatypeCredentialsProvider := {
      CredentialProvider.Environment(apacheSonatypeCredentialsUserEnvVar.value,
                                     apacheSonatypeCredentialsPasswordEnvVar.value
      )
    },
    apacheSonatypeCredentialsLogLevel   := Level.Debug,
    apacheSonatypeArtifactNameProcessor := processArtifactName
  )

  private[sonatype] lazy val sbtSonatypeBuildSettings: Seq[Setting[_]] = Seq(
    sonatypeCredentialHost := apacheSonatypeBaseRepo.value,
    sonatypeProfileName    := s"org.apache.${apacheSonatypeProjectProfile.value}"
  )

  private[sonatype] lazy val baseDir = LocalRootProject / baseDirectory

  import scala.xml._

  private[sonatype] def processNameInPom(processName: String => String): Node => Node = (pomXml: Node) =>
    new transform.RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case projectNode if projectNode.label == "project" =>
          val modifiedChildren = projectNode.child.collect {
            case <name>{currentName}</name> => <name>{processName(currentName.text)}</name>
            case other                      => other
          }
          Elem(projectNode.prefix,
               projectNode.label,
               projectNode.attributes,
               projectNode.scope,
               minimizeEmpty = false,
               modifiedChildren: _*
          )
        case other => other
      }
    }.transform(pomXml).head

  private[sonatype] lazy val sbtMavenBuildSettings: Seq[Setting[_]] = Seq(
    credentials ++= {
      val log   = sLog.value
      val level = apacheSonatypeCredentialsLogLevel.value
      apacheSonatypeCredentialsProvider.value.credentials(log, level).map { apacheCredentials =>
        Credentials(apacheSonatypeCredentialsHost.value,
                    apacheSonatypeBaseRepo.value,
                    apacheCredentials.nexusUser,
                    apacheCredentials.nexusPassword
        )
      }
    },
    organization         := s"org.apache.${apacheSonatypeProjectProfile.value}",
    organizationName     := apacheSonatypeOrganizationName.value,
    organizationHomepage := Some(apacheSonatypeOrganizationHomePage.value),
    pomPostProcess       := pomPostProcess.value andThen processNameInPom(apacheSonatypeArtifactNameProcessor.value)
  )

  private[sonatype] lazy val sbtMavenProjectSettings: Seq[Setting[_]] = Seq(
    licenses ++= {
      val log             = sLog.value
      val currentLicenses = licenses.value
      currentLicenses.collectFirst { case (field, url) if field.contains("Apache") => url } match {
        case Some(url) =>
          log.warn(
            s"No Apache license added in project ${projectID.value} since a duplicate license has already been detected with url: ${url.toString}, please remove it"
          )
          Nil
        case None => Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html"))
      }
    },
    apacheSonatypeLicenseFile    := baseDir.value / "LICENSE",
    apacheSonatypeNoticeFile     := baseDir.value / "NOTICE",
    apacheSonatypeDisclaimerFile := None,
    publishTo                    := sonatypePublishToBundle.value
  ) ++ inConfig(Compile)(
    Seq(
      resourceGenerators += {
        Def.task {
          val dir = resourceManaged.value
          List(
            addFileToMetaInf(dir, apacheSonatypeLicenseFile.value, Some("LICENSE")),
            addFileToMetaInf(dir, apacheSonatypeNoticeFile.value, Some("NOTICE"))
          ) ++ apacheSonatypeDisclaimerFile.value
            .map(disclaimerFile => addFileToMetaInf(dir, disclaimerFile))
            .toList
        }
      },
      // See https://issues.apache.org/jira/browse/LEGAL-28
      packageSrc / mappings ++= {
        Seq(
          apacheSonatypeLicenseFile.value -> "META-INF/LICENSE",
          apacheSonatypeNoticeFile.value  -> "META-INF/NOTICE"
        ) ++ apacheSonatypeDisclaimerFile.value.map(path => path -> "META-INF/DISCLAIMER").toSeq
      },
      packageDoc / mappings ++= {
        Seq(
          apacheSonatypeLicenseFile.value -> "META-INF/LICENSE",
          apacheSonatypeNoticeFile.value  -> "META-INF/NOTICE"
        ) ++ apacheSonatypeDisclaimerFile.value.map(path => path -> "META-INF/DISCLAIMER").toSeq
      }
    )
  )

  override lazy val globalSettings: Seq[Setting[_]] =
    apacheSonatypeGlobalSettings

  override lazy val buildSettings: Seq[Setting[_]] =
    sbtSonatypeBuildSettings ++ sbtMavenBuildSettings

  override lazy val projectSettings: Seq[Setting[_]] = sbtMavenProjectSettings

  override def trigger = allRequirements

  override def requires = Sonatype

}
