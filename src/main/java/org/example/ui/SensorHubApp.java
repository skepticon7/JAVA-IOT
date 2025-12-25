package org.example.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.example.coordinators.DeviceSensorCoordinator;
import org.example.model.Device;
import org.example.model.Reading;
import org.example.model.TemperatureSensor;
import org.example.mqtt.MqttClientProvider;
import org.example.runners.TemperatureSensorRunner;

import java.io.*;
import java.util.*;
import java.util.List;

public class SensorHubApp extends Application {
//    private Map<String, String> users = new HashMap<>();
    private List<Sensor> sensors = new ArrayList<>();
    private Stage stage;
//    private String currentUser;
    private BorderPane mainLayout;
    private VBox contentArea;
    private String selectedView = "Dashboard";
    private Timeline updateTimeline;
    private static DeviceSensorCoordinator coordinator;

    public SensorHubApp() {

    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("SensorHub");
//        showLoginScreen();
        showDashboard();

        stage.show();
    }

    @Override
    public void stop() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
    }

    private void loadSensorsFromDB() {
        try{
            sensors.clear();
            List<TemperatureSensor> temperatureSensorList = coordinator.getDeviceService().getAllTemperatureSensors();
            temperatureSensorList.forEach(tmp -> {
                sensors.add(new Sensor(tmp.getName() , tmp.getType().toString() , tmp.getStatus().toString()));
            });
            sensors.forEach(this::createSensorCard);
            updateContent();
            coordinator.start(temperatureSensorList);

            coordinator.getReadingService().addListener(reading -> {
                Platform.runLater(() -> {
                    updateSensorValueInUI(reading);
                });
            });

        }catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load sensors from DB", Alert.AlertType.ERROR);
        }
    }

    private void updateSensorValueInUI(Reading reading) {
        for (Sensor s : sensors) {
            if (s.getName().equalsIgnoreCase(reading.getDevice().getName())) {
                s.value = reading.getValue();
            }
        }
        updateContent();
    }

    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1e293b, #581c87, #1e293b);");


        VBox appIntro = new VBox(2);
        appIntro.setAlignment(Pos.CENTER);


        HBox logoContainer = new HBox(10);

        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setPadding(new Insets(10));
        logoContainer.setMaxWidth(Region.USE_PREF_SIZE);
        logoContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        logoContainer.setStyle( "-fx-background-color: linear-gradient(to bottom right, #a855f7, #ec4899);" +
                "-fx-border-color: white;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;");

        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sensor.png")));
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(20);
        logoView.setFitWidth(20);
        logoContainer.getChildren().add(logoView);

        Label title = new Label("SensorHub");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 30));
        title.setStyle("-fx-text-fill: white;");

        Label subtitle = new Label("Real-time monitoring platform");
        subtitle.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 15));
        subtitle.setStyle("-fx-text-fill: #c4b5fd;");

        appIntro.getChildren().addAll(logoContainer , title, subtitle);

        // for signup
        TextField usernameFieldSignup = new TextField();
        usernameFieldSignup.setFont(Font.font("Inter", FontWeight.MEDIUM, 15));
        usernameFieldSignup.setPadding(new Insets(10 , 15 , 10 , 15));
        usernameFieldSignup.setPromptText("Username");
        usernameFieldSignup.setMaxWidth(300);
        usernameFieldSignup.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.5); -fx-background-radius: 5;");

        TextField emailFieldSignup = new TextField();
        emailFieldSignup.setFont(Font.font("Inter", FontWeight.MEDIUM, 15));
        emailFieldSignup.setPadding(new Insets(10 , 15 , 10 , 15));
        emailFieldSignup.setPromptText("Email");
        emailFieldSignup.setMaxWidth(300);
        emailFieldSignup.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.5); -fx-background-radius: 5;");


        PasswordField passFieldSignup = new PasswordField();
        passFieldSignup.setFont(Font.font("Inter", FontWeight.MEDIUM, 15));
        passFieldSignup.setPadding(new Insets(10 , 15 , 10 , 15));
        passFieldSignup.setPromptText("Password");
        passFieldSignup.setMaxWidth(300);
        passFieldSignup.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.5); -fx-background-radius: 5;");

        // for login
        TextField emailFieldLogin = new TextField();
        emailFieldLogin.setFont(Font.font("Inter", FontWeight.MEDIUM, 15));
        emailFieldLogin.setPadding(new Insets(10 , 15 , 10 , 15));
        emailFieldLogin.setPromptText("Email");
        emailFieldLogin.setMaxWidth(300);
        emailFieldLogin.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.5); -fx-background-radius: 5;");


        PasswordField passFieldLogin = new PasswordField();
        passFieldLogin.setFont(Font.font("Inter", FontWeight.MEDIUM, 15));
        passFieldLogin.setPadding(new Insets(10 , 15 , 10 , 15));
        passFieldLogin.setPromptText("Password");
        passFieldLogin.setMaxWidth(300);
        passFieldLogin.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.5); -fx-background-radius: 5;");

        Label msgLabel = new Label();
        msgLabel.setManaged(false);
        msgLabel.setVisible(false);
        msgLabel.setStyle("-fx-text-fill: #fca5a5;");


        Text signupLink = new Text("Don't Have An Account Yet ? Signup");
        signupLink.setFont(Font.font("Inter", FontWeight.NORMAL, 12));
        signupLink.setCursor(Cursor.HAND);
        signupLink.setFill(Color.WHITE);

        Text loginLink = new Text("Already Have An Account ? Login");
        loginLink.setFill(Color.WHITE);
        loginLink.setCursor(Cursor.HAND);
        loginLink.setFont(Font.font("Inter", FontWeight.NORMAL, 12));


        Button loginBtn = new Button("Login");
        loginBtn.setCursor(Cursor.HAND);
        loginBtn.setFont(Font.font("Inter", FontWeight.BOLD, 15));
        loginBtn.setMaxWidth(300);
        loginBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #a855f7, #ec4899);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 10 0;"
        );

        Button signupBtn = new Button("Sign Up");
        signupBtn.setCursor(Cursor.HAND);
        signupBtn.setFont(Font.font("Inter", FontWeight.BOLD, 15));
        signupBtn.setMaxWidth(300);
        signupBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #a855f7, #ec4899);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 10 0;"
        );


        VBox loginFields = new VBox(10 , emailFieldLogin , passFieldLogin  ,loginBtn , signupLink);
        VBox signupFields = new VBox(10  , emailFieldSignup , usernameFieldSignup ,passFieldSignup , signupBtn , loginLink);
        loginFields.setAlignment(Pos.CENTER);
        signupFields.setAlignment(Pos.CENTER);

        StackPane formsPane = new StackPane();
        formsPane.getChildren().addAll(loginFields , signupFields);



        HBox.setHgrow(loginBtn, Priority.ALWAYS);
        HBox.setHgrow(signupBtn, Priority.ALWAYS);


        root.getChildren().addAll(appIntro , formsPane);
        signupFields.setVisible(false);

        signupLink.setOnMouseEntered(e -> signupLink.setUnderline(true));
        signupLink.setOnMouseExited(e -> signupLink.setUnderline(false));

        signupLink.setOnMouseClicked(e -> {
           loginFields.setVisible(false);
           signupFields.setVisible(true);
        });

        loginLink.setOnMouseEntered(e -> loginLink.setUnderline(true));
        loginLink.setOnMouseExited(e -> loginLink.setUnderline(false));
        loginLink.setOnMouseClicked(e -> {
            loginFields.setVisible(true);
            signupFields.setVisible(false);
        });
        signupBtn.setOnAction(e -> {
            String username = usernameFieldSignup.getText().trim();
            String email = emailFieldSignup.getText().trim();
            String password = passFieldSignup.getText().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert("All fields must be filled!", Alert.AlertType.WARNING);
            }else {
                try{

                } catch (Exception ex) {
                    System.out.println("Error : " + ex.getMessage());
                    showAlert("Internal Server Error", Alert.AlertType.ERROR);
                }

            }
        });

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Warning");
        alert.setHeaderText(null);

        Label content = new Label(message);
        content.setStyle("-fx-text-fill: #fca5a5; -fx-font-weight: bold; -fx-font-size: 14;");
        alert.getDialogPane().setContent(content);

        alert.getDialogPane().setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1e293b, #581c87, #1e293b);"
        );

        alert.showAndWait();
    }

    private void showDashboard() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #1e293b, #581c87, #1e293b);");

        // Sidebar
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        // Content Area
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainLayout.setCenter(scrollPane);

        loadSensorsFromDB();
        updateContent();

        Scene scene = new Scene(mainLayout, 1200, 700);
        stage.setScene(scene);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 0 1 0 0;");

        HBox logoContainer = new HBox(7);
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setPadding(new Insets(5));
        logoContainer.setMaxWidth(Region.USE_PREF_SIZE);
        logoContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        logoContainer.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #a855f7, #ec4899);" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        try {
            Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sensor.png")));
            ImageView logoView = new ImageView(logo);
            logoView.setFitWidth(30);
            logoView.setFitHeight(30);
            logoView.setPreserveRatio(true);
            logoContainer.getChildren().add(logoView);
        } catch (Exception e) {
            // Fallback icon
            Label logoText = new Label("⚡");
            logoText.setFont(Font.font("System", FontWeight.BOLD, 32));
            logoText.setStyle("-fx-text-fill: white;");
            logoContainer.getChildren().add(logoText);
        }

        // Header
        Label title = new Label("SensorHub");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: white;");

        HBox logoTitleContainer = new HBox(8);
        logoTitleContainer.getChildren().addAll(logoContainer, title);

//        Label userLabel = new Label(currentUser);
//        userLabel.setStyle("-fx-text-fill: #c4b5fd; -fx-font-size: 12;");

        Button addBtn = new Button("+ Add Sensor");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setStyle("-fx-background-color: linear-gradient(to right, #a855f7, #ec4899); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10;");
        addBtn.setOnAction(e -> showAddSensorDialog());

        // Navigation
        Label navLabel = new Label("NAVIGATION");
        navLabel.setStyle("-fx-text-fill: #c4b5fd; -fx-font-size: 11; -fx-font-weight: bold;");
        navLabel.setPadding(new Insets(10, 0, 5, 0));

        Button dashBtn = createNavButton("Dashboard", "Dashboard");
        dashBtn.setCursor(Cursor.HAND);

        Button tempBtn = createNavButton("Temperature", "Temperature");
        tempBtn.setCursor(Cursor.HAND);

        Button humBtn = createNavButton("Humidity", "Humidity");
        humBtn.setCursor(Cursor.HAND);

        Button airBtn = createNavButton("Air Quality", "Air Quality");
        airBtn.setCursor(Cursor.HAND);


        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("Export CSV");
        exportBtn.setMaxWidth(Double.MAX_VALUE);
        exportBtn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10;");
        exportBtn.setOnAction(e -> exportCSV());


        sidebar.getChildren().addAll(logoTitleContainer, addBtn, navLabel, dashBtn, tempBtn, humBtn, airBtn, spacer, exportBtn);
        return sidebar;
    }

    private Button createNavButton(String text, String view) {

        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        btn.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: #c4b5fd; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 12;");

        btn.setOnAction(e -> {
            System.out.println("here");
            selectedView = view;
            updateContent();
        });

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 12;");
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: #c4b5fd; -fx-font-weight: bold;  -fx-background-radius: 5; -fx-padding: 12;");
        });



        return btn;
    }

    private void updateContent() {
        contentArea.getChildren().clear();

        if (selectedView.equals("Dashboard")) {
            showDashboardView();
        } else {
            showSensorTypeView(selectedView);
        }
    }

    private void showDashboardView() {
        Label title = new Label("Dashboard Overview");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: white;");

        // Stats Cards
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(15);

        int activeCount = (int) sensors.stream().filter(s -> s.getStatus().equals("Active")).count();
        int warningCount = (int) sensors.stream().filter(s -> s.getStatus().equals("Warning")).count();
        int inactiveCount = (int) sensors.stream().filter(s -> s.getStatus().equals("Inactive")).count();

        statsGrid.add(createStatCard("Total Sensors", String.valueOf(sensors.size()), "#a855f7"), 0, 0);
        statsGrid.add(createStatCard("Active", String.valueOf(activeCount), "#10b981"), 1, 0);
        statsGrid.add(createStatCard("Warnings", String.valueOf(warningCount), "#f59e0b"), 2, 0);
        statsGrid.add(createStatCard("Inactive", String.valueOf(inactiveCount), "#6b7280"), 3, 0);

        // Status Bars
        VBox statusBox = new VBox(15);
        statusBox.setPadding(new Insets(20));
        statusBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 15; -fx-border-width: 1;");

        Label statusTitle = new Label("Sensor Status Distribution");
        statusTitle.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        statusTitle.setStyle("-fx-text-fill: white;");

        statusBox.getChildren().add(statusTitle);
        statusBox.getChildren().add(createStatusBar("Active", activeCount, sensors.size(), "#10b981"));
        statusBox.getChildren().add(createStatusBar("Warning", warningCount, sensors.size(), "#f59e0b"));
        statusBox.getChildren().add(createStatusBar("Inactive", inactiveCount, sensors.size(), "#6b7280"));

        // Type Stats
        GridPane typeGrid = new GridPane();
        typeGrid.setHgap(15);
        typeGrid.setVgap(15);

        typeGrid.add(createTypeStatCard("Temperature", "°C", "#ef4444"), 0, 0);
        typeGrid.add(createTypeStatCard("Humidity", "%", "#3b82f6"), 1, 0);
        typeGrid.add(createTypeStatCard("Air Quality", "AQI", "#10b981"), 2, 0);

        contentArea.getChildren().addAll(title, statsGrid, statusBox, typeGrid);
    }

    private VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 15; -fx-border-width: 1;");

        Label labelText = new Label(label);
        labelText.setStyle("-fx-text-fill: #c4b5fd; -fx-font-size: 12; -fx-font-weight: bold;");

        Label valueText = new Label(value);
        valueText.setFont(Font.font("Inter", FontWeight.BOLD, 32));
        valueText.setStyle("-fx-text-fill: white;");

        card.getChildren().addAll(labelText, valueText);
        return card;
    }

    private VBox createStatusBar(String label, int count, int total, String color) {
        VBox bar = new VBox(8);

        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label countLabel = new Label(count + " / " + total);
        countLabel.setStyle("-fx-text-fill: white;");
        labelBox.getChildren().addAll(nameLabel, spacer, countLabel);

        ProgressBar progress = new ProgressBar();
        progress.setMaxWidth(Double.MAX_VALUE);
        progress.setProgress(total > 0 ? (double) count / total : 0);
        progress.setStyle("-fx-accent: " + color + ";");

        bar.getChildren().addAll(labelBox, progress);
        return bar;
    }

    private VBox createTypeStatCard(String type, String unit, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 15; -fx-border-width: 1;");

        Label typeLabel = new Label(type);
        typeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

        long count = sensors.stream().filter(s -> s.getType().equals(type)).count();
        Label countLabel = new Label(count + " sensors");
        countLabel.setStyle("-fx-text-fill: #c4b5fd; -fx-font-size: 11;");

        double avg = sensors.stream().filter(s -> s.getType().equals(type)).mapToDouble(Sensor::getValue).average().orElse(0);
        Label avgLabel = new Label(String.format("%.1f", avg) + unit);
        avgLabel.setFont(Font.font("Inter", FontWeight.BOLD, 36));
        avgLabel.setStyle("-fx-text-fill: white;");

        Label descLabel = new Label("Average reading");
        descLabel.setStyle("-fx-text-fill: #c4b5fd; -fx-font-size: 12;");

        card.getChildren().addAll(typeLabel, countLabel, avgLabel, descLabel);
        return card;
    }

    private void showSensorTypeView(String type) {
        Label title = new Label(type + " Sensors");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: white;");

        List<Sensor> typeSensors = new ArrayList<>();
        for (Sensor s : sensors) {
            if (s.getType().equalsIgnoreCase(type)) {
                typeSensors.add(s);
            }
        }

        Label subtitle = new Label(typeSensors.size() + " active sensors");
        subtitle.setStyle("-fx-text-fill: #c4b5fd;");

        if (typeSensors.isEmpty()) {
            VBox emptyBox = new VBox(20);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(80));
            emptyBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15;");

            Label emptyLabel = new Label("No " + type + " sensors yet");
            emptyLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));
            emptyLabel.setStyle("-fx-text-fill: white;");

            Label emptyDesc = new Label("Add sensors to start monitoring");
            emptyDesc.setStyle("-fx-text-fill: #c4b5fd;");

            emptyBox.getChildren().addAll(emptyLabel, emptyDesc);
            contentArea.getChildren().addAll(title, subtitle, emptyBox);
        } else {
            FlowPane sensorGrid = new FlowPane();
            sensorGrid.setHgap(15);
            sensorGrid.setVgap(15);

            for (Sensor sensor : typeSensors) {
                sensorGrid.getChildren().add(createSensorCard(sensor));
            }

            contentArea.getChildren().addAll(title, subtitle, sensorGrid);
        }
    }

    private VBox createSensorCard(Sensor sensor) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 15; -fx-border-width: 1;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label(sensor.getStatus());
        statusLabel.setStyle("-fx-background-color: " + getStatusColor(sensor.getStatus()) + "; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 11; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(spacer, statusLabel);

        Label nameLabel = new Label(sensor.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setStyle("-fx-text-fill: white;");

        Label typeLabel = new Label(sensor.getType());
        typeLabel.setStyle("-fx-text-fill: #c4b5fd; -fx-font-size: 12;");

        VBox valueBox = new VBox(5);
        valueBox.setPadding(new Insets(15));
        valueBox.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10;");

        Label valueLabel = new Label(String.format("%.1f", sensor.getValue()));
        valueLabel.setFont(Font.font("Inter", FontWeight.BOLD, 40));
        valueLabel.setStyle("-fx-text-fill: white;");

        Label unitLabel = new Label(getUnit(sensor.getType()));
        unitLabel.setStyle("-fx-text-fill: #c4b5fd; -fx-font-size: 16;");

        valueBox.getChildren().addAll(valueLabel, unitLabel);

        HBox buttonBox = new HBox(10);
        Button statusBtn = new Button("Change Status");
        statusBtn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 15;");
        statusBtn.setOnAction(e -> showStatusDialog(sensor));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: rgba(239,68,68,0.2); -fx-text-fill: #fca5a5; -fx-background-radius: 8; -fx-padding: 8 15;");
        deleteBtn.setOnAction(e -> {
            sensors.remove(sensor);
            updateContent();
        });

        buttonBox.getChildren().addAll(statusBtn, deleteBtn);

        card.getChildren().addAll(header, nameLabel, typeLabel, valueBox, buttonBox);
        return card;
    }

    private void showAddSensorDialog() {
        Dialog<Sensor> dialog = new Dialog<>();
        dialog.setTitle("Add New Sensor");
        dialog.setHeaderText("Enter sensor details");

        ButtonType addButton = new ButtonType("Add Sensor", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Living Room Temp");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Temperature", "Humidity", "Air Quality");
        typeBox.setValue("Temperature");

        ComboBox<String> statusBox = new ComboBox<>();
            statusBox.getItems().addAll("Functional", "Maintenance");
        statusBox.setValue("Active");

        grid.add(new Label("Sensor Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeBox, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == addButton) {
                String name = nameField.getText().trim();
                if(name.isEmpty()) {
                    showAlert("Please fill in the fields" , Alert.AlertType.WARNING);
                    return null;
                }
                return new Sensor(nameField.getText(), typeBox.getValue(), statusBox.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(sensor -> {
            try{
                if(sensor.getType().equals("Temperature")) {
                    TemperatureSensor newTmpSensor = coordinator.getDeviceService().saveTemperatureSensor(sensor.getName() , sensor.getStatus().toUpperCase());
                    coordinator.getTemperatureManager().addSensor(new TemperatureSensorRunner(newTmpSensor , MqttClientProvider.getMqttClient()));
                }
                sensors.add(sensor);
                updateContent();
            }catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Internal Server Error" , Alert.AlertType.ERROR);
            }


        });
    }

    private void showStatusDialog(Sensor sensor) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(sensor.getStatus(), "Active", "Inactive", "Warning");
        dialog.setTitle("Change Status");
        dialog.setHeaderText("Select new status for " + sensor.getName());

        dialog.showAndWait().ifPresent(status -> {
            sensor.setStatus(status);
            updateContent();
        });
    }

    private void exportCSV() {
        if (sensors.isEmpty()) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Export CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName("sensors.csv");

        File file = fc.showSaveDialog(stage);
        if (file != null) {
            try (PrintWriter pw = new PrintWriter(file)) {
                pw.println("Name,Type,Value,Status");
                for (Sensor s : sensors) {
                    pw.printf("%s,%s,%.2f,%s%n", s.getName(), s.getType(), s.getValue(), s.getStatus());
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Data exported successfully!");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Export failed: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

//    private void startAutoUpdate() {
//        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
//            for (Sensor sensor : sensors) {
//                sensor.updateValue();
//            }
//            if (selectedView.equals("Dashboard")) {
//                updateContent();
//            } else if (!sensors.isEmpty()) {
//                updateContent();
//            }
//        }));
//        updateTimeline.setCycleCount(Timeline.INDEFINITE);
//        updateTimeline.play();
//    }

    private String getUnit(String type) {
        switch (type) {
            case "Temperature": return "°C";
            case "Humidity": return "%";
            case "Air Quality": return "AQI";
            default: return "";
        }
    }

    private String getStatusColor(String status) {
        switch (status) {
            case "Active": return "#10b981";
            case "Warning": return "#f59e0b";
            case "Inactive": return "#6b7280";
            default: return "#6b7280";
        }
    }

    public static class Sensor {
        private String name;
        private String type;
        private double value;
        private String status;

        public Sensor(String name, String type, String status) {
            this.name = name;
            this.type = type;
            this.status = status;
        }



        public String getName() { return name; }
        public String getType() { return type; }
        public double getValue() { return value; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }


    public static void launchApp(DeviceSensorCoordinator c, String[] args) {
        coordinator = c;
        launch(args);
    }
}