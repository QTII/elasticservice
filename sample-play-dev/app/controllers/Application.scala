package controllers

import com.typesafe.scalalogging.LazyLogging

import play.api.mvc.Action
import play.api.mvc.Controller

class Application extends Controller with LazyLogging {

  def index = Action { request =>
//    logger.trace("trace...")
//    logger.debug("debug...")
//    logger.info("info...")
//    logger.warn("warn...")
//    logger.error("error...")

    Ok(views.html.index("Your new application is ready."))
  }

}
