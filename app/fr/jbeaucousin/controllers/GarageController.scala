package fr.jbeaucousin.controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.http.Status

import play.api.libs.json.{ JsObject, JsValue, Json, Writes }

import scala.concurrent.{ ExecutionContext, Future, Promise }

import fr.jbeaucousin.model.{ JsonError }

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class GarageController @Inject()(
    val controllerComponents: ControllerComponents,
    val action: DefaultActionBuilder) extends BaseController {

  val logger: Logger = Logger(this.getClass())
   
  def addGarage() = action.async { implicit request: Request[AnyContent] =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val json = jsonBody.getOrElse(null)
    if(json == null) {
      val error = JsonError(BAD_REQUEST, "No json body sent")
      Future.successful(BadRequest(Json.toJson(error)))
    } else {
      logger.debug(json.toString())
      Future.successful(Ok(""))  
    }
  }
}
