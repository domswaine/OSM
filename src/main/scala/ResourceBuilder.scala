import org.apache.tools.bzip2.CBZip2InputStream

import java.io.{BufferedWriter, File, InputStream, FileWriter}
import java.net.URL
import javax.xml.parsers.SAXParserFactory
import scala.collection.mutable.ListBuffer

class ResourceBuilder {
  def get_nodes_and_ways(): (ListBuffer[Node], ListBuffer[Way]) = {
    val resource_url: String = "https://download.geofabrik.de/europe/great-britain/england/west-sussex-latest.osm.bz2"
    val stream: InputStream = new URL(resource_url).openStream
    (0 until 2).foreach(_ => stream.read())
    val resource: InputStream = new CBZip2InputStream(stream, true)
    val saxParserFactory: SAXParserFactory = SAXParserFactory.newInstance()
    val saxParser = saxParserFactory.newSAXParser
    val handler = new OsmHandler
    saxParser.parse(resource, handler)
    (handler.nodes, handler.ways)
  }

  var (nodes: ListBuffer[Node], ways: ListBuffer[Way]) = get_nodes_and_ways()
  within_distance()

  private def within_distance(e:Int=529315, n:Int=106240, dist:Float=3000): Unit = {
    nodes.filterInPlace(node => node.distance(n, e) <= dist)
    val nodes_ids: Set[Long] = nodes.map(node => node.id).toSet
    ways.filterInPlace(way => way.nodes.toSet.intersect(nodes_ids).nonEmpty)
    val highway_nodes: Set[Long] = ways.flatMap(way => way.nodes).toSet
    nodes.filterInPlace(node => highway_nodes.contains(node.id))
  }

  def write_to_file(filepath: String, content: String): Unit = {
    val bw = new BufferedWriter(new FileWriter(new File(filepath).getAbsolutePath))
    bw.write(content)
    bw.close()
  }

  def create_node_list(): Unit = {
    write_to_file(
      "nodes.csv",
      nodes.toList.map(n => n.to_record).mkString("\n")
    )
  }
}

object ResourceBuilder {
  def main(args: Array[String]): Unit = {
    val rb = new ResourceBuilder()
    rb.create_node_list()
  }
}