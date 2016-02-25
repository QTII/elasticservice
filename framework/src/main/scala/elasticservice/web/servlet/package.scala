package elasticservice.web

package object servlet {
  case class EncryptEnv(name: String, algorithm: String, operationMode:  String, padding:  String, key: Array[Byte], iv: Array[Byte])
}