package application;

public class HashTableMovieCatalog {

	private AVLTree[] hashTable;
	private int size;
	private int numberOfMovies;

	// Getter for the hashTable
	public AVLTree[] getHashTable() {
		return hashTable;
	}

	// make constructor
	public HashTableMovieCatalog(int size) {

		this.size = size;
		this.hashTable = new AVLTree[size];

		for (int i = 0; i < size; i++) {
			hashTable[i] = new AVLTree();
		}
	}

	private int hashFunction(String title) {

		int hash = 0;
		int prime = 31;

		for (int i = 0; i < title.length(); i++) {
			hash = (hash * prime + title.charAt(i)) % getHashTableSize();
		}

		return hash;
	}

	private void reHashTable() {

		int newSize = allocate(size * 2);
		AVLTree[] newHashTable = new AVLTree[newSize];
		for (int i = 0; i < newSize; i++) {
			newHashTable[i] = new AVLTree();
		}

		for (AVLTree tree : hashTable) {
			rehashTree(tree.root, newHashTable, newSize);
		}

		hashTable = newHashTable;
		size = newSize;
	}

	private int allocate(int num) {
		while (!isPrime(num)) {
			num++;
		}
		return num;
	}

	private boolean isPrime(int num) {
		if (num <= 1)
			return false;
		for (int i = 2; i * i <= num; i++) {
			if (num % i == 0)
				return false;
		}
		return true;
	}

	public void put(Movie movie) {
		int index = hashFunction(movie.getTitle());
		hashTable[index].insert(movie);

		if (isResizeNeeded()) {
			reHashTable();
		}
	}

	private boolean isResizeNeeded() {

		if (getLoadFactor() > 0.75) {
			return true;
		}

		int totalHeight = 0;
		int nonEmptyTrees = 0;

		for (AVLTree tree : hashTable) {
			int height = tree.getHeight();
			if (height > 0) {
				totalHeight += height;
				nonEmptyTrees++;
			}
		}

		double averageHeight;
		if (nonEmptyTrees == 0) {
			averageHeight = 0;

		} else {
			averageHeight = (double) totalHeight / nonEmptyTrees;
		}

		return averageHeight > 3;
	}

	private double getLoadFactor() {
		return (double) numberOfMovies / size;
	}

	public void deallocate() {

		if (hashTable != null) {
			for (int i = 0; i < hashTable.length; i++) {
				if (hashTable[i] != null) {
					hashTable[i].clear();
					hashTable[i] = null;
				}
			}
		}

		size = 0;
		numberOfMovies = 0;
		hashTable = new AVLTree[0];

	}

	/// -------------------------------------------------------------

	private int collectMovies(AVLNode node, Movie[] movies, int index) {
		if (node != null) {
			index = collectMovies(node.left, movies, index);
			movies[index++] = node.movie;
			index = collectMovies(node.right, movies, index);
		}
		return index;
	}

//	public void addMovie(Movie movie) {
//		int index = hashFunction(movie.getTitle());
//		hashTable[index].insert(movie);
//
//		if (isResizeNeeded()) {
//			resizeHashTable();
//		}
//	}

	private int countMoviesInHashTable() {
		int count = 0;
		for (AVLTree tree : hashTable) {
			count += countMoviesInTree(tree.root);
		}
		return count;
	}

	private int countMoviesInTree(AVLNode node) {
		if (node == null)
			return 0;
		return 1 + countMoviesInTree(node.left) + countMoviesInTree(node.right);
	}

	private void rehashTree(AVLNode node, AVLTree[] newHashTable, int newSize) {
		if (node != null) {
			rehashTree(node.left, newHashTable, newSize);

			int newIndex = Math.abs(node.movie.getTitle().hashCode()) % newSize;
			newHashTable[newIndex].insert(node.movie);

			rehashTree(node.right, newHashTable, newSize);
		}
	}

	/// -------------------------------------------------------------
	/// -------------------------------------------------------------

	public int getHashTableSize() {
		return hashTable.length;
	}

	public AVLTree getTreeAt(int i) {

		if (i >= 0 && i < hashTable.length) {
			return hashTable[i];
		}
		return null;
	}

	public Movie findMovieByTitle(String title) {

		int index = hashFunction(title);

		AVLTree tree = hashTable[index];

		if (tree != null) {
			return tree.search(title);
		}

		return null;
	}

	/// -------------------------------------------------------------
	/// -------------------------------------------------------------

	// Retrieve a movie by its title
	public Movie get(String title) {

		int index = hashFunction(title);
		AVLTree tree = hashTable[index];

		if (tree != null && !tree.isEmpty()) {
			return searchInNode(tree.root, title);
		}

		return null;
	}

	private Movie searchInNode(AVLNode node, String title) {

		if (node == null)
			return null;
		if (title.compareTo(node.movie.getTitle()) < 0) {
			return searchInNode(node.left, title);
		} else if (title.compareTo(node.movie.getTitle()) > 0) {
			return searchInNode(node.right, title);
		} else {
			return node.movie;

		}
	}

	public Movie[] getAllMovies() {

		int totalMovies = countMoviesInHashTable();
		Movie[] movies = new Movie[totalMovies];
		int index = 0;

		for (AVLTree tree : hashTable) {
			index = collectMovies(tree.root, movies, index);
		}

		return movies;

	}
}