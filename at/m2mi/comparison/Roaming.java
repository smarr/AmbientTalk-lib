/**
 * AmbientTalk/2 Project
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

class RoamingServiceImpl implements RoamingService {
	public void anycast(ReplyHandler r) {
		r.reply((RoamingService) M2MI.getUnihandle(this, RoamingService.class));
	}
	public void deliver(Object data) {
		System.err.println("data received: " + data);
	}
}

public class Roaming implements ReplyHandler, TimerTask {
	
	private static final long ANYCAST_TIMEOUT = 2000L; // 2 seconds
		
	private boolean isMessageSent_;
	private final Object data_;
	private final ReplyHandler replyHandler;
	private final RoamingService services;
	private final Object lock = new Object();
	
	public Roaming(Object data) {
		isMessageSent_ = false;
		data_ = data;
		replyHandler = (ReplyHandler) M2MI.getUnihandle(this, ReplyHandler.class);
		services = (RoamingService) M2MI.getOmnihandle(RoamingService.class);
		performAnycast(TimerThread.getDefault().createTimer(this));
	}
	
	public synchronized void reply(RoamingService s) {
		System.err.println("reply received...");
		if (!isMessageSent_) {
			isMessageSent_ = true;
			s.deliver(data_);
		}
	}
	
	private void performAnycast(Timer repeater) {
		isMessageSent_ = false;
		System.err.println("sending anycast...");
		services.anycast(replyHandler);
		repeater.start(ANYCAST_TIMEOUT);
	}
	
	public void action(Timer theTimer) {
		if (theTimer.isTriggered()) {
			synchronized(this) {
				if (!isMessageSent_) {
					System.err.println("no one replied, repeating...");
					performAnycast(theTimer);
				} else {
					System.err.println("message sent");
					synchronized (lock) { lock.notifyAll(); }
				}
			}
		}
	}
	
	private void waitUntilSent() throws InterruptedException {
		synchronized (lock) {
			lock.wait();
		}
	}
	
	public static void main(final String[] args) throws Exception {
		M2MI.initialize();
		if (args[0].equals("client")) {
			M2MI.export(new RoamingServiceImpl(), RoamingService.class);
			System.out.println("press any key to quit");
			System.in.read();
		} else {
			Roaming r = new Roaming(args[1]);
			r.waitUntilSent();
		}
	}
	
}
