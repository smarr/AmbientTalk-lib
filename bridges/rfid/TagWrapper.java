package bridges.rfid;

import java.io.*;

import edu.vub.at.actors.natives.Packet;
import edu.vub.at.eval.Evaluator;
import edu.vub.at.exceptions.InterpreterException;
import edu.vub.at.exceptions.XIOProblem;
import edu.vub.at.objects.ATAbstractGrammar;
import edu.vub.at.objects.ATContext;
import edu.vub.at.objects.ATObject;
import edu.vub.at.objects.natives.NATContext;
import edu.vub.at.objects.natives.NATObject;
import edu.vub.at.objects.natives.NATText;
import edu.vub.at.objects.natives.OBJLexicalRoot;
import edu.vub.at.parser.NATParser;

import librfid.rfid.Serial;
import librfid.rfid.Transponder;

public class TagWrapper {
	
	private String prefix = "/tmp/";
	private String extension = ".tag";
	
	private Transponder tag; // optional
	private String serial; // required
	private File file = null; // required
	private byte[] contents = null; // required
	
	private boolean available = false;
	
	public TagWrapper(String serial) throws IOException {
		this.serial = serial;
		this.file = new File(prefix + this.serial + extension);
		//if(this.file.exists())
			//this.file.delete();
		this.file.createNewFile();
	}
	
	public TagWrapper(Transponder tag) throws IOException {
		this.tag = tag;
		this.serial = tag.getSerialHexString();
		this.file = new File("/tmp/" + this.serial + ".tag");
		//if(this.file.exists())
			//this.file.delete();
		this.file.createNewFile();
	}
	
	//public boolean equals(TagWrapper t) {
	//	return serial.equals(t.getSerialString());
	//}
	
	public Serial getSerialFromTag() {
		return this.tag.getSerial();
	}
	
	public String getSerialString() {
		return this.serial;
	}
	
	public String getSerial() {
		return this.serial;
	}
	
	public Transponder getTag() {
		return this.tag;
	}
	
	public boolean linkedToTag() {
		return !(this.tag == null);
	}
	
	public boolean isAvailable() {
		return this.available;
	}
	
	public boolean toggleAvailable() {
		return this.available = !this.available;
	}
	
	public boolean toggleAvailable(boolean status) {
		return this.available = status;
	}
	
	public void write(byte[] dump) throws IOException {
		this.contents = dump;
		FileOutputStream fstream = new FileOutputStream(this.file);
		fstream.write(dump);
		fstream.close();
	}
	
	public byte[] read(boolean fromFile) throws IOException {
		if(fromFile){
			return this.read();
		} else {
			return contents;
		}
	}
	
	public byte[] read() throws IOException {
		FileInputStream fstream = new FileInputStream(this.file);
		long length = file.length();
		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			throw new IOException("File is too large: "+file.getName());
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=fstream.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		// Close the input stream and return bytes
		fstream.close();
		return bytes;
	}
	
	public void dump(NATObject o) throws IOException, XIOProblem {
		Packet p = new Packet(o);
		this.write(p.getPayload());
	}
	

	public void dumpAsCode(NATObject o) throws IOException, InterpreterException {
		String c = o.meta_asCode().javaValue;
		this.write(c.getBytes());
	}
	
	/*
	public void dump(Object o) throws IOException {
		byte[] payload = serialize(o);
		this.write(payload);
	}
	*/
	
	public Object load() throws IOException {
		byte[] payload = this.read();
		try {
			return deserialize(payload);
		} catch(ClassNotFoundException e) {
			System.out.println("Unable to deserialize: considered as empty");
			return null;
		} catch(EOFException e) {
			System.out.println("Unable to deserialize: considered as empty");
			return null;
		}
	}
	
	public String loadToString() throws IOException {
		byte[] payload = this.read();
		return new String(payload);
	}
	
	/*
	public ATAbstractGrammar load() throws IOException {
		byte[] payload = this.read();
		try {
			return NATParser._INSTANCE_.base_parse(NATText.atValue(new String(payload)));
		} catch(InterpreterException e) {
			System.out.println("Unable to deserialize: interpreter exception " + e.getMessage());
			return null;
		}
	}
	
	public ATObject load(ATContext c) throws IOException {
		byte[] payload = this.read();
		try {
			ATAbstractGrammar ag = NATParser._INSTANCE_.base_parse(NATText.atValue(new String(payload)));
			return ag.meta_eval(c);
		} catch(InterpreterException e) {
			System.out.println("Unable to deserialize: interpreter exception" + e.getMessage());
			return null;
		}
	}
	*/
	
	private static byte[] serialize(Object o) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream stream = new ObjectOutputStream(out);
		stream.writeObject(o);
		stream.flush();
		stream.close();
		return out.toByteArray();
	}
	
	private static Object deserialize(byte[] b) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		ObjectInputStream instream = new ObjectInputStream(in);
		return instream.readObject();
	}
	
}
