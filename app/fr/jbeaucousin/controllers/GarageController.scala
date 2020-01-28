package fr.jbeaucousin.controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.http.Status

import play.api.libs.json.{ JsObject, JsValue, Json, Writes, JsResult }

import scala.concurrent.{ ExecutionContext, Future, Promise }

import fr.jbeaucousin.model.{ JsonError }
import fr.jbeaucousin.dal.GarageDAO
import fr.jbeaucousin.model.Garage

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class GarageController @Inject()(
    val controllerComponents: ControllerComponents,
    val garageDAO: GarageDAO,
    val action: DefaultActionBuilder) extends BaseController {

  val logger: Logger = Logger(this.getClass())
   
  def addGarage = action.async { implicit request: Request[AnyContent] =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val json = jsonBody.getOrElse(null)
    if(json == null) {
      val error = JsonError(BAD_REQUEST, "No json body sent")
      Future.successful(BadRequest(Json.toJson(error)))
    } else {
      logger.debug(s"json body receive : ${json.toString()}")
      val garageResult: JsResult[Garage] = json.validate[Garage]
      val garage =  garageResult.getOrElse(null);
      if(garage == null) {
        logger.debug(s"garage result : ${garageResult.toString()}")
        val error = JsonError(BAD_REQUEST, "The body is not a valid garage")
        Future.successful(BadRequest(Json.toJson(error)))  
      } else {
        logger.debug(s"garage parsed : ${garage.toString()}")
        val insertedId = garageDAO.addGarage(garage)
        logger.debug(s"InsertedId, $insertedId")
        if(insertedId > -1) {
          Future.successful(Ok.withHeaders(
            ControllerConstants.HeaderFields.location -> routes.GarageController.getGarage(insertedId).absoluteURL())
          )  
        } else {
          val error = JsonError(INTERNAL_SERVER_ERROR, "An error occured on insertion")
          Future.successful(InternalServerError(Json.toJson(error)))
        }          
      }
    }
  }
  
  def getGarage(id: Int) = action.async { implicit request: Request[AnyContent] =>
      logger.debug(s"id received : ${id.toString()}")
      val garage = garageDAO.getGarage(id)
      if(garage != null) {
        logger.debug(s"garages found : ${garage.toString()}")
        Future.successful(Ok(Json.toJson(garage)))
      } else {
        Future.successful(NotFound)
      }
  }
}
