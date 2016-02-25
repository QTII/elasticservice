import elasticservice.util.ep.MkString
import elasticservice.util.ep.json13.JSON13MkString

package object elasticservice {
  implicit val epMkString: MkString = JSON13MkString

  def currentMethodName(): String = Thread.currentThread.getStackTrace()(2).getMethodName
  def traceOut(callClass: Class[_], methodName: String, id: String, outStr: String) {
    println("[%s.%s] %s -> %s".format(callClass.getName, methodName, id, outStr))
  }
}