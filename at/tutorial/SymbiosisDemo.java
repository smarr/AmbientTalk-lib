package at.tutorial;

public class SymbiosisDemo {
	public interface PingPong {
		public int ping();
		public int pong();
	}
	
	public int run(PingPong pp) {
		return pp.ping();
	}

	public int run2(PingPong pp) {
		return pp.pong();
	}
	
}