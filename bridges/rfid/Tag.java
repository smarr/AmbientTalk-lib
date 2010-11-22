package bridges.rfid;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import librfid.exceptions.RFIDException;
import librfid.rfid.Serial;
import librfid.rfid.Transponder;

public class Tag {
		
		protected Transponder transponder_;

		public Tag(librfid.rfid.Transponder t) {
			transponder_ = t;
		}
		
		public Serial getSerial() {
			return transponder_.getSerial();
		}
		
		public void write(String s) throws Exception {
			transponder_.putString(s);
		}
		
		public String read() throws Exception {
			return transponder_.getString();
		}
		
		public void dump(Object o) throws Exception {
			byte[] data = Tag.serialize(o);
			this.write(new String(data));
		}
		
		public Object load() throws Exception {
			byte[] data = this.read().getBytes();
			return Tag.deserialize(data);
		}
		
		// static method to serialize an object to byte[]
		protected static byte[] serialize(Object o) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(out);
			stream.writeObject(o);
			stream.flush();
			stream.close();
			return out.toByteArray();
		}
		
		// static method to deserialize a byte[] to object
		protected static Object deserialize(byte[] b) throws IOException, ClassNotFoundException {
			ByteArrayInputStream in = new ByteArrayInputStream(b);
			ObjectInputStream instream = new ObjectInputStream(in);
			return instream.readObject();
		}
}
