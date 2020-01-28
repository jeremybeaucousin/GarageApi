package fr.jbeaucousin.dal.definitions

final object GarageTableDefinitions {
  final val tableName = "garage"
  
  final object columns {
    final val id = "id"
    final val name = "name"
    final val address = "address"
    final val creationDate = "creation_date"
    final val maxCarCapacity = "max_car_capacity"
  }
}