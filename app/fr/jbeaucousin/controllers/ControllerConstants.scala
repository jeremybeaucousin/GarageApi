package fr.jbeaucousin.controllers

final object ControllerConstants {
  final object HeaderFields {
    final val xApiKey = "X-Api-Key"
    final val xAuthToken = "X-Auth-Token"
    final val location: String = "Location"
    final val xTotalCount: String = "X-Total-Count"
    final val link: String = "link"
  }

  final object HeadersKey {
    final val Authorization = "Authorization"
    final val ApiKey = "ApiKey"
    final val Basic = "Basic"
  }
  
  final object QueryParams {
    final val CommaSeparator = ","
    final val ColonSeparator = ":"
  }
}