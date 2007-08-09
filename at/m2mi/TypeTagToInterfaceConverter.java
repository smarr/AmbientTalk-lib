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
import edu.vub.at.exceptions.InterpreterException;
import edu.vub.at.objects.ATObject;
import edu.vub.at.objects.ATTypeTag;
import edu.vub.at.objects.mirrors.Reflection;
import edu.vub.at.objects.natives.NATTypeTag.OBJRootType;
import edu.vub.at.objects.symbiosis.XJavaException;
import edu.vub.at.util.logging.Logging;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts AmbientTalk type tag objects into Java interface class objects
 * at runtime.
 * 
 * @author tvcutsem
 */
public class TypeTagToInterfaceConverter {

	private static final class TypeTagClassLoader extends ClassLoader {

		private static final Map registeredTypes = Collections.synchronizedMap(new HashMap());
		
		/**
		 * Register a type tag with this custom class loader.
		 * When requested for a name matching one of the registered type tags,
		 * the class loader will return a synthesized interface class for it.
		 */
		public void register(String typeName, ATTypeTag typeTag) {
			registeredTypes.put(typeName, typeTag);
		}
		
		/**
		 * The manually generated interface for the root type tag.
		 * This root interface extends EventListener such that all of
		 * the generated interfaces will be regarded as event listener
		 * interfaces by the AmbientTalk symbiosis layer.
		 * This ensures that invoke is regarded as an asynchronous invocation
		 * on the AmbientTalk object it wraps.
		 */
		public interface Type extends EventListener {
			/**
			 * The generic method serving as the asynchronous entry point
			 * for all M2MI calls that are forwarded to an actual AmbientTalk
			 * object by means of an "invoker" proxy object.
			 */
			public void invoke(Object message);
		}
		
		protected Class findClass(String name) throws ClassNotFoundException {
			ATTypeTag type = (ATTypeTag) registeredTypes.get(name);
			if (type != null) {
				try {
					// first, convert all of the typetag's supertypes to classes
					ATObject[] supertags = type.base_superTypes().asNativeTable().elements_;
					Class[] supertypes = new Class[supertags.length];
					for (int i = 0; i < supertypes.length; i++) {
						ATTypeTag typetag = supertags[i].asTypeTag();
						if (typetag.base_typeName().equals(OBJRootType._INSTANCE_.base_typeName())) {
							supertypes[i] = Type.class;
						} else {
							supertypes[i] = this.loadClass(Reflection.upSelector(typetag.base_typeName()));
						}	
					}
					
					byte[] code = synthesizeClass(name, supertypes);
					return super.defineClass(null, code, 0, code.length);
				} catch (Exception e) {
					Logging.VirtualMachine_LOG.fatal(e.getMessage());
					throw new ClassNotFoundException(name);
				}
			} else {
				throw new ClassNotFoundException(name);
			}
		}
		
		private byte[] synthesizeClass(String name, Class[] superinterfaces) throws Exception {
			// create a new inteface named name
			SynthesizedInterfaceDescription interfaceDesc = new SynthesizedInterfaceDescription(name);
			// make it public
			interfaceDesc.setPublic(true);
			
			// add all superinterfaces to this interface
			for (int i = 0; i < superinterfaces.length; i++) {
				interfaceDesc.addSuperinterface(new NamedClassReference(superinterfaces[i].getName()));
			}
			
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
	 * <code>deftype T <: T2</code>
	 * To the Java interface type:
	 * <code>
	 * public interface T extends T2 { }
	 * </code>
	 */
	public static Class convert(ATTypeTag tag) throws InterpreterException {
		try {
			String tagName = Reflection.upSelector(tag.base_typeName());
			_TYPETAGLOADER_.register(tagName, tag);
			return _TYPETAGLOADER_.loadClass(tagName);
		} catch (Exception e) {
			throw new XJavaException(e);
		}
	}
	
}
