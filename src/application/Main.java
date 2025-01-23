package application;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;

public class Main extends Application {

	private HashTableMovieCatalog movieCatalog = new HashTableMovieCatalog(11);
	private TableView<Movie> tableView = new TableView<>();

	private GridPane formPane;
	private BorderPane layout = new BorderPane();
	private VBox leftContainer = new VBox();

	/// -------------------------------------------------------------

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("Movie Catalog Management System");

		setupTableView();

		Button loadFile = createButton("Load File", e -> loadMoviesFromFile(primaryStage));
		Button saveFile = createButton("Save File", e -> saveMoviesToFile(primaryStage));
		Button addMovie = createButton("Add Movie", e -> showAddMovieForm());
		Button updateMovie = createButton("Update Movie", e -> showUpdateMovieForm());
		Button deleteMovie = createButton("Delete Movie", e -> deleteMovie());
		Button searchMovie = createButton("Search Movie", e -> searchMovie());
		Button printSorted = createButton("Print Sorted", e -> printSortedMovies());
		Button printTopLeast = createButton("Print Top & Least", e -> printTopAndLeastMovies());

		// Button countButton = createButton("Count Rows", e -> {
//			int rowCount = tableView.getItems().size();
//			showAlert("Row Count", "The number of rows in the TableView is: " + rowCount);
//		});

		Button hashTableCountButton = new Button("HashTable Indexes");
		hashTableCountButton.setOnAction(e -> {
			int hashTableSize = movieCatalog.getHashTableSize();
			showAlert("HashTable Size", "The number of indexes in the HashTable is: " + hashTableSize);
		});

		Button exitButton = createButton("Exit", e -> {
			movieCatalog.deallocate();
			System.exit(0);
		});

		HBox buttonBox = new HBox(10, loadFile, saveFile, addMovie, updateMovie, deleteMovie, searchMovie, printSorted,
				printTopLeast, hashTableCountButton, exitButton);
		buttonBox.setAlignment(Pos.CENTER_LEFT);
		buttonBox.setPadding(new Insets(10));

		formPane = new GridPane();
		formPane.setPadding(new Insets(15));
		formPane.setVgap(10);
		formPane.setHgap(10);
		formPane.setAlignment(Pos.CENTER);

		leftContainer.setAlignment(Pos.TOP_LEFT);
		leftContainer.setPadding(new Insets(20));
		leftContainer.getChildren().add(tableView);

		HBox mainContainer = new HBox();
		mainContainer.getChildren().addAll(leftContainer, formPane);

		layout.setTop(buttonBox);
		layout.setCenter(mainContainer);
		layout.setBottom(null);

		Scene scene = new Scene(layout, 1300, 750);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/// -------------------------------------------------------------

	//// create method add
	private void showAddMovieForm() {

		formPane.getChildren().clear();
		Label titleLabel = new Label("Title:");
		TextField titleField = new TextField();
		Label descriptionLabel = new Label("Description:");
		TextField descriptionField = new TextField();
		Label yearLabel = new Label("Release Year:");

		ComboBox<String> yearComboBox = new ComboBox<>();
		yearComboBox.getItems().add("Year");
		for (int year = 1888; year <= 2030; year++) {
			yearComboBox.getItems().add(String.valueOf(year));
		}
		yearComboBox.setValue("Year");

		Label ratingLabel = new Label("Rating:");
		TextField ratingField = new TextField();
		// create button add and action for this is method
		Button addButton = createButton("Add Movie", e -> {
			String title = titleField.getText().trim();
			String description = descriptionField.getText().trim();
			String ratingText = ratingField.getText().trim();

			// Validate the inputs
			if (title.isEmpty() || description.isEmpty()) {
				showAlert("Error", "Title and description cannot be empty.");
				return;
			}
			if (movieCatalog.get(title) != null) {
				showAlert("Error", "The Title is unique .");
				return;
			}

			int releaseYear;
			double rating;

			try {
				// Get the release year from the ComboBox
				if (yearComboBox.getValue() == null || yearComboBox.getValue().equals("Year")) {
					showAlert("Error", "Please select a valid release year.");
					return;
				}

				releaseYear = Integer.parseInt(yearComboBox.getValue());
			} catch (NumberFormatException o) {
				showAlert("Error", "Release year must be a valid year.");
				return;
			}

			try {
				rating = Double.parseDouble(ratingText);
				if (rating < 0.0 || rating > 10.0) {
					showAlert("Error", "Rating must be between 0.0 and 10.0.");
					return;
				}
			} catch (NumberFormatException o) {
				showAlert("Error", "Rating must be a valid number.");
				return;
			}

			// Create and add the movie
			Movie newMovie = new Movie(title, description, releaseYear, rating);

			// Add the movie to the catalog
			movieCatalog.put(newMovie);

			// Update the movie list and refresh the table
			tableView.getItems().add(newMovie);

			// Clear the input fields
			titleField.clear();
			descriptionField.clear();
			yearComboBox.setValue(null); // Reset the DatePicker
			ratingField.clear();

			// Show success message
			showAlert("Success", "Movie added successfully!");
		});

		formPane.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");
///put in the grid pane
		formPane.add(titleLabel, 0, 0);
		formPane.add(titleField, 1, 0);
		formPane.add(descriptionLabel, 0, 1);
		formPane.add(descriptionField, 1, 1);
		formPane.add(yearLabel, 0, 2);
		formPane.add(yearComboBox, 1, 2);
		formPane.add(ratingLabel, 0, 3);
		formPane.add(ratingField, 1, 3);
		formPane.add(addButton, 1, 4);

	}

	/// -------------------------------------------------------------

/////create method update
	private void showUpdateMovieForm() {
		formPane.getChildren().clear();
		Label titleLabel = new Label("Title:");
		TextField titleField = new TextField();

		Label descriptionLabel = new Label("Description:");
		TextField descriptionField = new TextField();

		Label yearLabel = new Label("Release Year:");

		ComboBox<String> yearComboBox = new ComboBox<>();
		yearComboBox.getItems().add("Year");
		for (int year = 1888; year <= 2030; year++) {
			yearComboBox.getItems().add(String.valueOf(year));
		}
		yearComboBox.setValue("Year");

		Label ratingLabel = new Label("Rating:");
		TextField ratingField = new TextField();

		Button findButton = new Button("Find");
		Button updateButton = createButton("Update Movie", e -> {
			String title = titleField.getText().trim();
			String updatedDescription = descriptionField.getText().trim();
			String updatedRatingText = ratingField.getText().trim();

			if (title.isEmpty()) {
				showAlert("Error", "Movie title cannot be empty.");
				return;
			}

			// Case-insensitive search for the movie in the tableView
			Movie movieToUpdate = null;
			for (Movie movie : tableView.getItems()) {
				if (movie.getTitle().equalsIgnoreCase(title)) { // Case-insensitive comparison
					movieToUpdate = movie;
					break;
				}
			}

			if (movieToUpdate != null) {
				try {
					// Validate new release year
					int updatedReleaseYear = movieToUpdate.getReleaseYear(); // Default to current value
					if (yearComboBox.getValue() != null && !yearComboBox.getValue().equals("Year")) {
						updatedReleaseYear = Integer.parseInt(yearComboBox.getValue());
					}

					// Validate and parse new rating
					double updatedRating = movieToUpdate.getRating(); // Default to current value
					if (!updatedRatingText.isEmpty()) {
						updatedRating = Double.parseDouble(updatedRatingText);
						if (updatedRating < 0.0 || updatedRating > 10.0) {
							showAlert("Error", "Rating must be between 0.0 and 10.0.");
							return;
						}
					}

					// If no new description is provided, keep the current description
					if (updatedDescription.isEmpty()) {
						updatedDescription = movieToUpdate.getDescription();
					}

					// Update movie details directly
					movieToUpdate.setDescription(updatedDescription);
					movieToUpdate.setReleaseYear(updatedReleaseYear);
					movieToUpdate.setRating(updatedRating);

					// Refresh the TableView to reflect changes
					tableView.refresh();

					showAlert("Success", "Movie updated successfully!");

				} catch (NumberFormatException ex) {
					showAlert("Error", "Please enter valid numbers for Release Year and Rating.");
				}

			} else {
				showAlert("Error", "Movie not found.");
			}

			// Clear the input fields
			titleField.clear();
			descriptionField.clear();
			yearComboBox.setValue("Year"); // Reset the ComboBox
			ratingField.clear();
		});

		findButton.setOnAction(e -> {
			String title = titleField.getText().trim();
			if (title.isEmpty()) {
				showAlert("Error", "Title is required.");
				return;
			}

			Movie movieToFind = movieCatalog.findMovieByTitle(title);

			if (movieToFind != null) {

				descriptionField.setText(movieToFind.getDescription());
				yearComboBox.setValue(String.valueOf(movieToFind.getReleaseYear()));
				ratingField.setText(String.valueOf(movieToFind.getRating()));
				showAlert("Success", "Movie found. You can now update the details.");

			} else {

				showAlert("Error", "No movie found with the given title.");
				descriptionField.clear();
				yearComboBox.setValue("Year");
				ratingField.clear();

			}
		});

		formPane.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");

		formPane.add(titleLabel, 0, 0);
		formPane.add(titleField, 1, 0);
		formPane.add(findButton, 2, 0);
		formPane.add(descriptionLabel, 0, 1);
		formPane.add(descriptionField, 1, 1);
		formPane.add(yearLabel, 0, 2);
		formPane.add(yearComboBox, 1, 2);
		formPane.add(ratingLabel, 0, 3);
		formPane.add(ratingField, 1, 3);
		formPane.add(updateButton, 1, 4);

	}

	/// -------------------------------------------------------------

////create method for to the print sorted 
	private void printSortedMovies() {

		formPane.getChildren().clear();

		ComboBox<String> sortOrderBox = new ComboBox<>();
		sortOrderBox.getItems().addAll("Ascending", "Descending");
		sortOrderBox.setValue("Ascending");

		ComboBox<String> indexComboBox = new ComboBox<>();
		for (int i = 0; i < movieCatalog.getHashTableSize(); i++) {
			AVLTree tree = movieCatalog.getTreeAt(i);
			indexComboBox.getItems().add(generateComboBoxText(i, tree));
		}
		indexComboBox.setValue(indexComboBox.getItems().get(0));

		TableView<Movie> sortedTableView = new TableView<>();
		setupSortedTableView(sortedTableView);

		Button loadMoviesButton = createButton("Load Movies", e -> {
			sortedTableView.getItems().clear();
			boolean ascending = sortOrderBox.getValue().equals("Ascending");

			int selectedIndex = parseIndexFromComboBox(indexComboBox.getValue());
			AVLTree tree = movieCatalog.getTreeAt(selectedIndex);

			if (tree != null && !tree.isEmpty()) {
				if (ascending) {
					tree.getAllMoviesSortedAscending(sortedTableView);
				} else {
					tree.getAllMoviesSortedDescending(sortedTableView);
				}
			} else {
				showAlert("Info", "The selected hash cell is empty.");
			}
		});

		Button nextButton = createButton("Next --->", e -> {
			int currentIndex = parseIndexFromComboBox(indexComboBox.getValue());
			int nextIndex = (currentIndex + 1) % movieCatalog.getHashTableSize();
			indexComboBox.setValue(indexComboBox.getItems().get(nextIndex));
			sortedTableView.getItems().clear();
			boolean ascending = sortOrderBox.getValue().equals("Ascending");
			AVLTree tree = movieCatalog.getTreeAt(nextIndex);
			if (tree != null && !tree.isEmpty()) {
				if (ascending) {
					tree.getAllMoviesSortedAscending(sortedTableView);
				} else {
					tree.getAllMoviesSortedDescending(sortedTableView);
				}
			} else {
				showAlert("Info", "The selected hash cell is empty.");
			}
		});

		Button previousButton = createButton("<--- Previous", e -> {
			int currentIndex = parseIndexFromComboBox(indexComboBox.getValue());
			int previousIndex = (currentIndex - 1 + movieCatalog.getHashTableSize()) % movieCatalog.getHashTableSize();
			indexComboBox.setValue(indexComboBox.getItems().get(previousIndex));
			sortedTableView.getItems().clear();
			boolean ascending = sortOrderBox.getValue().equals("Ascending");
			AVLTree tree = movieCatalog.getTreeAt(previousIndex);
			if (tree != null && !tree.isEmpty()) {
				if (ascending) {
					tree.getAllMoviesSortedAscending(sortedTableView);
				} else {
					tree.getAllMoviesSortedDescending(sortedTableView);
				}
			} else {
				showAlert("Info", "The selected hash cell is empty.");
			}
		});

		Label sortOrderLabel = new Label("Sort Order:");
		Label indexLabel = new Label("Select Index:");

		HBox controlsBox = new HBox(10);
		controlsBox.getChildren().addAll(indexLabel, indexComboBox, sortOrderLabel, sortOrderBox, loadMoviesButton);
		controlsBox.setAlignment(Pos.CENTER);

		HBox navigationButtons = new HBox(10, previousButton, nextButton);
		navigationButtons.setAlignment(Pos.CENTER);

		VBox mainContainer = new VBox(10);
		mainContainer.getChildren().addAll(controlsBox, sortedTableView, navigationButtons);
		mainContainer.setPadding(new Insets(15));

		formPane.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");

		formPane.add(mainContainer, 0, 0);
	}

	/// -------------------------------------------------------------

	private void setupSortedTableView(TableView<Movie> sortedTableView) {

		TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTitle()));
		titleColumn.setPrefWidth(110);

		TableColumn<Movie, String> descriptionColumn = new TableColumn<>("Description");
		descriptionColumn
				.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDescription()));
		descriptionColumn.setPrefWidth(200);

		TableColumn<Movie, Integer> yearColumn = new TableColumn<>("Release Year");
		yearColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getReleaseYear()));
		yearColumn.setPrefWidth(100);

		TableColumn<Movie, Double> ratingColumn = new TableColumn<>("Rating");
		ratingColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getRating()));
		ratingColumn.setPrefWidth(100);

		sortedTableView.getColumns().addAll(titleColumn, descriptionColumn, yearColumn, ratingColumn);

		sortedTableView.setStyle("-fx-border-color: #6A5ACD; -fx-font-size: 14; -fx-border-width: 1; "
				+ "-fx-border-radius: 10; -fx-background-color: #E6E6FA; -fx-background-radius: 10;");

		sortedTableView.setMaxWidth(510);
		sortedTableView.setPrefWidth(510);
		sortedTableView.setPrefHeight(300);

		sortedTableView.setRowFactory(tv -> {
			TableRow<Movie> row = new TableRow<>();
			row.setStyle("-fx-background-color: #E6E6FA;");
			row.setOnMouseEntered(event -> row.setStyle("-fx-background-color: #B0C4DE;"));
			row.setOnMouseExited(event -> row.setStyle("-fx-background-color: #E6E6FA;"));
			return row;
		});
	}

	/// -------------------------------------------------------------

	private void setupTableView(TableView<Movie> tableView) {

		TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTitle()));
		titleColumn.setPrefWidth(130);

		TableColumn<Movie, String> descriptionColumn = new TableColumn<>("Description");
		descriptionColumn
				.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDescription()));
		descriptionColumn.setPrefWidth(200);

		descriptionColumn.setCellFactory(column -> {
			return new TableCell<Movie, String>() {
				private final TextArea textArea = new TextArea();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setGraphic(null);
					} else {
						textArea.setText(item);
						textArea.setWrapText(false);
						textArea.setEditable(false);
						textArea.setMinHeight(50);
						textArea.setMaxHeight(100);
						setGraphic(textArea);
					}
				}
			};
		});

		TableColumn<Movie, Integer> yearColumn = new TableColumn<>("Release Year");
		yearColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getReleaseYear()));
		yearColumn.setPrefWidth(125);

		TableColumn<Movie, Double> ratingColumn = new TableColumn<>("Rating");
		ratingColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getRating()));
		ratingColumn.setPrefWidth(125);

		tableView.getColumns().addAll(titleColumn, descriptionColumn, yearColumn, ratingColumn);

		tableView.setStyle("-fx-border-color: #6A5ACD; -fx-font-size: 14; -fx-border-width: 1; "
				+ "-fx-border-radius: 10; -fx-background-color: #E6E6FA; -fx-background-radius: 10;");

		tableView.setMaxWidth(580);
		tableView.setPrefWidth(580);
		tableView.setPrefHeight(600);

		tableView.setRowFactory(tv -> {
			TableRow<Movie> row = new TableRow<>();
			row.setStyle("-fx-background-color: #E6E6FA;");
			row.setOnMouseEntered(event -> row.setStyle("-fx-background-color: #B0C4DE;"));
			row.setOnMouseExited(event -> row.setStyle("-fx-background-color: #E6E6FA;"));
			return row;
		});

		VBox leftContent = new VBox(tableView);
		leftContent.setAlignment(Pos.TOP_LEFT);
		leftContent.setPadding(new Insets(20));

		layout.setLeft(leftContent);

	}

	/// -------------------------------------------------------------

	private void printTopAndLeastMovies() {

		formPane.getChildren().clear();

		ComboBox<String> indexComboBox = new ComboBox<>();
		for (int i = 0; i < movieCatalog.getHashTableSize(); i++) {
			AVLTree tree = movieCatalog.getTreeAt(i);
			indexComboBox.getItems().add(generateComboBoxText(i, tree));
		}
		indexComboBox.setValue(indexComboBox.getItems().get(0)); // تعيين القيمة الافتراضية

		TableView<Movie> topLeastTableView = new TableView<>();
		setupTopLeastTableView(topLeastTableView);

		Button loadMoviesButton = createButton("Load Movies", e -> {
			int selectedIndex = parseIndexFromComboBox(indexComboBox.getValue());
			updateTopLeastMovies(topLeastTableView, selectedIndex);
		});

		Button nextButton = createButton("Next --->", e -> {
			int currentIndex = parseIndexFromComboBox(indexComboBox.getValue());
			int nextIndex = (currentIndex + 1) % movieCatalog.getHashTableSize();
			indexComboBox.setValue(indexComboBox.getItems().get(nextIndex));
			updateTopLeastMovies(topLeastTableView, nextIndex);
		});

		Button previousButton = createButton("<--- Previous", e -> {
			int currentIndex = parseIndexFromComboBox(indexComboBox.getValue());
			int previousIndex = (currentIndex - 1 + movieCatalog.getHashTableSize()) % movieCatalog.getHashTableSize();
			indexComboBox.setValue(indexComboBox.getItems().get(previousIndex));
			updateTopLeastMovies(topLeastTableView, previousIndex);
		});

		HBox controlsBox = new HBox(10, new Label("Select Index:"), indexComboBox, loadMoviesButton);
		controlsBox.setAlignment(Pos.CENTER);

		HBox navigationButtons = new HBox(10, previousButton, nextButton);
		navigationButtons.setAlignment(Pos.CENTER);

		VBox mainContainer = new VBox(10, controlsBox, topLeastTableView, navigationButtons);
		mainContainer.setPadding(new Insets(15));
		mainContainer.setAlignment(Pos.CENTER);

		formPane.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");

		formPane.add(mainContainer, 0, 0);
	}

	private String generateComboBoxText(int index, AVLTree tree) {
		if (tree == null || tree.isEmpty()) {
			return "Index " + index + " - [No Movies]";
		}

		Movie representativeMovie = tree.getAnyMovie();
		return "Index " + index + " - [" + representativeMovie.getTitle() + "]";
	}

	private int parseIndexFromComboBox(String comboValue) {

		return Integer.parseInt(comboValue.split(" ")[1]);

	}

	private void updateTopLeastMovies(TableView<Movie> tableView, int index) {

		tableView.getItems().clear();
		AVLTree tree = movieCatalog.getTreeAt(index);
		if (tree != null && !tree.isEmpty()) {
			Movie topRated = tree.getMaxRatingMovie();
			Movie leastRated = tree.getMinRatingMovie();
			if (topRated != null)
				tableView.getItems().add(topRated);
			if (leastRated != null)
				tableView.getItems().add(leastRated);

		}
	}

	private void setupTopLeastTableView(TableView<Movie> tableView) {

		TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTitle()));
		titleColumn.setPrefWidth(150);

		TableColumn<Movie, Double> ratingColumn = new TableColumn<>("Rating");
		ratingColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getRating()));
		ratingColumn.setPrefWidth(100);

		TableColumn<Movie, Integer> yearColumn = new TableColumn<>("Release Year");
		yearColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getReleaseYear()));
		yearColumn.setPrefWidth(100);

		TableColumn<Movie, String> descriptionColumn = new TableColumn<>("Description");
		descriptionColumn
				.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDescription()));
		descriptionColumn.setPrefWidth(200);

		tableView.getColumns().addAll(titleColumn, ratingColumn, yearColumn, descriptionColumn);

		tableView.setStyle("-fx-border-color: #6A5ACD; -fx-font-size: 14; -fx-border-width: 1; "
				+ "-fx-border-radius: 10; -fx-background-color: #E6E6FA; -fx-background-radius: 10;");
		tableView.setPrefHeight(300);

		tableView.setRowFactory(tv -> {
			TableRow<Movie> row = new TableRow<>();
			row.setStyle("-fx-background-color: #E6E6FA;");
			row.setOnMouseEntered(event -> row.setStyle("-fx-background-color: #B0C4DE;"));
			row.setOnMouseExited(event -> row.setStyle("-fx-background-color: #E6E6FA;"));
			return row;
		});
	}

	/// -------------------------------------------------------------

	private void setupTableView() {

		setupTableView(tableView);

	}

	/// -------------------------------------------------------------

	private Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {

		Button button = new Button(text);
		button.setOnAction(action);
		return button;

	}

	/// -------------------------------------------------------------

	private void deleteMovie() {
		formPane.getChildren().clear();
		Label titleLabel = new Label("Title:");
		TextField titleField = new TextField();

		Button deleteButton = createButton("Delete", e -> {
			String titleToDelete = titleField.getText().trim();

			// Validate input
			if (titleToDelete.isEmpty()) {
				showAlert("Error", "Please enter a movie title to delete.");
				return;
			}

			// Case-insensitive search for the movie to delete
			Movie movieToDelete = null;
			for (AVLTree tree : movieCatalog.getHashTable()) { // Iterate through the hash table
				if (tree != null && !tree.isEmpty()) {
					for (Movie movie : tree.getAllMovies()) { // Fetch all movies from the AVL tree
						if (movie.getTitle().equalsIgnoreCase(titleToDelete)) { // Case-insensitive comparison
							movieToDelete = movie;
							tree.delete(movie.getTitle()); // Remove movie from the AVLTree
							break;
						}
					}
				}
				if (movieToDelete != null)
					break; // Stop searching once the movie is found and deleted
			}

			if (movieToDelete != null) {
				// Remove the movie from the TableView
				tableView.getItems().remove(movieToDelete);
				showAlert("Success", "Movie '" + titleToDelete + "' deleted successfully!");
			} else {
				showAlert("Error", "Movie not found. Please check the title and try again.");
			}

			// Clear the title field
			titleField.clear();
		});

		// Style the formPane
		formPane.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");

		// Add components to the formPane
		formPane.add(titleLabel, 0, 0);
		formPane.add(titleField, 1, 0);
		formPane.add(deleteButton, 1, 1);
	}

	/// -------------------------------------------------------------

	private void searchMovie() {
		formPane.getChildren().clear();

		Label searchTypeLabel = new Label("Select Search Type:");
		ComboBox<String> searchTypeComboBox = new ComboBox<>();
		searchTypeComboBox.getItems().addAll("Title", "Year");

		Label titleLabel = new Label("Title:");
		TextField titleField = new TextField();

		Label yearLabel = new Label("Year:");
		TextField yearField = new TextField();

		titleLabel.setVisible(false);
		titleField.setVisible(false);
		yearLabel.setVisible(false);
		yearField.setVisible(false);

		searchTypeComboBox.setOnAction(e -> {
			String selectedType = searchTypeComboBox.getValue();

			if ("Title".equals(selectedType)) {
				titleLabel.setVisible(true);
				titleField.setVisible(true);
				yearLabel.setVisible(false);
				yearField.setVisible(false);
			} else if ("Year".equals(selectedType)) {
				titleLabel.setVisible(false);
				titleField.setVisible(false);
				yearLabel.setVisible(true);
				yearField.setVisible(true);
			}
		});

		Button searchButton = createButton("Search", e -> {
			if (searchTypeComboBox.getValue() == null) {
				showAlert("Error", "Please select a search type (Title or Year).");
				return;
			}

			String searchQuery = searchTypeComboBox.getValue().equals("Title") ? titleField.getText().trim()
					: yearField.getText().trim();

			// Validate input
			if (searchQuery.isEmpty()) {
				showAlert("Error", "Please enter a " + searchTypeComboBox.getValue().toLowerCase() + " to search.");
				return;
			}

			boolean found = false;

			try {
				for (AVLTree tree : movieCatalog.getHashTable()) { // Iterate through the hash table
					if (tree != null && !tree.isEmpty()) {
						for (Movie movie : tree.getAllMovies()) { // Fetch all movies from the AVL tree
							if ((searchTypeComboBox.getValue().equals("Title")
									&& movie.getTitle().equalsIgnoreCase(searchQuery))
									|| (searchTypeComboBox.getValue().equals("Year")
											&& String.valueOf(movie.getReleaseYear()).equals(searchQuery))) {
								found = true;
								showAlert("Search Result", "Movie found: " + movie.toString());
								break;
							}
						}
					}
					if (found)
						break; // Stop searching once a match is found
				}

				if (!found) {
					showAlert("No Results", "No movies found matching your query.");
				}

			} catch (Exception ex) {
				showAlert("Error", "An error occurred while searching for movies. Please try again.");
			}

			// Clear the fields
			titleField.clear();
			yearField.clear();
		});

		Button clearButton = createButton("Clear", e -> {
			titleField.clear();
			yearField.clear();
			titleLabel.setVisible(false);
			titleField.setVisible(false);
			yearLabel.setVisible(false);
			yearField.setVisible(false);
			searchTypeComboBox.setValue(null);
		});

		// Style the formPane
		formPane.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");

		// Add components to the formPane
		formPane.add(searchTypeLabel, 0, 0);
		formPane.add(searchTypeComboBox, 1, 0);
		formPane.add(titleLabel, 0, 1);
		formPane.add(titleField, 1, 1);
		formPane.add(yearLabel, 0, 2);
		formPane.add(yearField, 1, 2);
		formPane.add(searchButton, 1, 3);
		formPane.add(clearButton, 1, 4);
	}

	/// ------------------------------------------------------------
	/// -------------------------------------------------------------

	private void loadMoviesFromFile(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Movies");
		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith("Title:")) {
						try {
							String title = line.replace("Title:", "").trim();

							String description = reader.readLine().replace("Description:", "").trim();

							int releaseYear = Integer.parseInt(reader.readLine().replace("Release Year:", "").trim());

							double rating = Double.parseDouble(reader.readLine().replace("Rating:", "").trim());

							Movie movie = new Movie(title, description, releaseYear, rating);
							movieCatalog.put(movie);

							reader.readLine();
						} catch (Exception ex) {
							System.err.println("Error reading movie: " + ex.getMessage());
						}
					}
				}
				updateTableView();
				showAlert("Success", "Movies loaded successfully from file!");
			} catch (IOException e) {
				showAlert("Error", "Error reading file.");
			}
		} else {
			showAlert("Error", "No file selected.");
		}
	}

	/// -------------------------------------------------------------

	private void saveMoviesToFile(Stage stage) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Movies");
		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

				for (Movie movie : movieCatalog.getAllMovies()) {
					writer.write("Title: " + movie.getTitle());
					writer.newLine();
					writer.write("Description: " + movie.getDescription());
					writer.newLine();
					writer.write("Release Year: " + movie.getReleaseYear());
					writer.newLine();
					writer.write("Rating: " + movie.getRating());
					writer.newLine();
					writer.newLine();
				}

			} catch (IOException e) {

				showAlert("Error", "Failed to save file.");

			}
		}
	}

	/// -------------------------------------------------------------

	private void updateTableView() {

		tableView.getItems().clear();
		tableView.getItems().addAll(movieCatalog.getAllMovies());

	}

	/// -------------------------------------------------------------

	private void showAlert(String title, String message) {

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();

	}

	/// ---------------------------------------------------

	public static void main(String[] args) {
		launch(args);
	}

	/// -------------------------------------------------------------
	/// -------------------------------------------------------------

}