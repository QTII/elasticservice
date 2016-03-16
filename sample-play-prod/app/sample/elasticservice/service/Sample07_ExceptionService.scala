package sample.elasticservice.service

import scala.util.Try

import elasticservice.ElasticService
import elasticservice.util.ep.ElasticParams

class Sample07_ExceptionService extends ElasticService {
  def execute(reqEP: ElasticParams): Try[ElasticParams] = {
    Try {
      throw new Exception("Exception test")
    }
  }
}