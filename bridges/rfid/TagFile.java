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

public class TagFile extends Tag {
	
	private static final String PREFIX = "/tmp/";
	private static final String EXTENSION = ".tag";
	
	private Serial serial_;
	private File file_;
	
	private boolean available_ = false;
	
	public TagFile(String serial) throws IOException {
		super(null);
		this.serial_ = new Serial(serial);
		this.file_ = new File(PREFIX + this.serial_ + EXTENSION);
		//if(this.file.exists())
			//this.file.delete();
		this.file_.createNewFile();
	}
	
	public TagFile(Transponder t) throws IOException {
		super(t);
		this.serial_ = transponder_.getSerial();
		this.file_ = new File(PREFIX + this.serial_ + EXTENSION);
		//if(this.file.exists())
			//this.file.delete();
		this.file_.createNewFile();
	}
	
	public Serial getSerial() {
		return this.serial_;
	}
	
	public boolean linkedToTag() {
		return !(transponder_ == null);
	}
	
	public boolean isAvailable() {
		return this.available_;
	}
	
	public boolean toggleAvailable() {
		return this.available_ = !this.available_;
	}
	
	public boolean setAvailable(boolean status) {
		return this.available_ = status;
	}
	
	public void write(String s) throws IOException {
		this.write(s.getBytes());
	}
	
	public void write(byte[] dump) throws IOException {
		FileOutputStream fstream = new FileOutputStream(this.file_);
		fstream.write(dump);
		fstream.close();
	}
	
	public String read() throws IOException {
		FileInputStream fstream = new FileInputStream(this.file_);
		long length = file_.length();
		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			throw new IOException("File is too large: "+file_.getName());
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
			throw new IOException("Could not completely read file "+file_.getName());
		}

		// Close the input stream and return bytes
		fstream.close();
		return new String(bytes);
	}
	
	public void dump(NATObject o) throws IOException, XIOProblem {
		Packet p = new Packet(o);
		this.write(p.getPayload());
	}	
	
	public Object load() throws IOException {
		byte[] payload = this.read().getBytes();
		try {
			return Tag.deserialize(payload);
		} catch(ClassNotFoundException e) {
			System.out.println("Unable to deserialize: considered as empty");
			return null;
		} catch(EOFException e) {
			System.out.println("Unable to deserialize: considered as empty");
			return null;
		}
	}
	
}
