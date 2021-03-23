import java.net.URL

import org.apache.tools.bzip2.CBZip2InputStream

import scala.io.BufferedSource

object Iterators {

  def bz2_url_to_iterator(url: String): Iterator[String] = {
    val stream = new URL(url).openStream
    (0 until 2).foreach(_ => stream.read())
    new BufferedSource(new CBZip2InputStream(stream, true)).getLines()
  }

}