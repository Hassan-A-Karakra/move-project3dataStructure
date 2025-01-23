package application;

import javafx.scene.control.TableView;

public class AVLTree {
	AVLNode root;

	private int height(AVLNode node) {
		return (node == null) ? 0 : node.height;
	}

	private AVLNode rotateRight(AVLNode y) {
		AVLNode x = y.left;
		AVLNode T2 = x.right;
		x.right = y;
		y.left = T2;
		y.height = Math.max(height(y.left), height(y.right)) + 1;
		x.height = Math.max(height(x.left), height(x.right)) + 1;
		return x;
	}

	private AVLNode rotateLeft(AVLNode x) {
		AVLNode y = x.right;
		AVLNode T2 = y.left;
		y.left = x;
		x.right = T2;
		x.height = Math.max(height(x.left), height(x.right)) + 1;
		y.height = Math.max(height(y.left), height(y.right)) + 1;
		return y;
	}

	private int getBalance(AVLNode node) {
		return (node == null) ? 0 : height(node.left) - height(node.right);
	}

	public void insert(Movie movie) {
		root = insert(root, movie);
	}

	private AVLNode insert(AVLNode node, Movie movie) {
		if (node == null) {
			return new AVLNode(movie);
		}
		if (movie.getTitle().compareTo(node.movie.getTitle()) < 0) {
			node.left = insert(node.left, movie);
		} else if (movie.getTitle().compareTo(node.movie.getTitle()) > 0) {
			node.right = insert(node.right, movie);
		} else {
			return node; // Duplicate keys not allowed
		}
		node.height = 1 + Math.max(height(node.left), height(node.right));
		int balance = getBalance(node);

		if (balance > 1 && movie.getTitle().compareTo(node.left.movie.getTitle()) < 0) {
			return rotateRight(node);
		}
		if (balance < -1 && movie.getTitle().compareTo(node.right.movie.getTitle()) > 0) {
			return rotateLeft(node);
		}
		if (balance > 1 && movie.getTitle().compareTo(node.left.movie.getTitle()) > 0) {
			node.left = rotateLeft(node.left);
			return rotateRight(node);
		}
		if (balance < -1 && movie.getTitle().compareTo(node.right.movie.getTitle()) < 0) {
			node.right = rotateRight(node.right);
			return rotateLeft(node);
		}
		return node;
	}

	/// -------------------------------------------------------------

	public void delete(String title) {
		root = delete(root, title);
	}

	private AVLNode delete(AVLNode node, String title) {
		if (node == null) {
			return node;
		}
		if (title.compareTo(node.movie.getTitle()) < 0) {
			node.left = delete(node.left, title);
		} else if (title.compareTo(node.movie.getTitle()) > 0) {
			node.right = delete(node.right, title);
		} else {
			if ((node.left == null) || (node.right == null)) {
				AVLNode temp = (node.left != null) ? node.left : node.right;
				if (temp == null) {
					node = null;
				} else {
					node = temp;
				}
			} else {
				AVLNode temp = getMinValueNode(node.right);
				node.movie = temp.movie;
				node.right = delete(node.right, temp.movie.getTitle());
			}
		}
		if (node == null) {
			return node;
		}
		node.height = Math.max(height(node.left), height(node.right)) + 1;
		int balance = getBalance(node);

		if (balance > 1 && getBalance(node.left) >= 0) {
			return rotateRight(node);
		}
		if (balance > 1 && getBalance(node.left) < 0) {
			node.left = rotateLeft(node.left);
			return rotateRight(node);
		}
		if (balance < -1 && getBalance(node.right) <= 0) {
			return rotateLeft(node);
		}
		if (balance < -1 && getBalance(node.right) > 0) {
			node.right = rotateRight(node.right);
			return rotateLeft(node);
		}
		return node;
	}

	private AVLNode getMinValueNode(AVLNode node) {
		AVLNode current = node;
		while (current.left != null) {
			current = current.left;
		}
		return current;
	}

	public int getHeight() {
		return getHeight(root);
	}

	private int getHeight(AVLNode node) {
		if (node == null) {
			return 0;
		}
		return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
	}

	public Movie getMinRatingMovie() {
		if (root == null) {
			return null;
		}

		AVLNode current = root;
		while (current.left != null) {
			current = current.left;
		}

		return current.movie;
	}

	public Movie getMaxRatingMovie() {
		if (root == null) {
			return null;
		}

		AVLNode current = root;
		while (current.right != null) {
			current = current.right;
		}

		return current.movie;
	}

	public Movie search(String title) {
		return search(root, title);
	}

	private Movie search(AVLNode node, String title) {
		if (node == null) {
			return null;
		}

		int compareResult = title.compareTo(node.movie.getTitle());

		if (compareResult < 0) {
			return search(node.left, title);
		} else if (compareResult > 0) {
			return search(node.right, title);
		} else {
			return node.movie;
		}
	}

	public Movie[] getAllMovies() {
		int totalMovies = getSize(root);
		Movie[] allMovies = new Movie[totalMovies];
		fillArray(allMovies, 0);
		return allMovies;
	}

	/// -------------------------------------------------------------

	private int getSize(AVLNode node) {
		if (node == null) {
			return 0;
		}
		return 1 + getSize(node.left) + getSize(node.right);
	}

	public int fillArray(Movie[] array, int index) {
		return fillArray(root, array, index);
	}

	private int fillArray(AVLNode node, Movie[] array, int index) {
		if (node != null) {
			index = fillArray(node.left, array, index);
			array[index++] = node.movie;
			index = fillArray(node.right, array, index);
		}
		return index;
	}

	public Movie getAnyMovie() {
		if (root != null) {
			return root.movie;
		}
		return null;
	}

	/// -------------------------------------------------------------

	public void getAllMoviesSortedAscending(TableView<Movie> tableView) {
		tableView.getItems().clear();
		inOrderTraversalAscending(root, tableView);
	}

	private void inOrderTraversalAscending(AVLNode node, TableView<Movie> tableView) {
		if (node != null) {
			inOrderTraversalAscending(node.left, tableView);
			tableView.getItems().add(node.movie);
			inOrderTraversalAscending(node.right, tableView);
		}
	}

	/// -------------------------------------------------------------

	public void getAllMoviesSortedDescending(TableView<Movie> tableView) {
		tableView.getItems().clear();
		inOrderTraversalDescending(root, tableView);
	}

	/// -------------------------------------------------------------

	private void inOrderTraversalDescending(AVLNode node, TableView<Movie> tableView) {
		if (node != null) {
			inOrderTraversalDescending(node.right, tableView);
			tableView.getItems().add(node.movie);
			inOrderTraversalDescending(node.left, tableView);
		}
	}

	// Check if the AVL tree is empty
	public boolean isEmpty() {
		return root == null;
	}

	public void clear() {
		root = null;
	}

}