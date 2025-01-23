package application;

public class Movie {
	private String title;
	private String description;
	private int releaseYear;
	private double rating;
	private int index;

	// Constructor
	public Movie(String title, String description, int releaseYear, double rating) {
		this.title = title;
		this.description = description;
		this.releaseYear = releaseYear;
		this.rating = rating;
	}

	// Getters and Setters
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "Title: " + title + ", Description: " + description + ", Release Year: " + releaseYear + ", Rating: "
				+ rating;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}