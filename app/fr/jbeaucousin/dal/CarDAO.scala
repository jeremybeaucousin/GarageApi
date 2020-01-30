package fr.jbeaucousin.dal

import javax.inject.{Inject, Singleton }

import play.api.Logger
import play.api.db._

import scala.collection.mutable.ListBuffer
import java.sql.ResultSet

import fr.jbeaucousin.model.Car
import fr.jbeaucousin.dal.definitions.{ CarTableDefinitions, GarageTableDefinitions }
import java.sql.Statement

@Singleton
class CarDAO @Inject()(db: Database){
  
  val logger: Logger = Logger(this.getClass())
  
  def addCar(car: Car) = {
    var insertedId: Int = -1

    if(car != null) {
      def requestProcessing(stmt: Statement) = {
        val request = s"INSERT INTO ${CarTableDefinitions.tableName} " + 
            s"(${CarTableDefinitions.columns.licenceId}, ${CarTableDefinitions.columns.brand}, ${CarTableDefinitions.columns.model}, ${CarTableDefinitions.columns.price}, ${CarTableDefinitions.columns.garageId}) " +
            s"VALUES('${car.licenceId}', '${car.brand}', '${car.model}', ${car.price}, ${car.garageId.getOrElse(null)}) " +
            s"RETURNING ${CarTableDefinitions.columns.id};"
        logger.debug(s"request : ${request}")
        val rs   = stmt.executeQuery(request)
  
        while (rs.next()) {
          logger.debug(s"inserted Id result : ${rs.toString()}")
          insertedId = rs.getInt(s"${CarTableDefinitions.columns.id}")
        }
      }
      DAOUtils.handleConnection(db, requestProcessing)
    }
    insertedId
  }
  
  def updateCar(car: Car) = {
    var insertedId: Int = -1

    if(car != null) {
      def requestProcessing(stmt: Statement) = {
        val request = s"UPDATE ${CarTableDefinitions.tableName} " + 
            s"SET ${CarTableDefinitions.columns.licenceId} = '${car.licenceId}', ${CarTableDefinitions.columns.brand} = '${car.brand}', " +
            s"${CarTableDefinitions.columns.model} = '${car.model}', ${CarTableDefinitions.columns.price} = ${car.price} " +
            s"WHERE ${CarTableDefinitions.tableName}.${CarTableDefinitions.columns.garageId} = ${car.garageId.getOrElse(null)} " +
            s"AND ${CarTableDefinitions.tableName}.${CarTableDefinitions.columns.id} = ${car.id.getOrElse(null)} " +
            s"RETURNING ${CarTableDefinitions.columns.id};"
            
        logger.debug(s"request : ${request}")
        val rs   = stmt.executeQuery(request)
  
        while (rs.next()) {
          logger.debug(s"updated Id result : ${rs.toString()}")
          insertedId = rs.getInt(s"${CarTableDefinitions.columns.id}")
        }
      }
      DAOUtils.handleConnection(db, requestProcessing)
    }
    insertedId
  }

  def getCars(garageId: Option[Int]) = {
    val cars = new ListBuffer[Car]()

    def requestProcessing(stmt: Statement) = {
      var request = s"SELECT * " +
          s"FROM ${CarTableDefinitions.tableName} "
      
      if(garageId.isDefined) {
        request = request.concat(s"WHERE ${CarTableDefinitions.tableName}.${CarTableDefinitions.columns.garageId} = '${garageId.get}'")
      }
      request = request.concat(";")
      
      logger.debug(s"request : ${request}")
      val rs = stmt.executeQuery(request)
      
      while (rs.next()) {
        val currentCar = extractCar(rs)
        cars += currentCar
      }
    }
    DAOUtils.handleConnection(db, requestProcessing)
    cars
  }
  
  def getCar(garageId: Int, carId: Int) = {
    var car: Car = null

    if(garageId != null && carId != null) {
      def requestProcessing(stmt: Statement) = {
        val request = s"SELECT * " +
            s"FROM ${CarTableDefinitions.tableName} " + 
            s"WHERE ${CarTableDefinitions.tableName}.${CarTableDefinitions.columns.id} =  ${carId} " +
            s"AND ${CarTableDefinitions.tableName}.${CarTableDefinitions.columns.garageId} =  ${garageId};"
        
        logger.debug(s"request : ${request}")
        val rs = stmt.executeQuery(request)
        
        while (rs.next()) {
          car = extractCar(rs)
        }
      }
      DAOUtils.handleConnection(db, requestProcessing)
    }
    car
  }
  
  def deleteCars(garageId: Int, carId: Option[Int]) = {
    var deletedIds = new ListBuffer[Int]()

    if(garageId != null && carId != null) {
      def requestProcessing(stmt: Statement) = {
        var request = s"DELETE " +
            s"FROM ${CarTableDefinitions.tableName} " + 
            s"WHERE ${CarTableDefinitions.tableName}.${CarTableDefinitions.columns.garageId} =  ${garageId} "
        
        if(carId.isDefined) {
          request = request.concat(s"AND ${CarTableDefinitions.tableName}.${CarTableDefinitions.columns.id} =  ${carId.get} ")
        }
        request = request.concat(s"RETURNING ${CarTableDefinitions.columns.id};")
      
        logger.debug(s"request : ${request}")
        val rs = stmt.executeQuery(request)
        
        while (rs.next()) {
          logger.debug(s"deleted Id result : ${rs.toString()}")
          deletedIds += rs.getInt(GarageTableDefinitions.columns.id)
        }
      }
      
      DAOUtils.handleConnection(db, requestProcessing)
    }
    deletedIds
  }
  
  private def extractCar(rs: ResultSet) = {
    val id = rs.getInt(s"${CarTableDefinitions.columns.id}")
    val licenceId = rs.getString(s"${CarTableDefinitions.columns.licenceId}")
    val brand = rs.getString(s"${CarTableDefinitions.columns.brand}")
    val model = rs.getString(s"${CarTableDefinitions.columns.model}")
    val price = rs.getDouble(s"${CarTableDefinitions.columns.price}")
    val garageId = rs.getInt(s"${CarTableDefinitions.columns.garageId}")
    
    Car(Some(id), licenceId, brand, model, price, Some(garageId))
  }
}