package fr.jbeaucousin.model

object QueryParamOperatorEnum extends Enumeration {
  type QueryParamOperatorEnum = Value
  
  val equals = Value("e") 
  val greaterThan = Value("gt")
  val greaterThanEquals = Value("gte") 
  val lowerThan = Value("lt")
  val lowerThanEquals = Value("lte") 
}