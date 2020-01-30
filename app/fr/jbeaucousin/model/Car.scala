package fr.jbeaucousin.model

import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.util.Date
import java.text.{ SimpleDateFormat, DateFormat }
import fr.jbeaucousin.dal.definitions.CarJsonDefinitions

case class Car(id: Option[Int], licenceId: String, brand: String, model: String, price: Double, var garageId: Option[Int]) 

object Car {
  implicit val carWrites = new Writes[Car] {
    def writes(car: Car) = Json.obj(
      CarJsonDefinitions.columns.id  -> car.id,
      CarJsonDefinitions.columns.licenceId -> car.licenceId,
      CarJsonDefinitions.columns.brand -> car.brand,
      CarJsonDefinitions.columns.model -> car.model,
      CarJsonDefinitions.columns.price -> car.price,
      CarJsonDefinitions.columns.garageId -> car.garageId
    )
  }
  
  implicit val carReads: Reads[Car] = (
      (JsPath \ CarJsonDefinitions.columns.id).readNullable[Int] and
      (JsPath \ CarJsonDefinitions.columns.licenceId).read[String] and
      (JsPath \ CarJsonDefinitions.columns.brand).read[String] and
      (JsPath \ CarJsonDefinitions.columns.model).read[String] and
      (JsPath \ CarJsonDefinitions.columns.price).read[Double] and
      (JsPath \ CarJsonDefinitions.columns.garageId).readNullable[Int] 
    )(Car.apply _)
}