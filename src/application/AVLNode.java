package application;

class AVLNode {

	Movie movie;
	AVLNode left, right;
	int height;

	AVLNode(Movie movie) {

		this.movie = movie;
		this.left = null;
		this.right = null;
		this.height = 1;

	}

}