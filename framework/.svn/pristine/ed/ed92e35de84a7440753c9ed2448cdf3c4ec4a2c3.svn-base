package elasticservice.util.nio

import java.nio.channels.ReadableByteChannel
import scala.util.Try
import com.typesafe.scalalogging.LazyLogging
import elasticservice.util.ExceptionDetail
import scala.util.Failure
import scala.util.Success

object ChannelUtil extends LazyLogging {
	def readUntilEndOfStream(channel: ReadableByteChannel, presumedLength: Int): Option[Array[Byte]] = {
		val bbUtil = ByteBufferUtil(if (presumedLength > 0) presumedLength else 1024)
	
		@annotation.tailrec
		def loop(len: Int, total: Int): Int = {
			if (len == -1) total
			else {
				val l = bbUtil.read(channel, 1)
				loop(l, total + l)
			}
		}
		
    bbUtil.retrieve(loop(0, 0))
	}
}