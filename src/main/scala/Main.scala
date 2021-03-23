import org.apache.tools.bzip2.CBZip2InputStream

import java.io.InputStream
import java.net.URL
import javax.xml.parsers.SAXParserFactory

object Main {
  def main(args: Array[String]): Unit = {
    val resource_url: String = "https://download.geofabrik.de/europe/great-britain/england/west-sussex-latest.osm.bz2"
    val stream: InputStream = new URL(resource_url).openStream
    (0 until 2).foreach(_ => stream.read())
    val resource: InputStream = new CBZip2InputStream(stream, true)

    val saxParserFactory: SAXParserFactory = SAXParserFactory.newInstance()
    val saxParser = saxParserFactory.newSAXParser
    val handler = new OsmHandler
    saxParser.parse(resource, handler)

    handler.nodes.foreach(println)
    handler.ways.foreach(println)
  }
}