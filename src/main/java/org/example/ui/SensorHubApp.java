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
import org.example.enums.DeviceType;
import org.example.enums.Status;
import org.example.model.Device;
import org.example.model.Reading;
import org.example.model.TemperatureSensor;
import org.example.mqtt.MqttClientProvider;
import org.example.runners.TemperatureSensorRunner;
import org.example.util.MqttConnectionListener;

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
    private Button activeNavButton;
    private Label mqttStatusLabel;

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
                sensors.add(new Sensor(tmp.getId() , tmp.getName() , tmp.getType().toString() , tmp.getStatus().toString()));
            });
//            sensors.forEach(this::createSensorCard);

            List<TemperatureSensor> activeTemperatureSensors = temperatureSensorList.stream().filter(sensor -> sensor.getStatus().equals(Status.ACTIVE)).toList();
            coordinator.start(activeTemperatureSensors);

            coordinator.getReadingService().addListener(reading -> {
                Platform.runLater(() -> {
                    updateSensorValueInUI(reading);
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load sensors from DB", Alert.AlertType.ERROR);
        }
    }

    private void setupMqttStatusListener() {
        coordinator.getTemperatureReadingService().setConnectionListener(new MqttConnectionListener() {
            @Override
            public void onConnect() {
                Platform.runLater(() -> {
                    mqttStatusLabel.setText("MQTT Connected");
                    mqttStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                });
            }

            @Override
            public void onDisconnect(Throwable cause) {
                Platform.runLater(() -> {
                    mqttStatusLabel.setText("MQTT Disconnected");
                    mqttStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

                    // Clear all active sensor values
                    for (Sensor s : sensors) {
                        s.setValue("--");
                    }
                    coordinator.getTemperatureManager().stopAll();
                    updateContent();
                });
            }
        });
    }

    private void updateSensorValueInUI(Reading reading) {
        for (Sensor s : sensors) {
            if (s.getName().equalsIgnoreCase(reading.getDevice().getName())) {
                s.value = String.valueOf(reading.getValue());
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

        mqttStatusLabel = new Label("MQTT Disconnected");
        mqttStatusLabel.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        mqttStatusLabel.setStyle("-fx-text-fill: red; -fx-padding: 10;");
        contentArea.getChildren().add(0, mqttStatusLabel);

        setupMqttStatusListener();

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
        setNavButtonActive(dashBtn);

        Button tempBtn = createNavButton("Temperature", "Temperature");
        tempBtn.setCursor(Cursor.HAND);

        Button humBtn = createNavButton("Humidity", "Humidity");
        humBtn.setCursor(Cursor.HAND);

        Button airBtn = createNavButton("Air Quality", "Air Quality");
        airBtn.setCursor(Cursor.HAND);


        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("Export CSV");
        exportBtn.setCursor(Cursor.HAND);
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
        setNavButtonInactive(btn);

        btn.setOnAction(e -> {
            selectedView = view;
            setNavButtonActive(btn);
            updateContent();
        });

        btn.setOnMouseEntered(e -> {
            if (btn != activeNavButton) {
                btn.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.2);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-padding: 12;"
                );
            }
        });

        btn.setOnMouseExited(e -> {
            if (btn != activeNavButton) {
                setNavButtonInactive(btn);
            }
        });

        return btn;
    }

    private void updateContent() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(0, mqttStatusLabel);

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

        int activeCount = (int) sensors.stream().filter(s -> s.getStatus().equals("ACTIVE")).count();
        int maintenanceCount = (int) sensors.stream().filter(s -> s.getStatus().equals("MAINTENANCE")).count();
        int stoppedCount = (int) sensors.stream().filter(s -> s.getStatus().equals("STOPPED")).count();

        statsGrid.add(createStatCard("Total Sensors", String.valueOf(sensors.size()), "#a855f7"), 0, 0);
        statsGrid.add(createStatCard("Active", String.valueOf(activeCount), getStatusColor(Status.ACTIVE.toString())), 1, 0);
        statsGrid.add(createStatCard("Maintenance", String.valueOf(maintenanceCount), getStatusColor(Status.MAINTENANCE.toString())), 2, 0);
        statsGrid.add(createStatCard("Stopped", String.valueOf(stoppedCount), getStatusColor(Status.STOPPED.toString())), 3, 0);

        // Status Bars
        VBox statusBox = new VBox(15);
        statusBox.setPadding(new Insets(20));
        statusBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 15; -fx-border-width: 1;");

        Label statusTitle = new Label("Sensor Status Distribution");
        statusTitle.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        statusTitle.setStyle("-fx-text-fill: white;");

        statusBox.getChildren().add(statusTitle);
        statusBox.getChildren().add(createStatusBar("Active", activeCount, sensors.size(), getStatusColor(Status.ACTIVE.toString())));
        statusBox.getChildren().add(createStatusBar("Maintenance", maintenanceCount, sensors.size() , getStatusColor(Status.MAINTENANCE.toString())));
        statusBox.getChildren().add(createStatusBar("Stopped", stoppedCount, sensors.size(), getStatusColor(Status.STOPPED.toString())));

        // Type Stats
        GridPane typeGrid = new GridPane();
        typeGrid.setHgap(15);
        typeGrid.setVgap(15);

        typeGrid.add(createTypeStatCard("Temperature", "°C", "#ef4444"), 0, 0);
        typeGrid.add(createTypeStatCard("Humidity", "%", "#3b82f6"), 1, 0);
        typeGrid.add(createTypeStatCard("Air_Quality", "AQI", "#10b981"), 2, 0);

        contentArea.getChildren().addAll(title, statsGrid, statusBox, typeGrid);
    }

    private void setNavButtonActive(Button btn) {
        if (activeNavButton != null) {
            setNavButtonInactive(activeNavButton);
        }

        btn.setStyle(
                "-fx-background-color: rgba(168,85,247,0.4);" + // purple highlight
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12;"
        );

        activeNavButton = btn;
    }

    private void setNavButtonInactive(Button btn) {
        btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05);" +
                        "-fx-text-fill: #c4b5fd;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12;"
        );
    }

    private String getFormatedSensorName(String sensor) {
        if(sensor == null) return "Sensor";
        return switch (sensor) {
            case "TEMPERATURE" -> "Temperature";
            case "HUMIDITY" -> "Humidity";
            case "AIR_QUALITY" -> "Air Quality";
            default -> "Sensor";
        };
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

        String typeFormated = DeviceType.valueOf(type.toUpperCase()).toString();

        long count = sensors.stream().filter(s -> s.getStatus().equalsIgnoreCase(Status.ACTIVE.toString())).filter(s -> s.getType().equals(typeFormated)).count();
        Label countLabel = new Label(count + " sensors");
        countLabel.setStyle("-fx-text-fill: #c4b5fd; -fx-font-size: 11;");

        double avg = sensors.stream()
                .filter(s -> s.getStatus().equalsIgnoreCase(Status.ACTIVE.toString()))
                .filter(s -> s.getType().equals(typeFormated))
                .map(Sensor::getValue)
                .filter(v -> v != null && !v.equals("--"))
                .mapToDouble(Double::parseDouble)
                .average()
                .orElse(0);


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

        Label valueLabel;

        try {
            double v = Double.parseDouble(sensor.getValue());
            valueLabel = new Label(String.format("%.1f", v));
        } catch (Exception e) {
            valueLabel = new Label("--");
        }        valueLabel.setFont(Font.font("Inter", FontWeight.BOLD, 40));
        valueLabel.setStyle("-fx-text-fill: white;");

        Label unitLabel = new Label(getUnit(sensor.getType()));
        unitLabel.setStyle("-fx-text-fill: #c4b5fd; -fx-font-size: 16;");

        valueBox.getChildren().addAll(valueLabel, unitLabel);

        HBox buttonBox = new HBox(10);
        Button statusBtn = new Button("Update");
        statusBtn.setCursor(Cursor.HAND);
        statusBtn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 15;");
        statusBtn.setOnAction(e -> showUpdateSensorDialog(sensor));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setCursor(Cursor.HAND);
        deleteBtn.setStyle("-fx-background-color: rgba(239,68,68,0.2); -fx-text-fill: #fca5a5; -fx-background-radius: 5; -fx-padding: 8 15;");
        deleteBtn.setOnAction(e -> {
            try {
                System.out.println("id here : " + sensor.getId());
                coordinator.getDeviceService().deleteTemperatureSensor(sensor.getId());
                sensors.remove(sensor);
                coordinator.getTemperatureManager().removeSensor(sensor.getId());
            } catch (Exception ex) {
                showAlert("Internal server error" , Alert.AlertType.ERROR);
            }
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
            statusBox.getItems().addAll("Active", "Stopped" , "Maintenance");
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
                return new Sensor(null , nameField.getText(), typeBox.getValue(), statusBox.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(sensor -> {
            try{
                if(sensor.getType().equals("Temperature")) {
                    TemperatureSensor newTmpSensor = coordinator.getDeviceService().saveTemperatureSensor(sensor.getName() , sensor.getStatus().toUpperCase());
                    coordinator.getTemperatureManager().addSensor(newTmpSensor);
                    sensors.add(new Sensor(newTmpSensor.getId() , newTmpSensor.getName() , newTmpSensor.getType().toString() , newTmpSensor.getStatus().toString()));
                }
                updateContent();
            }catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Internal Server Error" , Alert.AlertType.ERROR);
            }


        });
    }


    private void showUpdateSensorDialog(Sensor sensor) {

        Dialog<Sensor> dialog = new Dialog<>();
        dialog.setTitle("Update Sensor");
        dialog.setHeaderText("Update sensor details");

        ButtonType addButton = new ButtonType("Update Sensor", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setText(sensor.getName());

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active", "Stopped" , "Maintenance");
        statusBox.setValue(sensor.getStatus());

        grid.add(new Label("Sensor Name:"), 0, 0);
        grid.add(nameField, 1, 0);
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
                return new Sensor(sensor.getId() , nameField.getText(), sensor.getType() , statusBox.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(sen -> {
            try{
                if(sen.getType().equalsIgnoreCase("Temperature")) {
                    coordinator.getDeviceService().updateTemperatureSensor(sen.getId() , sen.getName() , sen.getStatus().toUpperCase());
                    TemperatureSensor updatedTemperatureSensor = coordinator.getDeviceService().getTemperatureSensorById(sen.getId());
                    sensors.removeIf(s -> Objects.equals(s.getId() , sen.getId()));
                    coordinator.getTemperatureManager().removeSensor(sen.getId());
                    if(updatedTemperatureSensor.getStatus().equals(Status.ACTIVE))
                        coordinator.getTemperatureManager().addSensor(updatedTemperatureSensor);
                    sensors.add(new Sensor(updatedTemperatureSensor.getId() , updatedTemperatureSensor.getName()  ,updatedTemperatureSensor.getType().toString() , updatedTemperatureSensor.getStatus().toString()));
                }
                updateContent();
            }catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Internal Server Error" , Alert.AlertType.ERROR);
            }


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
            case "TEMPERATURE": return "°C";
            case "HUMIDITY": return "%";
            case "AIR_QUALITY": return "AQI";
            default: return "";
        }
    }

    private String getStatusColor(String status) {
        if (status == null) return "#9ca3af";

        switch (status.toUpperCase()) {
            case "ACTIVE":
                return "#22c55e";

            case "MAINTENANCE":
                return "#f59e0b";

            case "STOPPED":
                return "#ef4444";

            default:
                return "#9ca3af";
        }
    }


    public static class Sensor {
        private Long id;
        private String name;
        private String type;
        private String value = "--";
        private String status;

        public Sensor(Long id , String name, String type, String status) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.status = status;
        }


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public String getValue() { return value; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }


    public static void launchApp(DeviceSensorCoordinator c, String[] args) {
        coordinator = c;
        launch(args);
    }
}