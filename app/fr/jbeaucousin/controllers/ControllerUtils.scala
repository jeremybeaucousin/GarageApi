package fr.jbeaucousin.controllers

import java.io.StringWriter
import java.io.PrintWriter

import play.api.Logger
import play.api.http.Status
import play.api.mvc.Results
import play.api.libs.json.Json

import scala.concurrent.Future

import fr.jbeaucousin.model.{ JsonError, TechnicalException }

object ControllerUtils {
  
  val logger: Logger = Logger(this.getClass())
  
  def handleException(callback: () => Future[play.api.mvc.Result]) = {
    try {
      callback()
    } catch { 
      case e: Exception => {
        logger.error("An error occured during call", e)
        // Print StackTrace
        val sw = new StringWriter
        e.printStackTrace(new PrintWriter(sw))
        logger.error(sw.toString)
        val error = JsonError(Status.INTERNAL_SERVER_ERROR, e.getMessage, Some(sw.toString()))
        Future.successful(Results.InternalServerError(Json.toJson(error)))
      }
    }
  }
}