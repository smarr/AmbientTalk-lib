package bridges.rfid;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import librfid.exceptions.RFIDException;
import librfid.rfid.*;

public class ReaderFromDisk extends Reader {
	
	public ReaderFromDisk() throws RFIDException {
		super();
	}
	
	public ReaderFromDisk(int deviceNumber) throws RFIDException {
		super(deviceNumber);
	}

	public Vector<Tag> inventory(boolean withReset) throws RFIDException, IOException {
		Vector<Transponder> transponders = device_.readTransponders(withReset);
		Vector<Tag> wrapped = new Vector<Tag>(transponders.size());
		Iterator<Transponder> i = transponders.iterator();
		while (i.hasNext ()) {
			Transponder tag = i.next ();
			wrapped.add(new TagFile(tag));
		}
		return wrapped;
	}
	
	public Vector<Tag> inventory() throws RFIDException, IOException {
		return this.inventory(false);
	}
	
}
