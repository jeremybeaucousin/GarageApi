package fr.jbeaucousin.dal

import javax.inject.{Inject, Singleton }

import play.api.Logger
import play.api.db._

import java.sql.ResultSet
import java.sql.Statement

import fr.jbeaucousin.model.Garage
import fr.jbeaucousin.dal.definitions.GarageTableDefinitions

@Singleton
class GarageDAO @Inject()(
    db: Database) {
  
  val logger: Logger = Logger(this.getClass())
  
  def addGarage(garage: Garage) = {
    var insertedId: Int = -1

    if(garage != null) {
      def requestProcessing(stmt: Statement) = {
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
      } 
      DAOUtils.handleConnection(db, requestProcessing)
    }
    insertedId
  }
  
  def getGarage(id: Int) = {
    var garage: Garage = null

    if(id != null) {
      def requestProcessing(stmt: Statement) = {
        val request = s"SELECT * " +
            s"FROM ${GarageTableDefinitions.tableName} " + 
            s"WHERE ${GarageTableDefinitions.tableName}.${GarageTableDefinitions.columns.id} =  ${id};"
        
        logger.debug(s"request : ${request}")
        val rs = stmt.executeQuery(request)
        
        while (rs.next()) {
          garage = extractGarage(rs)
        }
      }
      DAOUtils.handleConnection(db, requestProcessing)
    }
    garage
  }
  
  def deleteGarage(id: Int) = {
    var deletedId: Int = -1

    if(id != null) {
      def requestProcessing(stmt: Statement) = {
        val request = s"DELETE " +
            s"FROM ${GarageTableDefinitions.tableName} " + 
            s"WHERE ${GarageTableDefinitions.tableName}.${GarageTableDefinitions.columns.id} =  ${id} " +
            s"RETURNING ${GarageTableDefinitions.columns.id};"
        
        logger.debug(s"request : ${request}")
        val rs = stmt.executeQuery(request)
        
        while (rs.next()) {
          logger.debug(s"deleted Id result : ${rs.toString()}")
          deletedId = rs.getInt(GarageTableDefinitions.columns.id)
        }
      }
      DAOUtils.handleConnection(db, requestProcessing)
    }
    deletedId
  }
  
  private def extractGarage(rs: ResultSet) = {
    val id = rs.getInt(s"${GarageTableDefinitions.columns.id}")
    val name = rs.getString(s"${GarageTableDefinitions.columns.name}")
    val address = rs.getString(s"${GarageTableDefinitions.columns.address}")
    val creationDate = rs.getDate(s"${GarageTableDefinitions.columns.creationDate}")
    val maxCarCapacity = rs.getInt(s"${GarageTableDefinitions.columns.maxCarCapacity}")
          
    Garage(Some(id), name, address, creationDate, maxCarCapacity)
  }
}