package org.mdedetrich.apache.sonatype

import sbt._
import sbt.Keys._
import sbt.util.Level
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.SonatypeKeys._

object SonatypeApachePlugin extends AutoPlugin {

  object autoImport extends SonatypeApacheKeys

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

  private[sonatype] def sonatypeApacheGlobalSettings: Seq[Setting[_]] = Seq(
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
    apacheSonatypeCredentialsLogLevel := Level.Debug
  )

  private[sonatype] def sbtSonatypeGlobalSettings: Seq[Setting[_]] = Seq(
    sonatypeCredentialHost := apacheSonatypeBaseRepo.value,
    sonatypeProfileName    := s"org.apache.${apacheSonatypeProjectProfile.value}"
  )

  private[sonatype] lazy val baseDir = LocalRootProject / baseDirectory

  private[sonatype] def sbtMavenSettings: Seq[Setting[_]] = Seq(
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
    organizationName             := apacheSonatypeOrganizationName.value,
    organizationHomepage         := Some(apacheSonatypeOrganizationHomePage.value),
    publishMavenStyle            := true,
    pomIncludeRepository         := (_ => false),
    apacheSonatypeLicenseFile    := baseDir.value / "LICENSE",
    apacheSonatypeNoticeFile     := baseDir.value / "NOTICE",
    apacheSonatypeDisclaimerFile := None
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
      }
    )
  )

  override def projectSettings: Seq[Setting[_]] =
    sonatypeApacheGlobalSettings ++ sbtSonatypeGlobalSettings ++ sbtMavenSettings

  override def trigger = allRequirements

  override def requires = Sonatype

}
