/**
 * AmbientTalk/2 Project
 * AWTCanvas.java created on 21 aug 2008 at 10:48:43
 * (c) Programming Technology Lab, 2006 - 2007
 * Authors: Tom Van Cutsem & Stijn Mostinckx
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package frameworks.morphic.platform;

import edu.vub.at.actors.net.OBJNetwork;
import edu.vub.at.eval.Evaluator;
import edu.vub.at.exceptions.InterpreterException;
import edu.vub.at.exceptions.XArityMismatch;
import edu.vub.at.objects.ATContext;
import edu.vub.at.objects.ATObject;
import edu.vub.at.objects.ATTable;
import edu.vub.at.objects.ATTypeTag;
import edu.vub.at.objects.coercion.Coercer;
import edu.vub.at.objects.mirrors.PrimitiveMethod;
import edu.vub.at.objects.natives.FieldMap;
import edu.vub.at.objects.natives.MethodDictionary;
import edu.vub.at.objects.natives.NATNamespace;
import edu.vub.at.objects.natives.NATNumber;
import edu.vub.at.objects.natives.NATObject;
import edu.vub.at.objects.natives.NATTable;
import edu.vub.at.objects.natives.NATText;
import edu.vub.at.objects.natives.grammar.AGSymbol;
import edu.vub.at.util.logging.Logging;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

/**
 * A native drawing canvas based on Java 2D graphics.
 * 
 * @author tvcutsem
 */
public class NATAWTCanvas extends NATObject {

	private final Graphics2D graphics_;
	
	public NATAWTCanvas(Graphics2D g) {
		graphics_ = g;
		try {
			addNativeMethods();
		} catch(InterpreterException e) {
			Logging.Actor_LOG.fatal("Fatal error while creating NATAWTCanvas: "+e.getMessage(), e);
		}
	}
	
	private NATAWTCanvas(FieldMap map, Vector state, LinkedList customFields,
			MethodDictionary methodDict, ATObject dynamicParent,
			ATObject lexicalParent, byte flags, ATTypeTag[] types, Set freeVars, Graphics2D g) throws InterpreterException {
		super(map, state, customFields, methodDict, dynamicParent, lexicalParent, flags, types);
		graphics_ = g;
		addNativeMethods();
	}

	protected NATObject createClone(FieldMap map,
			  Vector state,
			  LinkedList customFields,
			  MethodDictionary methodDict,
			  ATObject dynamicParent,
			  ATObject lexicalParent,
			  byte flags, ATTypeTag[] types,
			  Set freeVars) throws InterpreterException {
      return new NATAWTCanvas(map,
  		  				    state,
  		  				    customFields,
  		  				    methodDict,
  		  				    dynamicParent,
  		  				    lexicalParent,
  		  				    flags,
  		  				    types,
  		  				    freeVars,
  		  				    (Graphics2D) graphics_.create());
	}
	
	public NATText meta_print() throws InterpreterException {
		return NATText.atValue("<awt canvas:"+graphics_+">");
	}
	
	/**
	 * Maps all prim_* methods of this class onto ATMethod objects and registers
	 * those ATMethod instances with this AmbientTalk object.
	 */
	private void addNativeMethods() throws InterpreterException {
		// def draw(shape, color);
		super.meta_addMethod(new PrimitiveMethod(        
				AGSymbol.jAlloc("draw"), NATTable.of(AGSymbol.jAlloc("shape"), AGSymbol.jAlloc("color"))) {
	      public ATObject base_apply(ATTable arguments, ATContext ctx) throws InterpreterException {
	    	    prim_draw((Shape) arguments.base_at(NATNumber.ONE).asJavaObjectUnderSymbiosis().getWrappedObject(),
	    	    		  (Color) arguments.base_at(NATNumber.atValue(2)).asJavaObjectUnderSymbiosis().getWrappedObject());
	    	    return Evaluator.getNil();
		  }
		});
		// def fillRect(x,y,width,height,c);
		super.meta_addMethod(new PrimitiveMethod(        
				AGSymbol.jAlloc("fillRect"), NATTable.atValue(new ATObject[] { AGSymbol.jAlloc("x"), AGSymbol.jAlloc("y"), AGSymbol.jAlloc("width"), AGSymbol.jAlloc("height"), AGSymbol.jAlloc("color") })) {
	      public ATObject base_apply(ATTable arguments, ATContext ctx) throws InterpreterException {
	    	  ATObject[] args = arguments.asNativeTable().elements_;
	    	  if (args.length != 5) {
	    		  throw new XArityMismatch("fillRect", 5, args.length);
	    	  }
	    	  prim_fillRect(args[0].asNativeNumber().javaValue,
	    			        args[1].asNativeNumber().javaValue,
	    			        args[2].asNativeNumber().javaValue,
	    			        args[3].asNativeNumber().javaValue,
	    			        (Color) args[4].asJavaObjectUnderSymbiosis().getWrappedObject());
	    	  return Evaluator.getNil();
		  }
		});
		// def fillOval(x,y,width,height,c);
		super.meta_addMethod(new PrimitiveMethod(        
				AGSymbol.jAlloc("fillOval"), NATTable.atValue(new ATObject[] { AGSymbol.jAlloc("x"), AGSymbol.jAlloc("y"), AGSymbol.jAlloc("width"), AGSymbol.jAlloc("height"), AGSymbol.jAlloc("color") })) {
	      public ATObject base_apply(ATTable arguments, ATContext ctx) throws InterpreterException {
	    	  ATObject[] args = arguments.asNativeTable().elements_;
	    	  if (args.length != 5) {
	    		  throw new XArityMismatch("fillOval", 5, args.length);
	    	  }
	    	  prim_fillOval(args[0].asNativeNumber().javaValue,
	    			        args[1].asNativeNumber().javaValue,
	    			        args[2].asNativeNumber().javaValue,
	    			        args[3].asNativeNumber().javaValue,
	    			        (Color) args[4].asJavaObjectUnderSymbiosis().getWrappedObject());
	    	  return Evaluator.getNil();
		  }
		});
		// def drawString(string,x,y,c);
		super.meta_addMethod(new PrimitiveMethod(        
				AGSymbol.jAlloc("drawString"), NATTable.atValue(new ATObject[] { AGSymbol.jAlloc("string"), AGSymbol.jAlloc("x"), AGSymbol.jAlloc("y"), AGSymbol.jAlloc("color") })) {
	      public ATObject base_apply(ATTable arguments, ATContext ctx) throws InterpreterException {
	    	  ATObject[] args = arguments.asNativeTable().elements_;
	    	  if (args.length != 4) {
	    		  throw new XArityMismatch("drawString", 4, args.length);
	    	  }
	    	  prim_drawString(args[0].asNativeText().javaValue,
	    			          args[1].asNativeNumber().javaValue,
	    			          args[2].asNativeNumber().javaValue,
	    			          (Color) args[3].asJavaObjectUnderSymbiosis().getWrappedObject());
	    	  return Evaluator.getNil();
		  }
		});
		// def fillRoundRect(x,y,width,height,c);
		super.meta_addMethod(new PrimitiveMethod(        
				AGSymbol.jAlloc("fillRoundRect"), NATTable.atValue(new ATObject[] { AGSymbol.jAlloc("x"), AGSymbol.jAlloc("y"), AGSymbol.jAlloc("width"), AGSymbol.jAlloc("height"), AGSymbol.jAlloc("arcwidth"), AGSymbol.jAlloc("archeight"), AGSymbol.jAlloc("color") })) {
	      public ATObject base_apply(ATTable arguments, ATContext ctx) throws InterpreterException {
	    	  ATObject[] args = arguments.asNativeTable().elements_;
	    	  if (args.length != 7) {
	    		  throw new XArityMismatch("fillRoundRect", 7, args.length);
	    	  }
	    	  prim_fillRoundRect(args[0].asNativeNumber().javaValue,
	    			             args[1].asNativeNumber().javaValue,
	    			             args[2].asNativeNumber().javaValue,
	    			             args[3].asNativeNumber().javaValue,
	    			             args[4].asNativeNumber().javaValue,
	    			             args[5].asNativeNumber().javaValue,
	    			             (Color) args[6].asJavaObjectUnderSymbiosis().getWrappedObject());
	    	  return Evaluator.getNil();
		  }
		});
		// def drawRoundRect(x,y,width,height,c);
		super.meta_addMethod(new PrimitiveMethod(        
				AGSymbol.jAlloc("drawRoundRect"), NATTable.atValue(new ATObject[] { AGSymbol.jAlloc("x"), AGSymbol.jAlloc("y"), AGSymbol.jAlloc("width"), AGSymbol.jAlloc("height"), AGSymbol.jAlloc("arcwidth"), AGSymbol.jAlloc("archeight"), AGSymbol.jAlloc("color") })) {
	      public ATObject base_apply(ATTable arguments, ATContext ctx) throws InterpreterException {
	    	  ATObject[] args = arguments.asNativeTable().elements_;
	    	  if (args.length != 7) {
	    		  throw new XArityMismatch("drawRoundRect", 7, args.length);
	    	  }
	    	  prim_drawRoundRect(args[0].asNativeNumber().javaValue,
	    			             args[1].asNativeNumber().javaValue,
	    			             args[2].asNativeNumber().javaValue,
	    			             args[3].asNativeNumber().javaValue,
	    			             args[4].asNativeNumber().javaValue,
	    			             args[5].asNativeNumber().javaValue,
	    			             (Color) args[6].asJavaObjectUnderSymbiosis().getWrappedObject());
	    	  return Evaluator.getNil();
		  }
		});
		// def clearRect(x,y,width,height,c);
		super.meta_addMethod(new PrimitiveMethod(        
				AGSymbol.jAlloc("clearRect"), NATTable.atValue(new ATObject[] { AGSymbol.jAlloc("x"), AGSymbol.jAlloc("y"), AGSymbol.jAlloc("width"), AGSymbol.jAlloc("height"), AGSymbol.jAlloc("color") })) {
	      public ATObject base_apply(ATTable arguments, ATContext ctx) throws InterpreterException {
	    	  ATObject[] args = arguments.asNativeTable().elements_;
	    	  if (args.length != 5) {
	    		  throw new XArityMismatch("clearRect", 5, args.length);
	    	  }
	    	  prim_clearRect(args[0].asNativeNumber().javaValue,
	    			         args[1].asNativeNumber().javaValue,
	    			         args[2].asNativeNumber().javaValue,
	    			         args[3].asNativeNumber().javaValue,
	    			         (Color) args[4].asJavaObjectUnderSymbiosis().getWrappedObject());
	    	  return Evaluator.getNil();
		  }
		});
	}
	
	public void prim_draw(Shape s, Color c) {
		graphics_.setColor(c);
		graphics_.draw(s);
	}
	
	public void prim_fillRect(int x, int y, int width, int height, Color c) {
		graphics_.setColor(c);
		graphics_.fillRect(x, y, width, height);
	}
	
	public void prim_fillOval(int x, int y, int width, int height, Color c) {
		graphics_.setColor(c);
		graphics_.fillOval(x, y, width, height);
	}
	
	public void prim_drawString(String str, int x, int y, Color c) {
		graphics_.setColor(c);
		graphics_.drawString(str, x, y);
	}
	
	public void prim_fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight, Color c) {
		graphics_.setColor(c);
		graphics_.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}
	
	public void prim_drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, Color c) {
		graphics_.setColor(c);
		graphics_.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }
	
	public void prim_clearRect(int x, int y, int width, int height, Color c) {
		graphics_.setColor(c);
		graphics_.clearRect(x, y, width, height);
	}
	
	// TODO: Still to hook up
	

	public void prim_fill(Shape s) {
		graphics_.fill(s);
	}

	public boolean prim_hit(Rectangle rect, Shape s, boolean onStroke) {
		return graphics_.hit(rect, s, onStroke);
	}

	public void prim_rotate(double theta, double x, double y) {
		graphics_.rotate(theta, x, y);
	}

	public void prim_rotate(double theta) {
		graphics_.rotate(theta);
	}

	public void prim_scale(double sx, double sy) {
		graphics_.scale(sx, sy);
	}

	public void prim_setBackground(Color color) {
		graphics_.setBackground(color);
	}

	public void prim_setStroke(Stroke s) {
		graphics_.setStroke(s);
	}

	public void prim_setTransform(AffineTransform Tx) {
		graphics_.setTransform(Tx);
	}

	public void prim_transform(AffineTransform Tx) {
		graphics_.transform(Tx);
	}

	public void prim_translate(int x, int y) {
		graphics_.translate(x, y);
	}

	public void prim_drawLine(int x1, int y1, int x2, int y2, Color c) {
		graphics_.setColor(c);
		graphics_.drawLine(x1, y1, x2, y2);
	}

	public void prim_drawOval(int x, int y, int width, int height, Color c) {
		graphics_.setColor(c);
		graphics_.drawOval(x, y, width, height);
	}

	public void prim_drawPolygon(Polygon p, Color c) {
		graphics_.setColor(c);
		graphics_.drawPolygon(p);
	}

	public void prim_drawRect(int x, int y, int width, int height, Color c) {
		graphics_.setColor(c);
		graphics_.drawRect(x, y, width, height);
	}

	public void prim_fillPolygon(Polygon p, Color c) {
		graphics_.setColor(c);
		graphics_.fillPolygon(p);
	}

	public void prim_setColor(Color c) {
		graphics_.setColor(c);
	}

	public void prim_setFont(Font font) {
		graphics_.setFont(font);
	}

}
