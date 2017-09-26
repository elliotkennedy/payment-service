package io.elken.fakebook.paymentservice.domain;

public class User {

	private final String id;
	private final String name;
	private final String bio;
	private final String imageSrc;
	private final int numberOfFriends;

	public User(String id, String name, String bio, String imageSrc, int numberOfFriends) {
		this.id = id;
		this.name = name;
		this.bio = bio;
		this.imageSrc = imageSrc;
		this.numberOfFriends = numberOfFriends;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBio() {
		return bio;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public int getNumberOfFriends() {
		return numberOfFriends;
	}

	@Override
	public String toString() {
		return "User{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", bio='" + bio + '\'' +
				", imageSrc='" + imageSrc + '\'' +
				", numberOfFriends=" + numberOfFriends +
				'}';
	}

}
