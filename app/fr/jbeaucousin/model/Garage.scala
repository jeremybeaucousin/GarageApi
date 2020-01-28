package fr.jbeaucousin.model

import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.util.Date
import java.text.{ SimpleDateFormat, DateFormat }
import fr.jbeaucousin.dal.definitions.GarageJsonDefinitions

case class Garage(id: Option[Int], name: String, address: String, creationDate: Date, maxCarCapacity: Int) {
  
  def getDatabaseCreationDate = {
    val formatter: DateFormat = new SimpleDateFormat("yyyy-MM-dd");
    formatter.format(this.creationDate);
  }
}

object Garage {
  implicit val garageWrites = new Writes[Garage] {
    def writes(garage: Garage) = Json.obj(
      GarageJsonDefinitions.columns.id  -> garage.id,
      GarageJsonDefinitions.columns.name -> garage.name,
      GarageJsonDefinitions.columns.address -> garage.address,
      GarageJsonDefinitions.columns.creationDate -> garage.getDatabaseCreationDate,
      GarageJsonDefinitions.columns.maxCarCapacity -> garage.maxCarCapacity
    )
  }
  
  implicit val garageReads: Reads[Garage] = (
      (JsPath \ GarageJsonDefinitions.columns.id).readNullable[Int] and
      (JsPath \ GarageJsonDefinitions.columns.name).read[String] and
      (JsPath \ GarageJsonDefinitions.columns.address).read[String] and
      (JsPath \ GarageJsonDefinitions.columns.creationDate).read[Date] and
      (JsPath \ GarageJsonDefinitions.columns.maxCarCapacity).read[Int]      
    )(Garage.apply _)
}