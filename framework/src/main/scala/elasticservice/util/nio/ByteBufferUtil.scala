package elasticservice.util.nio

import java.nio.ByteBuffer
import java.nio.channels.Channel
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SocketChannel

import com.typesafe.scalalogging.LazyLogging

object ByteBufferUtil {
  def apply(capacity: Int) = new ByteBufferUtil(capacity)
}

class ByteBufferUtil(capacity: Int) extends LazyLogging {
	private var buf = ByteBuffer.allocateDirect(capacity + 1)
	private var begin = 0
	private var end = 0
	
	private def size(): Int = end - begin
	
	def retrieve(length: Int): Option[Array[Byte]] = {
		if (size() == 0) {
			None
		} else {
  		val max = if (length <= size()) length else size()
  		val dst = new Array[Byte](max)
  		buf.position(begin)
  		buf.get(dst, 0, max)
  		begin = buf.position()
  		Some(dst)
		}
	}
	
	private def checkRemaining(requiredRemaining: Int) {
		val oldRem = buf.remaining()
		if (oldRem < requiredRemaining) {
  		if ((oldRem + begin) >= requiredRemaining) {
  			val pos = buf.position()
  			buf.position(begin)
  			buf.compact()
  			buf.position(pos - begin)
  			end = end - begin
  			begin = 0
  		} else {
  			val pos = buf.position()
  			val oldCapa = buf.capacity()
  			val newCapa = 
  			  oldCapa + (if (oldCapa / 4 > requiredRemaining - oldRem) oldCapa / 4 else (requiredRemaining - oldRem) * 2)
  			val tmp = ByteBuffer.allocateDirect(newCapa)
  			tmp.put(buf.position(begin).asInstanceOf[ByteBuffer])
  			tmp.position(pos - begin)
  			buf = tmp
  			end = end - begin
  			begin = 0
  
  			logger.trace("### allocated: pos=" + buf.position() + ", reqRem=" + requiredRemaining + ", oldRem=" + (oldRem + begin) + ", oldCapa=" + oldCapa + ", newCapa=" + newCapa)
  		}
		}
	}

	def read(channel: Channel, requiredRemaining: Int): Int = {
		var len = 0
		val pos = buf.position()
		if (pos < end)
			buf.position(end)
		checkRemaining(requiredRemaining)
		
		channel match {
		  case sc: SocketChannel => len = sc.read(buf)
		  case fc: FileChannel => len = fc.read(buf)
		  case rbc: ReadableByteChannel => len = rbc.read(buf)
//		  case sbc: ScatteringByteChannel => len = sbc.read(buf)
		}
		
		end = buf.position()
		if (len == 0 && pos < end)
			len = end - pos
		
    len
	}
}