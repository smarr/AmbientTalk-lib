/**
 * AmbientTalk/2 Project
 * RunnableTimerTask.java created on 27-mrt-2007 at 10:24:34
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
package at.support;

import java.util.EventListener;
import java.util.TimerTask;

/**
 * A runnable timer task takes a runnable object as input and uses that
 * as the body for a scheduled timer task. This enables AmbientTalk/2 code
 * to symbiotically provide an AmbientTalk object implementing java.lang.Runnable
 * to serve as a timer task.
 * 
 * @author tvcutsem
 */
public class RunnableTimerTask extends TimerTask {

	public interface AsyncRunnable extends EventListener {
		public void run();
	};
	
	private final AsyncRunnable runnable_;
	
	public RunnableTimerTask(AsyncRunnable r) {
		runnable_ = r;
	}

	public void run() {
		runnable_.run();
	}

}
