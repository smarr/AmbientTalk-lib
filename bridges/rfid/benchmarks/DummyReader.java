package bridges.rfid.benchmarks;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import librfid.exceptions.RFIDException;
import librfid.rfid.Device;
import librfid.rfid.Transponder;

public class DummyReader {
	
	public static int TAGNUM = 1 * 10 * 10;
	public static int TAGSIZE = 1000;
	
	private Vector<DummyTag> tags_;
		
	public DummyReader(int j) throws RFIDException {
		tags_ = new Vector<DummyTag>();
		for (int i = 0 ; i < TAGNUM ; i++) {
			tags_.add(new DummyTag(TAGSIZE));
		}
	}
	
	public static int getTagNum() {
		return TAGNUM;
	}
	
	public DummyReader() throws RFIDException {
		this(0);
	}
	
	public Device getDevice() {
		return null;
	}
	
	public void setAntennaAlwaysOn() throws RFIDException {
	}
	
	public void setAntennaOnRequest() throws RFIDException {
	}
	
	/*
	public Vector<DummyTag> inventory(boolean withReset) throws RFIDException, IOException {
		Vector<DummyTag> tags = new Vector<DummyTag>();
		for (DummyTag t : tags_) {
			tags.add(t);
		}
		return tags;
	}
	*/
	
	public Vector<DummyTag> inventory(boolean withReset) throws RFIDException, IOException {
		Vector<DummyTag> tags = new Vector<DummyTag>();
		for (int i = 0 ; i < TAGNUM ; i++) {
			tags.add(new DummyTag(TAGSIZE));
		}
		return tags;
	}
	
	public Vector<DummyTag> inventory() throws RFIDException, IOException {
		return this.inventory(true);
	}
	
	public Vector<DummyTag> readTransponders() throws RFIDException, IOException {
		return this.inventory(false);
	}

}
