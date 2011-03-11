package edu.vub.at.twitterWall.processing;

public interface TweetInterface {
	void addUser(String name, int id);
	void removeUser(int id);
	void addMessage(int id, String message);
}
