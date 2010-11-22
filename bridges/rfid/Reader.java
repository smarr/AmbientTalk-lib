package bridges.rfid;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import librfid.exceptions.RFIDException;
import librfid.rfid.Device;
import librfid.rfid.Transponder;

public class Reader {
	
	protected Device device_;
	
	public Reader(int i) throws RFIDException {
		device_ = new Device(i);
	}
	
	public Reader() throws RFIDException {
		device_ = new Device();
	}
	
	public Vector<Tag> inventory(boolean withReset) throws RFIDException, IOException {
		Vector<librfid.rfid.Transponder> transponders = device_.readTransponders(withReset);
		Vector<bridges.rfid.Tag> wrapped = new Vector<Tag>(transponders.size());
		Iterator<Transponder> i = transponders.iterator();
		while (i.hasNext ()) {
			librfid.rfid.Transponder tag = i.next();
			wrapped.add(new Tag(tag));
		}
		return wrapped;
	}
	
	public Vector<Tag> readTransponders() throws RFIDException, IOException {
		return this.inventory(false);
	}

}
