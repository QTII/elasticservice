package elasticservice.util

import scala.reflect.runtime.{ universe => ru }

object ReflectionUtil {

  /**
   * Example:
   * case class MyClass(id:Long, name:String)
   * val list = List[AnyRef](new java.lang.Long(1), "a name")
   * val result = newInstance[MyClass](list)
   * println(result.id)
   */
  def newInstance[T](classArgs: List[AnyRef])(implicit m: Manifest[T]): T = {
    if (DataValid.isEmpty(classArgs)) m.runtimeClass.newInstance().asInstanceOf[T]
    else m.runtimeClass.getConstructors()(0).newInstance(classArgs: _*).asInstanceOf[T]
  }

  def newInstance(clazz: Class[_], classArgs: List[AnyRef]): Any = {
    if (DataValid.isEmpty(classArgs)) clazz.newInstance()
    else clazz.getConstructors()(0).newInstance(classArgs: _*)
  }

  def newInstance(className: String, classArgs: List[AnyRef]): Any = {
    val cl = Thread.currentThread().getContextClassLoader
    val clazz = Class.forName(className, true, cl)
    newInstance(clazz, classArgs)
  }

  def invokeMethod(instance: AnyRef, methodName: String)(args: Any*): Any = {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(instance)
    val method = ru.typeOf[instance.type]
      .decl(ru.TermName(methodName)).asMethod
    val methodRun = im.reflectMethod(method)
    methodRun(args)
  }
}