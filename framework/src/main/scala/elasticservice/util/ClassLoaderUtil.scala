package elasticservice.util

import scala.util.Try

object ClassLoaderUtil {
  def forName(className: String, classLoader: ClassLoader): Try[Class[_]] =
    Try(Class.forName(className, true, classLoader)).orElse(forName(className))

  def forName(className: String): Try[Class[_]] =
  	for {
  		c <- Try(Class.forName(className))
  	} yield {
  		val cl = c.getClassLoader()
      if (cl != null)
        cl.setDefaultAssertionStatus(false)
      c
  	}
}