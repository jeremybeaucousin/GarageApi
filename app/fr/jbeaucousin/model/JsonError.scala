package fr.jbeaucousin.model

import play.api.libs.json._

case class JsonError(code: Int, message: String)

object JsonError {
  implicit val JsonErrorWrites = new Writes[JsonError] {
    def writes(jsonError: JsonError) = Json.obj(
      "code"  -> jsonError.code,
      "message" -> jsonError.message
    )
  }  
}
