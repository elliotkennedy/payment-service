package io.elken.fakebook.paymentservice.domain;

public class Recipient {

	private String id;
	private String name;
	private String bio;
	private String imageSrc;
	private int numberOfFriends;

	public Recipient() {
	}

	public Recipient(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Recipient(String id, String name, String bio, String imageSrc, int numberOfFriends) {
		this.id = id;
		this.name = name;
		this.bio = bio;
		this.imageSrc = imageSrc;
		this.numberOfFriends = numberOfFriends;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public int getNumberOfFriends() {
		return numberOfFriends;
	}

	public void setNumberOfFriends(int numberOfFriends) {
		this.numberOfFriends = numberOfFriends;
	}

}
