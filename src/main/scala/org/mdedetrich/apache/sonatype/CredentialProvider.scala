package org.mdedetrich.apache.sonatype

import sbt.util.{Level, Logger}

/** A datatype used to describe the way to derive the username and password that is used for Apache Nexus Repository
  */
trait CredentialProvider {

  /** @param logger
    *   The base sbt [[Logger]] to use when logging about potential problems in resolving credentials
    * @param level
    *   The log level [[Level.Value]] to use when logging about potential problems in resolving credentials
    * @return
    *   Either the [[ApacheNexusCredentials]] or [[None]] if credentials could not be resolved (use `logger` when you
    *   cannot resolve the credentials to report problems).
    */
  def credentials(logger: Logger, level: Level.Value): Option[ApacheNexusCredentials]
}

object CredentialProvider {

  /** For credentials that are provided by environment variables, this is typical of Apache projects that use github
    * actions.
    */
  final case class Environment(nexusUserVar: String, nexusPasswordVar: String) extends CredentialProvider {
    override def credentials(logger: Logger, level: Level.Value): Option[ApacheNexusCredentials] =
      (sys.env.get(nexusUserVar), sys.env.get(nexusPasswordVar)) match {
        case (Some(user), Some(password)) if user.nonEmpty && password.nonEmpty =>
          Some(ApacheNexusCredentials(user, password))
        case (Some(user), Some(password)) if user.isEmpty && password.isEmpty =>
          logger.log(
            level,
            s"Both $nexusUserVar and $nexusPasswordVar environment variables for Apache Maven Nexus repo are empty strings"
          )
          None
        case (Some(user), Some(password)) =>
          if (user.isEmpty)
            logger.log(level, s"Environment variable $nexusUserVar for Apache Maven Nexus repo is an empty string")
          else if (password.isEmpty)
            logger.log(level, s"Environment variable $nexusPasswordVar for Apache Maven Nexus repo is an empty string")
          None
        case (Some(_), None) =>
          logger.log(level, s"Environment variable $nexusPasswordVar for Apache Maven Nexus repo is absent")
          None
        case (None, Some(_)) =>
          logger.log(level, s"Environment variable $nexusUserVar for Apache Maven Nexus repo is absent")
          None
        case _ =>
          logger.log(
            level,
            s"Both $nexusUserVar and $nexusPasswordVar environment variables for Apache Maven Nexus repo are absent"
          )
          None
      }
  }
}
