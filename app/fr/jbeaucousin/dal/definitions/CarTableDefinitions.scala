package fr.jbeaucousin.dal.definitions

final object CarTableDefinitions {
  final val tableName = "car"
  
  final object columns {
    final val id = "id"
    final val licenceId = "licence_id"
    final val brand = "brand"
    final val model = "model"
    final val price = "price"
    final val garageId = "garage_id"
  }
}