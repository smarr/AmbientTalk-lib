package bridges.rfid.benchmarks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import librfid.exceptions.RFIDException;
import librfid.rfid.Serial;
import librfid.rfid.Transponder;

public class DummyTag {
	
		private Serial serial_;
		private String data_;
		
		private static String objectStart_ = "object: { def data := \"";
		private static String objectEnd_ = "\" ; def setN(y)@Mutator { data := y }} taggedAs: [{deftype DummyTag}()]";
		
		public DummyTag(int size) {
			size = size - objectStart_.length() - objectEnd_.length();
			serial_ = new Serial(UUID.randomUUID().toString());
			StringBuilder s = new StringBuilder(objectStart_);
			for (int i = 0 ; i < size ; i++) {
				s.append("x");
			}
			s.append(objectEnd_);
			data_ = s.toString();
		}
		
		public DummyTag(librfid.rfid.Transponder t) {
			serial_ = new Serial(UUID.randomUUID().toString());
		}
		
		public Serial getSerial() {
			return serial_;
		}
		
		public void write(String s) throws Exception {
			data_ = s;
		}
		
		public String read() throws Exception {
			return data_;
		}
		
		public Transponder getTransponder() {
			return null;
		}
		
		
		/*
		public void dump(Object o) throws Exception {
		}
		
		public Object load() throws Exception {
			return null;
		}
		
		// static method to serialize an object to byte[]
		protected static byte[] serialize(Object o) throws IOException {
			return null;
		}
		
		// static method to deserialize a byte[] to object
		protected static Object deserialize(byte[] b) throws IOException, ClassNotFoundException {
			return null;
		}
		*/
}
