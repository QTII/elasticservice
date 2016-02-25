package elasticservice.util

object Debug {
  def sysOut(msg: String) {
		val thread = Thread.currentThread()
		val stes = thread.getStackTrace()
		
		@annotation.tailrec
		def loop(n: Int): Unit =
			if (n >= stes.length) ()
			else if (stes(n).getClassName() == Debug.getClass.getName() && stes(n).getMethodName() == "sysOut") {
				val tmp = stes(n + 1)
				println("[%s] %s.%s():%d: %s%n", thread.getName(), tmp.getClassName(), tmp.getMethodName(), tmp.getLineNumber(), msg)
			} else
				loop(n + 1)
				
		loop(0)
	}
}