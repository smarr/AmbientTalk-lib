package bridges.rfid;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import librfid.file.TagWrapper;
import librfid.exceptions.RFIDException;
import librfid.rfid.*;

public class ReaderWrapper {
	
	Device reader;
	
	public ReaderWrapper(int i) throws RFIDException {
		reader = new Device(i);
	}
	
	public ReaderWrapper() throws RFIDException {
		reader = new Device();
	}
	
	public Vector<TagWrapper> readTransponders(boolean with_reset) throws RFIDException, IOException {
		Vector<Transponder> realTags = reader.readTransponders(with_reset);
		Vector<TagWrapper> fakeTags = new Vector<TagWrapper>(realTags.size());
		Iterator<Transponder> i = realTags.iterator();
		while (i.hasNext ()) {
			Transponder tag = i.next ();
			fakeTags.add(new TagWrapper(tag));
		}
		return fakeTags;
	}
	
	public Vector<TagWrapper> readTransponders() throws RFIDException, IOException {
		return this.readTransponders(false);
	}
}
