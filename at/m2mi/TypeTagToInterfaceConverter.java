/**
 * AmbientTalk/2 Project
 * TypeTagToInterfaceConverter.java created on 6 aug 2007 at 15:43:53
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
package at.m2mi;

import edu.rit.classfile.NamedClassReference;
import edu.rit.classfile.SynthesizedInterfaceDescription;
import edu.rit.classfile.SynthesizedInterfaceMethodDescription;
import edu.rit.m2mi.M2MI;
import edu.vub.at.exceptions.InterpreterException;
import edu.vub.at.objects.ATObject;
import edu.vub.at.objects.ATTypeTag;
import edu.vub.at.objects.mirrors.Reflection;
import edu.vub.at.objects.symbiosis.XJavaException;
import edu.vub.at.util.logging.Logging;

import java.io.ByteArrayOutputStream;
import java.util.EventListener;
import java.util.HashSet;

/**
 * Converts AmbientTalk type tag objects into Java interface class objects
 * at runtime.
 * 
 * @author tvcutsem
 */
public class TypeTagToInterfaceConverter {

	private static final class TypeTagClassLoader extends ClassLoader {
		
		// all generated interface class files belong to this package
		private static final String _PKG_PREFIX_ = "typetags.";
		
		private static final String _INVOKE_NAME_ = "invoke";
		private static final NamedClassReference _EVENTLISTENER_ = 
			new NamedClassReference(EventListener.class.getName());
		
		protected Class findClass(String name) throws ClassNotFoundException {
			if (name.startsWith(_PKG_PREFIX_)) {
				try {
					byte[] code = synthesizeClass(name);
					return super.defineClass(null, code, 0, code.length);
				} catch (Exception e) {
					Logging.VirtualMachine_LOG.fatal(e.getMessage());
					throw new ClassNotFoundException(name);
				}
			} else {
				throw new ClassNotFoundException(name);
			}
		}
		
		private byte[] synthesizeClass(String name) throws Exception {
			// create a new inteface named name
			SynthesizedInterfaceDescription interfaceDesc = new SynthesizedInterfaceDescription(name);
			// make it public
			interfaceDesc.setPublic(true);
			// add EventListener as superinterface such that AT's symbiosis recognizes
			// the invocation as being asynchronous
			interfaceDesc.addSuperinterface(_EVENTLISTENER_);
			
			// add a method void invoke(Object);
			SynthesizedInterfaceMethodDescription invokeMth =
				new SynthesizedInterfaceMethodDescription(interfaceDesc, _INVOKE_NAME_);
			invokeMth.addArgumentType(NamedClassReference.JAVA_LANG_OBJECT);
			
			// write the interface description out into a byte array
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			interfaceDesc.emit(out);
			return out.toByteArray();
		}
	}
	
	/** a dedicated class loader for loading converted type tag classes */
	public static final TypeTagClassLoader _TYPETAGLOADER_ = new TypeTagClassLoader();
	
	/**
	 * Converts the AmbientTalk type tag:
	 * <code>deftype T</code>
	 * To the Java interface type:
	 * <code>
	 * public interface tyeptags.T extends java.util.EventListener {
	 *   public void invoke(Object msg);
	 * }
	 * </code>
	 */
	public static Class convert(ATTypeTag tag) throws InterpreterException {
		try {
			String tagName = Reflection.upSelector(tag.base_typeName());
			return _TYPETAGLOADER_.loadClass(TypeTagClassLoader._PKG_PREFIX_ + tagName);
		} catch (Exception e) {
			throw new XJavaException(e);
		}
	}
	
	public static HashSet alreadyExported = new HashSet();
	
	// DEBUG CODE
	/*public static void export(ATObject obj, Class type) {
		System.out.println("EXPORTING OBJECT TO M2MI: " + obj + " hashcode: " + obj.hashCode() + " type = " + type);
		if (alreadyExported.contains(obj)) {
			System.out.println("The object " + obj + " was already previously exported");
		} else {
			System.out.println("The object " + obj + " was NOT previously exported");
		}
		alreadyExported.add(obj);
		M2MI.export(obj, type);
	}
	
	public static Object makeUni(ATObject obj, Class type) {
		System.out.println("EXPORTING OBJECT TO M2MI FOR UNIHDL: " + obj + " hashcode: " + obj.hashCode() + " type = " + type);
		if (alreadyExported.contains(obj)) {
			System.out.println("The object " + obj + " was already previously exported");
		} else {
			System.out.println("The object " + obj + " was NOT previously exported");
		}
		alreadyExported.add(obj);
		return M2MI.getUnihandle(obj, type);
	}*/
	
}
