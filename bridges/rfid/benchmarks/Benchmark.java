package bridges.rfid.benchmarks;

import java.io.IOException;
import java.util.Vector;

import librfid.exceptions.RFIDException;

public class Benchmark {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DummyReader.TAGNUM = 1 * 1 * 10 * 10;
		DummyReader.TAGSIZE = 100;

		try {

			Thread.sleep(5000);
			System.out.println("boe");

			int NUMINV = 100;

			// START!
			long startTime = System.nanoTime();
			int ctr = 0;

			for (int j = 0 ; j < NUMINV ; j++) {

				DummyReader r = new DummyReader();


				Vector<DummyTag> inv = r.inventory();
				DummyTag current;

				for (DummyTag t : inv) {
					ctr++;
					current = t;
				}

			}

			// STOP!
			long estimatedTime = System.nanoTime() - startTime;

			System.out.println(ctr + " tags in " + estimatedTime / 1000 / 1000 + " millisec");

			System.out.println("done");

		} catch (RFIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
