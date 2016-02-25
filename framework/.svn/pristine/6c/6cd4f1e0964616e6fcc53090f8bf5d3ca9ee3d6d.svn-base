package elasticservice.util

import elasticservice.util.CommonUtil._

object ExceptionDetail {
  def getDetail(t: Throwable): String = getDetail(None, t)

  def getDetail(tag: Option[String], t: Throwable): String = getDetailInternal(tag, t)
		
	private def getDetailInternal(tag: Option[String], t: Throwable): String = {
		val detail = StringBuilder.newBuilder
		appendHeader(tag, t, detail)
		appendBody(t, detail);

		val cause = t.getCause()
		if (cause != null)
			detail.append(NEW_LINE + "Caused by: " + getDetailInternal(tag, cause))
		
		detail.toString()
	}

  private def appendHeader(tag: Option[String], t: Throwable, detail: StringBuilder) {
	  detail.append(t.getClass().getName() + tag.map(": " + _).getOrElse(""))

		val msg = t.getMessage()
		if (msg != null) detail.append(": " + msg + NEW_LINE)
		else detail.append(NEW_LINE);
	}

  private def appendBody(t: Throwable, detail: StringBuilder) {
		val stes = t.getStackTrace()
		stes.foreach { appendLineDetail(detail, _) }
	}

  private def appendLineDetail(detail: StringBuilder, ste: StackTraceElement) = 
	  detail.append("\tat " + ste.getClassName() + "." + ste.getMethodName() + "():" + ste.getLineNumber() + NEW_LINE)
}