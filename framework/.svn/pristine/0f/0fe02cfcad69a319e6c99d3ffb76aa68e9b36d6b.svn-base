package elasticservice

import scala.xml.Elem

import com.typesafe.scalalogging.LazyLogging

import elasticservice.util.Debug

object EEnv extends LazyLogging {
  var webSocketPort = -1
  var webSocketServerClass: Class[_] = null
  var webSocketInMessageEventHandlerMaxThreads = -1

  def init(config: Elem) {
    initWebSocket(config)
  }

  private def initWebSocket(config: Elem) {
    webSocketPort = (config \ "webSocket" \@ "port").toInt
    if (webSocketPort < 1)
      return

    Debug.sysOut("WebSocket port: " + webSocketPort)

    val className = config \ "webSocket" \@ "class"

    Debug.sysOut("webSocketServerClass: " + className)

    webSocketServerClass = Class.forName(className)

    webSocketInMessageEventHandlerMaxThreads = (config \ "webSocket" \ "inMessageEventHandler" \@ "maxThreads").toInt
    if (webSocketInMessageEventHandlerMaxThreads > 0)
      Debug.sysOut("WebSocket inMessageEventHandler.maxThreads: " + webSocketInMessageEventHandlerMaxThreads)
    else {
      webSocketInMessageEventHandlerMaxThreads = 100
      Debug.sysOut("WebSocket inMessageEventHandler.maxThreads: " + webSocketInMessageEventHandlerMaxThreads + " by default")
    }
  }
}