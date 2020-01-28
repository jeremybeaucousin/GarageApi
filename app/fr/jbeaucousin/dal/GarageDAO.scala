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
    var insertedId: String = null
    val conn = db.getConnection()

    if(garage != null) {
      try {
        val stmt = conn.createStatement
        val request = s"INSERT INTO ${GarageTableDefinitions.tableName}" + 
            s" (${GarageTableDefinitions.columns.name}, ${GarageTableDefinitions.columns.address}, ${GarageTableDefinitions.columns.creationDate}, ${GarageTableDefinitions.columns.maxCarCapacity}) " +
            s"VALUES('${garage.name}', '${garage.address}', '${garage.getDatabaseCreationDate}', ${garage.maxCarCapacity}) " +
            "RETURNING id;"
        logger.debug(s"request : ${request}")
        val rs   = stmt.executeQuery(request)
  
        while (rs.next()) {
          logger.debug(s"inserted Id result : ${rs.toString()}")
          insertedId = rs.getString("id")
        }
      } finally {
        conn.close()
      }
    }
    insertedId
  }
}