package fr.jbeaucousin.model

import fr.jbeaucousin.model.SqlOperatorEnum.SqlOperatorEnum

case class SqlCondition (operator: SqlOperatorEnum, value: String)