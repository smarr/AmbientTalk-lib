/**
 * AmbientTalk/2 Project
 * Voting.java created on 8 mrt 2008 at 17:27:04
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
package at.m2mi.comparison;

import edu.rit.m2mi.M2MI;
import edu.rit.util.Timer;
import edu.rit.util.TimerTask;
import edu.rit.util.TimerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

class PlayerImpl implements Player {
	private final String myTeam;
	private final Player self = (Player) M2MI.getUnihandle(this, Player.class);
	public PlayerImpl(String team) {
		myTeam = team;
	}
	public void report(String team, VoteReplyHandler r) {
		if (myTeam.equals(team)) {
			r.playerDiscovered(self);
		}
	}
	public void askToVote(String poll, VoteReplyHandler r) {
		System.out.println("Answer to " + poll + "?");
		BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
		String answer;
		try {
			answer = bin.readLine();
		} catch (IOException e) {
			answer = "";
		}
		r.replyToVote(self, answer);
	}
}

/**
 * An implementation of the voting example in an ad hoc network using M2MI
 * @author tvcutsem
 */
public class Voting implements VoteReplyHandler, TimerTask {
	
	private static final long BROADCAST_RATE = 2000L; // 2 seconds
	
	private final String poll;
	private final long deadline;
	
	private final VoteReplyHandler replyHandler;
	private final Object lock = new Object();
	private final Player nearbyPlayers;
	private final Timer myTimer;
	private final HashMap receivedVotes = new HashMap();
	private final String myTeam;
	
	public Voting(String myTeam, String poll, long maxVoteTime) {
		this.poll = poll;
		this.deadline = System.currentTimeMillis() + maxVoteTime;
		this.myTeam = myTeam;
		replyHandler = (VoteReplyHandler) M2MI.getUnihandle(this, VoteReplyHandler.class);
		nearbyPlayers = (Player) M2MI.getOmnihandle(Player.class);
		myTimer = TimerThread.getDefault().createTimer(this);
		discover(myTimer);
	}
	
	public synchronized void playerDiscovered(Player p) {
		if (!receivedVotes.containsKey(p) && stillValid()) {
			System.err.println("player discovered...: " + p);
			// put the player in the map already, to ensure he only gets to vote once
			receivedVotes.put(p, null);
			p.askToVote(poll, replyHandler);
		} else {
			System.err.println("player rediscovered, ignoring");
		}
	}
	
	public synchronized void replyToVote(Player p, String answer) {
		System.err.println("reply received...: " + answer);
		if (stillValid()) {
			receivedVotes.put(p, answer);	
		}
	}
	
	private void discover(Timer repeater) {
		System.err.println("broadcasting report...");
		nearbyPlayers.report(myTeam, replyHandler);
		repeater.start(BROADCAST_RATE);
	}
	
	public boolean stillValid() {
		return System.currentTimeMillis() <= deadline;
	}
	
	public void action(Timer theTimer) {
		if (theTimer.isTriggered()) {
			if (stillValid()) {
				System.err.println("continuing discovery...");
				discover(theTimer);
			} else {
				System.err.println("vote expired, results:");
				System.err.println(receivedVotes);
				signalDone();	
			}
		}
	}
	
	private void signalDone() {
		synchronized (lock) { lock.notifyAll(); }
	}
	
	private void waitUntilDone() throws InterruptedException {
		synchronized (lock) {
			lock.wait();
		}
	}
	
	/**
	 * Usage:
	 *  either: 'client red|blue timeout'
	 *  or: 'server red|blue poll timeout'
	 */
	public static void main(final String[] args) throws Exception {
		M2MI.initialize();
		if (args[0].equals("client")) {
			M2MI.export(new PlayerImpl(args[1]), Player.class);
			//System.out.println("press any key to quit");
			//System.in.read();
			System.err.println("Serving requests...");
			Object lock = new Object();
			synchronized (lock) { lock.wait(Long.parseLong(args[2])); }
			System.err.println("Quitting");
		} else {
			Voting v = new Voting(args[1], args[2], Long.parseLong(args[3]));
			v.waitUntilDone();
		}
	}
	
}

