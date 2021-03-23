import org.xml.sax.{Attributes, SAXException}
import org.xml.sax.helpers.DefaultHandler

import scala.collection.mutable.ListBuffer

class OsmHandler extends DefaultHandler {
  private var data: StringBuilder = null
  private var way: Way = Way(0)

  val nodes: ListBuffer[Node] = ListBuffer.empty[Node]
  val ways: ListBuffer[Way] = ListBuffer.empty[Way]

  @throws[SAXException]
  override def startElement(uri: String, localName: String, qName: String, attributes: Attributes): Unit = {
    qName match {
      case "node" =>
        nodes.addOne(Node(
          attributes.getValue("id").toLong,
          attributes.getValue("lat").toFloat,
          attributes.getValue("lon").toFloat
        ))
      case "way" =>
        way = Way(attributes.getValue("id").toLong)
      case "nd" =>
        way.addNode(attributes.getValue("ref").toDouble)
      case "tag" => attributes.getValue("k") match {
        case "highway" => way.setIsHighway()
        case "name" => way.setName(attributes.getValue("name"))
        case _ => ()
      }
      case _ => ()
    }
    data = new StringBuilder()
  }

  @throws[SAXException]
  override def endElement(uri: String, localName: String, qName: String): Unit = {
    if (qName.equals("way") && way.isHighway){ways.addOne(way)}
  }

  @throws[SAXException]
  override def characters(ch: Array[Char], start: Int, length: Int): Unit = {
    data.append(new String(ch, start, length))
  }
}