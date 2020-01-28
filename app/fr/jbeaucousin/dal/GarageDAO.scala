package fr.jbeaucousin.dal

import javax.inject.{Inject, Singleton }

import play.api.Logger
import play.api.db._

import fr.jbeaucousin.model.Garage
import fr.jbeaucousin.dal.definitions.GarageTableDefinitions

@Singleton
class GarageDAO @Inject()(db: Database){
  
  val logger: Logger = Logger(this.getClass())
  
  def addGarage(garage: Garage) = {
    var insertedId: Int = -1
    val conn = db.getConnection()

    if(garage != null) {
      try {
        val stmt = conn.createStatement
        val request = s"INSERT INTO ${GarageTableDefinitions.tableName}" + 
            s" (${GarageTableDefinitions.columns.name}, ${GarageTableDefinitions.columns.address}, ${GarageTableDefinitions.columns.creationDate}, ${GarageTableDefinitions.columns.maxCarCapacity}) " +
            s"VALUES('${garage.name}', '${garage.address}', '${garage.getDatabaseCreationDate}', ${garage.maxCarCapacity}) " +
            s"RETURNING ${GarageTableDefinitions.columns.id};"
        logger.debug(s"request : ${request}")
        val rs   = stmt.executeQuery(request)
  
        while (rs.next()) {
          logger.debug(s"inserted Id result : ${rs.toString()}")
          insertedId = rs.getInt(s"${GarageTableDefinitions.columns.id}")
        }
      } catch { 
        case e: Exception => logger.error(s"An error occured on addGarage", e)
      } finally {
        conn.close()
      }
    }
    insertedId
  }
  
  def getGarages = {
    val garages = List[Garage]()
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val request = s"SELECT * " +
          s"FROM ${GarageTableDefinitions.tableName};"
      
      logger.debug(s"request : ${request}")
      val rs = stmt.executeQuery(request)
      
      while (rs.next()) {
        logger.debug(s"inserted Id result : ${rs.toString()}")
        val id = rs.getInt(s"${GarageTableDefinitions.columns.id}")
        val name = rs.getString(s"${GarageTableDefinitions.columns.name}")
        val address = rs.getString(s"${GarageTableDefinitions.columns.address}")
        val creationDate = rs.getDate(s"${GarageTableDefinitions.columns.creationDate}")
        val maxCarCapacity = rs.getInt(s"${GarageTableDefinitions.columns.maxCarCapacity}")
        val currentGarage = Garage(Some(id), name, address, creationDate, maxCarCapacity)
            
        currentGarage :: garages 
      }
    } catch { 
        case e: Exception => logger.error(s"An error occured on getGarages", e)
    } finally {
      conn.close()
    }
    garages
  }
  
  def getGarage(id: Int) = {
    var garage: Garage = null
    val conn = db.getConnection()

    if(id != null) {
      try {
        val stmt = conn.createStatement
        val request = s"SELECT * " +
            s"FROM ${GarageTableDefinitions.tableName} " + 
            s"WHERE ${GarageTableDefinitions.tableName}.${GarageTableDefinitions.columns.id} =  ${id};"
        
        logger.debug(s"request : ${request}")
        val rs = stmt.executeQuery(request)
        
        while (rs.next()) {
          logger.debug(s"inserted Id result : ${rs.toString()}")
          val id = rs.getInt(s"${GarageTableDefinitions.columns.id}")
          val name = rs.getString(s"${GarageTableDefinitions.columns.name}")
          val address = rs.getString(s"${GarageTableDefinitions.columns.address}")
          val creationDate = rs.getDate(s"${GarageTableDefinitions.columns.creationDate}")
          val maxCarCapacity = rs.getInt(s"${GarageTableDefinitions.columns.maxCarCapacity}")
          
          garage = Garage(Some(id), name, address, creationDate, maxCarCapacity)
              
        }
      } catch { 
        case e: Exception => logger.error(s"An error occured on getGarage", e)
      } finally {
        conn.close()
      }
    }
    garage
  }
}