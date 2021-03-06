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
    def callProcessing() = {
      val jsonBody: Option[JsValue] = request.body.asJson
      if(jsonBody.isEmpty) {
        val error = JsonError(BAD_REQUEST, "No json body sent", None)
        Future.successful(BadRequest(Json.toJson(error)))
      } else {
        val json = jsonBody.get
        logger.debug(s"json body receive : ${json.toString()}")
        val garageResult: JsResult[Garage] = json.validate[Garage]
        if(garageResult.isError) {
          logger.debug(s"garage result : ${garageResult.toString()}")
          val error = JsonError(BAD_REQUEST, "The body is not a valid garage", None)
          Future.successful(BadRequest(Json.toJson(error)))  
        } else {
          val garage =  garageResult.get
          logger.debug(s"garage parsed : ${garage.toString()}")
          val insertedId = garageDAO.addGarage(garage)
          logger.debug(s"InsertedId, $insertedId")
          if(insertedId > -1) {
            Future.successful(Created.withHeaders(
              ControllerConstants.HeaderFields.location -> routes.GarageController.getGarage(insertedId).absoluteURL())
            )  
          } else {
            val error = JsonError(INTERNAL_SERVER_ERROR, "An error occured on insertion", None)
            Future.successful(InternalServerError(Json.toJson(error)))
          }          
        }
      }
    }
    ControllerUtils.handleException(callProcessing)
  }
  
  def getGarage(id: Int) = action.async { implicit request: Request[AnyContent] =>
    def callProcessing() = {
      logger.debug(s"id received : ${id.toString()}")
      val garage = garageDAO.getGarage(id)
      if(garage != null) {
        logger.debug(s"garages found : ${garage.toString()}")
        Future.successful(Ok(Json.toJson(garage)))
      } else {
        Future.successful(NotFound)
      }
    }
    ControllerUtils.handleException(callProcessing)
  }
  
  def deleteGarage(id: Int) = action.async { implicit request: Request[AnyContent] =>
    def callProcessing() = {
      logger.debug(s"id received : ${id.toString()}")
      val deletedId = garageDAO.deleteGarage(id)
      logger.debug(s"deletedId : , $deletedId")
      if(deletedId > -1) {
        Future.successful(Ok)
      } else {
        Future.successful(NotFound)
      }
    }
    ControllerUtils.handleException(callProcessing)
  }
}
