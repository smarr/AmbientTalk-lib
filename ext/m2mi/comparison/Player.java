package ext.m2mi.comparison;

public interface Player {

	public void report(String team, VoteReplyHandler r);
	
	public void askToVote(String poll, VoteReplyHandler r);
	
}
