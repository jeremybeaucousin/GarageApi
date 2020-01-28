package fr.jbeaucousin.dal

import javax.inject.{Inject, Singleton }

import play.api.Logger
import play.api.db._

import fr.jbeaucousin.model.Car
import fr.jbeaucousin.dal.definitions.CarTableDefinitions

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
            s"VALUES('${car.licenceId}', '${car.brand}', '${car.model}', ${car.price}, ${car.garageId}) " +
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
  
//  def getGarages = {
//    val garages = List[Garage]()
//    val conn = db.getConnection()
//
//    try {
//      val stmt = conn.createStatement
//      val request = s"SELECT * " +
//          s"FROM ${GarageTableDefinitions.tableName};"
//      
//      logger.debug(s"request : ${request}")
//      val rs = stmt.executeQuery(request)
//      
//      while (rs.next()) {
//        logger.debug(s"inserted Id result : ${rs.toString()}")
//        val id = rs.getInt(s"${GarageTableDefinitions.columns.id}")
//        val name = rs.getString(s"${GarageTableDefinitions.columns.name}")
//        val address = rs.getString(s"${GarageTableDefinitions.columns.address}")
//        val creationDate = rs.getDate(s"${GarageTableDefinitions.columns.creationDate}")
//        val maxCarCapacity = rs.getInt(s"${GarageTableDefinitions.columns.maxCarCapacity}")
//        val currentGarage = Garage(Some(id), name, address, creationDate, maxCarCapacity)
//            
//        currentGarage :: garages 
//      }
//    } catch { 
//        case e: Exception => logger.error(s"An error occured on getGarages", e)
//    } finally {
//      conn.close()
//    }
//    garages
//  }
  
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