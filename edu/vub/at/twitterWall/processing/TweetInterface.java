package edu.vub.at.twitterWall.processing;

public interface TweetInterface {
	void addUser(String name, Integer id);
	void removeUser(Integer id);
	void addMessage(int id, String message);
}
