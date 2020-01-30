package fr.jbeaucousin.dal

import javax.inject.{Inject, Singleton }

import play.api.Logger
import play.api.db._

import fr.jbeaucousin.model.Car
import fr.jbeaucousin.dal.definitions.{ CarTableDefinitions, GarageTableDefinitions }
import scala.collection.mutable.ListBuffer

@Singleton
class CarDAO @Inject()(db: Database){
  
  val logger: Logger = Logger(this.getClass())
  
  def addGarage(car: Car) = {
    var insertedId: Int = -1
    val conn = db.getConnection()

    if(car != null) {
      try {
        val stmt = conn.createStatement
        val request = s"INSERT INTO ${CarTableDefinitions.tableName}" + 
            s" (${CarTableDefinitions.columns.licenceId}, ${CarTableDefinitions.columns.brand}, ${CarTableDefinitions.columns.model}, ${CarTableDefinitions.columns.price}, ${CarTableDefinitions.columns.garageId}) " +
            s"VALUES('${car.licenceId}', '${car.brand}', '${car.model}', ${car.price}, ${car.garageId.getOrElse(null)}) " +
            s"RETURNING ${CarTableDefinitions.columns.id};"
        logger.debug(s"request : ${request}")
        val rs   = stmt.executeQuery(request)
  
        while (rs.next()) {
          logger.debug(s"inserted Id result : ${rs.toString()}")
          insertedId = rs.getInt(s"${CarTableDefinitions.columns.id}")
        }
      } catch { 
        case e: Exception => logger.error(s"An error occured on addGarage", e)
      } finally {
        conn.close()
      }
    }
    insertedId
  }

  def getCars(garageId: Option[Int]) = {
    val cars = new ListBuffer[Car]()
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      var request = s"SELECT * " +
          s"FROM ${CarTableDefinitions.tableName} "
      
      if(garageId.isDefined) {
        request = request.concat(s"WHERE ${CarTableDefinitions.tableName}.${CarTableDefinitions.columns.garageId} = '${garageId.get}'")
      }
      request = request.concat(";")
      
      logger.debug(s"request : ${request}")
      val rs = stmt.executeQuery(request)
      
      while (rs.next()) {
        val id = rs.getInt(s"${CarTableDefinitions.columns.id}")
        val licenceId = rs.getString(s"${CarTableDefinitions.columns.licenceId}")
        val brand = rs.getString(s"${CarTableDefinitions.columns.brand}")
        val model = rs.getString(s"${CarTableDefinitions.columns.model}")
        val price = rs.getDouble(s"${CarTableDefinitions.columns.price}")
        val garageId = rs.getInt(s"${CarTableDefinitions.columns.garageId}")
        
        val currentCar = Car(Some(id), licenceId, brand, model, price, Some(garageId))
        
        cars += currentCar
      }
    } catch { 
        case e: Exception => logger.error(s"An error occured on getCars", e)
    } finally {
      conn.close()
    }
    cars
  }
  
//  def getGarage(id: Int) = {
//    var garage: Garage = null
//    val conn = db.getConnection()
//
//    if(id != null) {
//      try {
//        val stmt = conn.createStatement
//        val request = s"SELECT * " +
//            s"FROM ${GarageTableDefinitions.tableName} " + 
//            s"WHERE ${GarageTableDefinitions.tableName}.${GarageTableDefinitions.columns.id} =  ${id};"
//        
//        logger.debug(s"request : ${request}")
//        val rs = stmt.executeQuery(request)
//        
//        while (rs.next()) {
//          logger.debug(s"inserted Id result : ${rs.toString()}")
//          val id = rs.getInt(s"${GarageTableDefinitions.columns.id}")
//          val name = rs.getString(s"${GarageTableDefinitions.columns.name}")
//          val address = rs.getString(s"${GarageTableDefinitions.columns.address}")
//          val creationDate = rs.getDate(s"${GarageTableDefinitions.columns.creationDate}")
//          val maxCarCapacity = rs.getInt(s"${GarageTableDefinitions.columns.maxCarCapacity}")
//          
//          garage = Garage(Some(id), name, address, creationDate, maxCarCapacity)
//              
//        }
//      } catch { 
//        case e: Exception => logger.error(s"An error occured on getGarage", e)
//      } finally {
//        conn.close()
//      }
//    }
//    garage
//  }
//  
//  def deleteGarage(id: Int) = {
//    var deletedId: Int = -1
//    val conn = db.getConnection()
//
//    if(id != null) {
//      try {
//        val stmt = conn.createStatement
//        val request = s"DELETE " +
//            s"FROM ${GarageTableDefinitions.tableName} " + 
//            s"WHERE ${GarageTableDefinitions.tableName}.${GarageTableDefinitions.columns.id} =  ${id} " +
//            s"RETURNING ${GarageTableDefinitions.columns.id};"
//        
//        logger.debug(s"request : ${request}")
//        val rs = stmt.executeQuery(request)
//        
//        while (rs.next()) {
//          logger.debug(s"deleted Id result : ${rs.toString()}")
//          deletedId = rs.getInt(GarageTableDefinitions.columns.id)
//        }
//      } catch { 
//        case e: Exception => logger.error(s"An error occured on getGarage", e)
//      } finally {
//        conn.close()
//      }
//    }
//    deletedId
//  }
}