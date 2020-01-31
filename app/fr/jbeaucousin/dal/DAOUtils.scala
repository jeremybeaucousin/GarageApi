package fr.jbeaucousin.dal

import play.api.Logger
import play.api.db.Database

import java.sql.Statement

import fr.jbeaucousin.model.{ TechnicalException, QueryParamOperatorEnum, SqlCondition, SqlOperatorEnum }
import fr.jbeaucousin.model.QueryParamOperatorEnum.QueryParamOperatorEnum
import fr.jbeaucousin.model.SqlOperatorEnum.SqlOperatorEnum
import fr.jbeaucousin.controllers.ControllerConstants
import scala.collection.mutable.ListBuffer

object DAOUtils {
  
  val logger: Logger = Logger(this.getClass())
  
  def handleConnection(db : Database, callback: (Statement) => Unit) = {
    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement
      callback(stmt)
    } catch { 
      case e: Exception => {
        logger.error("An error occured during request", e)
        throw new TechnicalException("An error occured during connection to database", e)
      }
    } finally {
      conn.close()
    }
  }
  
  def extractConditions(conditionsString: String): ListBuffer[SqlCondition] = {
    val sqlConditions = new ListBuffer[SqlCondition]()
    if(conditionsString != null) {
      val conditions = conditionsString.split(ControllerConstants.QueryParams.CommaSeparator);
      for(condition <- conditions) {
        logger.debug(condition)
        val conditionArray = condition.split(ControllerConstants.QueryParams.ColonSeparator);
        if(conditionArray.length > 2) {
          logger.warn(s"The condition ${conditionsString} contains to much colon separator, only first will be processed")
        } else if (conditionArray.length == 1) {
          sqlConditions += SqlCondition(SqlOperatorEnum.equals, conditionArray(0))
        } else {
          val queryOperator = conditionArray(0)
          val value = conditionArray(1)
          
          logger.debug(s"queryOperator : ${queryOperator}; value : ${value}")
          
          // Operator e
          var sqlOperator: SqlOperatorEnum = SqlOperatorEnum.equals
          if(queryOperator == QueryParamOperatorEnum.greaterThanEquals.toString()) {
            sqlOperator = SqlOperatorEnum.greaterThanEquals
          } else if (queryOperator == QueryParamOperatorEnum.greaterThan.toString()) {
            sqlOperator = SqlOperatorEnum.greaterThan
          } else if (queryOperator == QueryParamOperatorEnum.lowerThanEquals.toString()) {
            sqlOperator = SqlOperatorEnum.lowerThanEquals
          } else if (queryOperator == QueryParamOperatorEnum.lowerThan.toString()) {
            sqlOperator = SqlOperatorEnum.lowerThan
          }
          
          sqlConditions += SqlCondition(sqlOperator, value)
        }
        
      }
    }
    sqlConditions
  }
  
  def generateConditionRequest(conditionsList: ListBuffer[SqlCondition], tableName: String, columnName: String) = {
    var conditionRequest = ""
    if(conditionsList != null && !conditionsList.isEmpty) {
      conditionRequest = "AND ("
      for(conditionIndex <- 0 until (conditionsList.length)) {
        logger.debug("conditionIndex")
        logger.debug(conditionIndex.toString())
        val condition = conditionsList(conditionIndex)
        conditionRequest = conditionRequest.concat(s"${tableName}.${columnName} ${condition.operator} ${condition.value} ")
        if (conditionIndex < (conditionsList.length -1)) {
          val nextCondition = conditionsList(conditionIndex + 1)
          // Manage the case of a between conditions
          if(condition.operator != SqlOperatorEnum.equals && nextCondition.operator != SqlOperatorEnum.equals) {
            conditionRequest = conditionRequest.concat("AND ")
          } else {
            conditionRequest = conditionRequest.concat("OR ")
          }
          
        } else {
          conditionRequest = conditionRequest.concat(") ")
        }
      }
    }
    conditionRequest
  }
}