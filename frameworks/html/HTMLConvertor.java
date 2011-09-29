package frameworks.html;

import java.util.Iterator;
import java.util.Vector;

import org.htmlparser.Attribute;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

public class HTMLConvertor {	
	
	public static String convert(String doc) throws ParserException {
		Parser parser = new Parser(new Lexer(doc));
		final StringBuilder out = new StringBuilder();
		parser.visitAllNodesWith(new NodeVisitor() {
			
			public void visitTag(Tag t) {
				out.append(t.getTagName().toLowerCase());
				out.append(":{");
				// handle attributes
				Vector attributes = t.getAttributesEx();
				if (!attributes.isEmpty()) {
					Iterator ai = attributes.iterator();
					Attribute current;
					while (ai.hasNext()) {
						current = (Attribute) ai.next();
						if (current.isValued() && !current.isWhitespace()) {
							out.append("attributes.put(`");
							out.append(current.getName().toLowerCase());
							out.append(",");
							out.append(current.getRawValue());
							out.append(");");
						}
					}
				}
			}
			
			public void visitEndTag(Tag t) {
				out.append("}");
				out.append(";");
			}
			public void visitStringNode(Text string) {
				out.append("\"");
				out.append(string.getText());
				out.append("\"");
		    }
		});
		return out.toString();
	}

}
