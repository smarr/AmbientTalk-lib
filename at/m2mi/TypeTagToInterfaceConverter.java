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
import edu.rit.m2mi.M2MI;
import edu.vub.at.exceptions.InterpreterException;
import edu.vub.at.objects.ATObject;
import edu.vub.at.objects.ATTypeTag;
import edu.vub.at.objects.mirrors.Reflection;
import edu.vub.at.objects.natives.NATTypeTag.OBJRootType;
import edu.vub.at.objects.symbiosis.XJavaException;
import edu.vub.at.util.logging.Logging;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

	private static final String _M2MP_DAEMON_CLASS_ = "edu.rit.m2mp.Daemon";
	
	private static boolean _alreadyInitialized = false;
	
	/**
	 * A quite heavy initialization procedure for M2MI. The procedure is heavy because we have to
	 * fork an M2MP Daemon process in the background. Note that this method is static such that
	 * it is shared by all actors. M2MI can only be initialized once.
	 *
	 * If ran on multiple Java VMs, only the first will succeed in starting the Daemon.
	 * All subsequent VMs will start a Daemon that crashes because its socket port is
	 * already bound. However, this does not make this process raise an IOException,
	 * so if the M2MP Daemon is already running, this method call will succeed.
	 *
	 * @throws IOException if there is a problem forking the M2MP process.
	 */
	public static final synchronized void initializeM2MI() throws IOException {
		if (!_alreadyInitialized) {
			Runtime currentRuntime = Runtime.getRuntime();
			
			// construct a full path name to the M2MI.jar file such that we can fire up the M2MP Daemon
			String pathToM2MIJarFile = TypeTagToInterfaceConverter.class.getResource("m2mi.jar").getPath();
			// the M2MP Daemon is a stand-alone Java executable, fork a process
			final Process daemon = currentRuntime.exec("java -cp "+pathToM2MIJarFile+" "+_M2MP_DAEMON_CLASS_);
			
			// be sure to shut down the M2MP daemon when the VM quits
			currentRuntime.addShutdownHook(new Thread() {
				public void run() {
					System.out.println("Stopping M2MP Daemon...");
					daemon.destroy();
				}
			});
			
			// print status line of the M2MP Daemon (this also serves to synchronize
			// with the M2MP daemon, waiting for it to be started)
			BufferedReader output = new BufferedReader(new InputStreamReader(daemon.getErrorStream()));
			System.err.println(output.readLine());
			System.out.println("M2MP Daemon started");
			
			// finally, when the Daemon is running, initialize M2MI
			M2MI.initialize(_TYPETAGLOADER_);
			_alreadyInitialized = true;
		}
	}
	
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
