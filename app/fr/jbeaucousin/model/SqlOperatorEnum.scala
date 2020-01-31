package fr.jbeaucousin.model

object SqlOperatorEnum extends Enumeration {
  type SqlOperatorEnum = Value
  
  val equals = Value("=") 
  val greaterThan = Value(">")
  val greaterThanEquals = Value(">=") 
  val lowerThan = Value("<")
  val lowerThanEquals = Value("<=") 
}