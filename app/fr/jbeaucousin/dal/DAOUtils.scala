package fr.jbeaucousin.dal

import play.api.Logger
import play.api.db.Database

import java.sql.Statement

import fr.jbeaucousin.model.TechnicalException

object DAOUtils {
  
  val logger: Logger = Logger(this.getClass())
  
  def handleConnection(db : Database, callback: (Statement) => Unit) = {
    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement
      callback(stmt)
    } catch { 
      case e: Exception => {
        logger.error("An error occured during request", e)
        throw new TechnicalException("An error occured during connection to database", e)
      }
    } finally {
      conn.close()
    }
  }
}