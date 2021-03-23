import scala.annotation.tailrec
import scala.util.matching.Regex

class SucceedingTagUtility(val iterator: Iterator[String], val rel_tags: Set[String]) {

  val opening_tag: Regex = "<[a-zA-z]+[^>/]*>".r
  val self_closing_tag: Regex = "<[a-zA-z]+[^>/]*/ ?>".r
  val closing_tag: Regex = "</[a-zA-z]+[^>/]*>".r
  val get_tag_name: Regex = "</?([a-zA-z]+)[^>/]*/?>".r

  def get_tag(line: String): Option[String] = {
    get_tag_name.findAllIn(line).matchData.map(m => m.group(1)).toList match {
      case a::_ => Some(a)
      case _ => None
    }
  }

  @tailrec
  final def get_succeeding_tag(lines: List[String] = Nil, stack: List[String] = Nil): Option[String] = {
    if(stack.isEmpty && lines.nonEmpty){Some(lines.reverse.mkString("\n"))}
    else if(iterator.hasNext) {
      val line: String = iterator.next().trim
      stack match {
        case Nil => (line, get_tag(line)) match {
          case (l: String, Some(tag)) if opening_tag.matches(l) && rel_tags.contains(tag) =>
            get_succeeding_tag(lines = l :: lines, stack = tag :: stack)
          case _ => get_succeeding_tag(lines = lines, stack = stack)
        }
        case _ :: _ => (line, get_tag(line)) match {
          case (l: String, Some(tag)) if closing_tag.matches(l) && stack.head == tag =>
            get_succeeding_tag(lines = l :: lines, stack = stack.drop(1))
          case (l: String, _) => get_succeeding_tag(lines = l :: lines, stack)
        }
      }
    }
    else None
  }

}