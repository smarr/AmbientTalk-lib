package at.m2mi.comparison;

public interface RoamingService {
	public void anycast(ReplyHandler r);
	public void deliver(Object data);
}
