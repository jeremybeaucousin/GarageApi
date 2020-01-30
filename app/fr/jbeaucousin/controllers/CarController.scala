package fr.jbeaucousin.controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.http.Status

import play.api.libs.json.{ JsObject, JsValue, Json, Writes, JsResult }

import scala.concurrent.{ ExecutionContext, Future, Promise }

import fr.jbeaucousin.model.{ JsonError }
import fr.jbeaucousin.dal.{ CarDAO, GarageDAO }
import fr.jbeaucousin.model.Car

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class CarController @Inject()(
    val controllerComponents: ControllerComponents,
    val carDAO: CarDAO,
    val garageDAO: GarageDAO,
    val action: DefaultActionBuilder) extends BaseController {

  val logger: Logger = Logger(this.getClass())
   
  def addCar(garageId: Int) = action.async { implicit request: Request[AnyContent] =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val json = jsonBody.getOrElse(null)
    if(json == null) {
      val error = JsonError(BAD_REQUEST, "No json body sent")
      Future.successful(BadRequest(Json.toJson(error)))
    } else {
      logger.debug(s"json body receive : ${json.toString()}")
      val carResult: JsResult[Car] = json.validate[Car]
      val car =  carResult.getOrElse(null);
      if(car == null) {
        logger.debug(s"car result : ${carResult.toString()}")
        val error = JsonError(BAD_REQUEST, "The body is not a valid car")
        Future.successful(BadRequest(Json.toJson(error)))  
      } else {
        logger.debug(s"car parsed : ${car.toString()}")
        // Check if garage exists
        val garageFound = garageDAO.getGarage(garageId)
        if(garageFound != null) {
          // Check if the garage is not already full
          val cars = carDAO.getCars(Some(garageId))
          if((cars.length + 1) > garageFound.maxCarCapacity) {
            val error = JsonError(UNPROCESSABLE_ENTITY, "Garage has reached its max capacity")
            Future.successful(UnprocessableEntity(Json.toJson(error)))
          } else {
            // Use path id only
            // Can be crossed with the one in the body for verification
            car.garageId = Some(garageId)
            val insertedId = carDAO.addGarage(car)
            logger.debug(s"InsertedId, $insertedId")
            if(insertedId > -1) {
              // ControllerConstants.HeaderFields.location -> routes.CarController.getGarage(insertedId).absoluteURL())
              Future.successful(Created.withHeaders(
                ControllerConstants.HeaderFields.location -> insertedId.toString())
              )  
            } else {
              val error = JsonError(INTERNAL_SERVER_ERROR, "An error occured on insertion")
              Future.successful(InternalServerError(Json.toJson(error)))
            }       
          }
        } else {
            val error = JsonError(NOT_FOUND, s"Garage identifier ${garageId} not found")
            Future.successful(NotFound(Json.toJson(error)))
        }
           
      }
    }
  }
  
//  def getGarage(id: Int) = action.async { implicit request: Request[AnyContent] =>
//      logger.debug(s"id received : ${id.toString()}")
//      val garage = garageDAO.getGarage(id)
//      if(garage != null) {
//        logger.debug(s"garages found : ${garage.toString()}")
//        Future.successful(Ok(Json.toJson(garage)))
//      } else {
//        Future.successful(NotFound)
//      }
//  }

//  def deleteGarage(id: Int) = action.async { implicit request: Request[AnyContent] =>
//      logger.debug(s"id received : ${id.toString()}")
//      val deletedId = garageDAO.deleteGarage(id)
//      logger.debug(s"deletedId : , $deletedId")
//      if(deletedId > -1) {
//        Future.successful(Ok)
//      } else {
//        Future.successful(NotFound)
//      }
//  }
}
