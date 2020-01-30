package fr.jbeaucousin.controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.http.Status
import play.api.mvc.Results

import play.api.libs.json.{ JsObject, JsValue, Json, Writes, JsResult }

import scala.concurrent.{ ExecutionContext, Future, Promise }

import fr.jbeaucousin.model.{ JsonError, Car, Garage  }
import fr.jbeaucousin.dal.{ CarDAO, GarageDAO }

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
    def callProcessing() = {
      val jsonBody: Option[JsValue] = request.body.asJson
      if(jsonBody.isEmpty) {
        val error = JsonError(BAD_REQUEST, "No json body sent", None)
        Future.successful(BadRequest(Json.toJson(error)))
      } else {
        val json = jsonBody.get
        logger.debug(s"json body receive : ${json.toString()}")
        val carResult: JsResult[Car] = json.validate[Car]
        if(carResult.isError) {
          logger.debug(s"car result : ${carResult.toString()}")
          val error = JsonError(BAD_REQUEST, "The body is not a valid car", None)
          Future.successful(BadRequest(Json.toJson(error)))  
        } else {
          val car =  carResult.get
          logger.debug(s"car parsed : ${car.toString()}")
          def carProcessing(garage: Garage) = {
            // Check if the garage is not already full
            val cars = carDAO.getCars(Some(garageId))
            if((cars.length + 1) > garage.maxCarCapacity) {
              val error = JsonError(UNPROCESSABLE_ENTITY, "Garage has reached its max capacity", None)
              Future.successful(UnprocessableEntity(Json.toJson(error)))
            } else {
              // Use path id only
              // Can be crossed with the one in the body for verification
              car.garageId = Some(garageId)
              val insertedId = carDAO.addCar(car)
              logger.debug(s"InsertedId, $insertedId")
              if(insertedId > -1) {
                Future.successful(Created.withHeaders(
                  ControllerConstants.HeaderFields.location -> routes.CarController.getCar(garageId, insertedId).absoluteURL())
                )  
              } else {
                val error = JsonError(INTERNAL_SERVER_ERROR, "An error occured on insertion", None)
                Future.successful(InternalServerError(Json.toJson(error)))
              }       
            }
          }
          // Check if garage exists
          checkGarageId(garageId, carProcessing)
        }
      }
    }
    ControllerUtils.handleException(callProcessing)
  }
  
  def getCars(garageId: Int) = action.async { implicit request: Request[AnyContent] =>
    def callProcessing() = {
      logger.debug(s"GarageID received : ${garageId.toString()}")
      def carProcessing(garage: Garage) = {
        val cars = carDAO.getCars(Some(garageId))
        logger.debug(s"cars found : ${cars.toString()}")
        Future.successful(Ok(Json.toJson(cars)))
      }
  
      // Check if garage exists
      checkGarageId(garageId, carProcessing)
    }
    
    ControllerUtils.handleException(callProcessing)
  }
    
  def getCar(garageId: Int, carId: Int) = action.async { implicit request: Request[AnyContent] =>
    def callProcessing() = {
      logger.debug(s"GarageID received : ${garageId.toString()}; CarID received : ${carId.toString()}")
      val car = carDAO.getCar(garageId, carId)
      if(car != null) {
        logger.debug(s"car found : ${car.toString()}")
        Future.successful(Ok(Json.toJson(car)))
      } else {
        Future.successful(NotFound)
      }
    }
    
    ControllerUtils.handleException(callProcessing)
  }
  
  def deleteCars(garageId: Int) = action.async { implicit request: Request[AnyContent] =>
    def callProcessing() = {
      logger.debug(s"GarageID received : ${garageId.toString()}")
      val deletedIds = carDAO.deleteCars(garageId, None)
      logger.debug(s"deletedIds : , $deletedIds")
      if(!deletedIds.isEmpty) {
        Future.successful(Ok)
      } else {
        Future.successful(NotFound)
      }
    }
    ControllerUtils.handleException(callProcessing)
  }
  
  def deleteCar(garageId: Int, carId: Int) = action.async { implicit request: Request[AnyContent] =>
    def callProcessing() = {
      logger.debug(s"GarageID received : ${garageId.toString()}; CarID received : ${carId.toString()}")
      val deletedIds = carDAO.deleteCars(garageId, Some(carId))
      logger.debug(s"deletedIds : , $deletedIds")
      if(!deletedIds.isEmpty) {
        Future.successful(Ok)
      } else {
        Future.successful(NotFound)
      }
    }
    ControllerUtils.handleException(callProcessing)
  }
    
  def updateCar(garageId: Int, carId: Int) = action.async { implicit request: Request[AnyContent] =>
    def callProcessing() = {
      Future.successful(Ok)
    }
    ControllerUtils.handleException(callProcessing)
  }
  
  private def checkGarageId(garageId: Int, callback: (Garage) => Future[play.api.mvc.Result]): Future[play.api.mvc.Result] = {
    // Check if garage exists
    val garageFound = garageDAO.getGarage(garageId)
    if(garageFound != null) {
      callback(garageFound)
    } else {
        val error = JsonError(NOT_FOUND, s"Garage identifier ${garageId} not found", None)
        Future.successful(NotFound(Json.toJson(error)))
    }
  }
}
