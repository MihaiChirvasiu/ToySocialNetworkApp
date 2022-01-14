package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.*;
import com.example.toysocialnetwork.Events.EntityChangeEvent;
import com.example.toysocialnetwork.Events.Event;
import com.example.toysocialnetwork.Observer.Observer;
import com.example.toysocialnetwork.Repository.RepoException;
import com.example.toysocialnetwork.Service.Controller;
import com.example.toysocialnetwork.Utils.Months;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.invoke.CallSite;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ControllerEvent extends Thread implements Observer<EntityChangeEvent>  {

    @FXML
    private TableView<PublicEvent> tableViewAllEvents;

    @FXML
    private TableView<PublicEvent> tableViewEventsSubscribed;

    @FXML
    private Button backButton;

    @FXML
    private Button notificationsButton;

    @FXML
    private Button subscribeButton;

    @FXML
    private Button unsubscribeButton;

    @FXML
    private Button saveButton;

    @FXML
    private TableColumn<PublicEvent, String> tableColumnNameAllEvents;

    @FXML
    private TableColumn<PublicEvent, String> tableColumnDateAllEvents;

    @FXML
    private TableColumn<PublicEvent, String> tableColumnNameSubscribed;

    @FXML
    private TableColumn<PublicEvent, String> tableColumnDateSubscribed;

    @FXML
    private TextField nameEventTextField;

    @FXML
    private ComboBox<String> comboBoxYear;

    @FXML
    private ComboBox<String> comboBoxMonth;

    @FXML
    private ComboBox<String> comboBoxDay;

    @FXML
    private ComboBox<String> comboBoxHour;

    @FXML
    private ComboBox<String> comboBoxMinute;


    Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent, GroupChat> controller;
    ObservableList<PublicEvent> model = FXCollections.observableArrayList();
    ObservableList<PublicEvent> modelSubscribed = FXCollections.observableArrayList();
    Stage detailStage;
    Scene mainScene;
    User friend;
    String selectedMonth;
    String timeRemaining;
    ImageView imageView;

    public void setService(Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent, GroupChat> controller, Stage stage, User user) throws SQLException {
        this.controller = controller;
        controller.addObserver(this);
        this.detailStage = stage;
        this.friend = user;
        initModel();
        loadCombos();
        this.mainScene = detailStage.getScene();
    }

    /**
     * Initialises the models
     * @throws SQLException database
     */
    public void initModel() throws SQLException {
        List<PublicEvent> publicEvents = controller.getAllEvents();
        model.setAll(publicEvents);
        List<PublicEvent> subscribedEvents = controller.getSubscribedEventsForUser(friend.getId());
        if(controller.getSubscribedEventsForUser(friend.getId())!=null)
            modelSubscribed.setAll(subscribedEvents);
    }

    /**
     * Initialises the tableViews
     */
    @FXML
    public void initialize(){
        tableColumnNameAllEvents.setCellValueFactory(new PropertyValueFactory<PublicEvent, String>("nameEvent"));
        tableColumnDateAllEvents.setCellValueFactory(new PropertyValueFactory<PublicEvent, String>("eventDate"));
        tableViewAllEvents.setItems(model);
        tableColumnNameSubscribed.setCellValueFactory(new PropertyValueFactory<PublicEvent, String>("nameEvent"));
        tableColumnDateSubscribed.setCellValueFactory(new PropertyValueFactory<PublicEvent, String>("eventDate"));
        tableViewEventsSubscribed.setItems(modelSubscribed);
        Image image = new Image("File:/E:/MAP/ReparareToySocialNetwork/src/main/resources/com/example/toysocialnetwork/Sources/NotificationBell.jpeg");
        ImageView imageView2 = new ImageView(image);
        imageView2.setFitHeight(34);
        imageView2.setFitWidth(36);
        imageView=imageView2;
        notificationsButton.setGraphic(imageView);
    }

    /**
     * Initialises the comboBoxes with the dates
     */
    public void loadCombos() {
        ObservableList<String> years = FXCollections.observableArrayList();
        for (int i = LocalDateTime.now().getYear(); i < 2049; i++) {
            Integer year = i;
            years.add(year.toString());
        }
        comboBoxYear.setItems(years);

        ObservableList<String> hours = FXCollections.observableArrayList();
        for(int i = 0; i < 24; i++){
            Integer hour = i;
            hours.add(hour.toString());
        }
        comboBoxHour.setItems(hours);

        ObservableList<String> minutes = FXCollections.observableArrayList();
        for(int i = 0; i < 60; i++){
            Integer minute = i;
            minutes.add(minute.toString());
        }
        comboBoxMinute.setItems(minutes);
    }

    /**
     * Initialises the combo with the days
     */
    public void setDays(){
        String year = comboBoxYear.getSelectionModel().getSelectedItem();
        selectedMonth = comboBoxMonth.getSelectionModel().getSelectedItem();
        Map<String, String> monthDay = new HashMap<>();
        monthDay.put(Months.JANUARY.toString(), "31");
        monthDay.put(Months.FEBRUARY.toString(), "28");
        monthDay.put(Months.MARCH.toString(), "31");
        monthDay.put(Months.APRIL.toString(), "30");
        monthDay.put(Months.MAY.toString(), "31");
        monthDay.put(Months.JUNE.toString(), "30");
        monthDay.put(Months.JULY.toString(), "31");
        monthDay.put(Months.AUGUST.toString(), "31");
        monthDay.put(Months.SEPTEMBER.toString(), "30");
        monthDay.put(Months.OCTOBER.toString(), "31");
        monthDay.put(Months.NOVEMBER.toString(), "30");
        monthDay.put(Months.DECEMBER.toString(), "31");
        if(Integer.parseInt(year) % 4 == 0)
            monthDay.put(Months.FEBRUARY.toString(), "29");
        ObservableList<String> days = FXCollections.observableArrayList();
        for(int i = 1; i <= Integer.parseInt(monthDay.get(selectedMonth)); i++){
            days.add(String.valueOf(i));
        }

        comboBoxDay.setItems(days);

    }

    /**
     * Handler for subscribing to an event
     * @throws SQLException database
     */
    public void subscribe() throws SQLException {
        try{
            PublicEvent event = tableViewAllEvents.getSelectionModel().getSelectedItem();
            if (event == null)
                MessageAlert.showErrorMessage(null, "No selected event!");
            else
                controller.subscribeToEventServ(event.getId(), friend.getId());
        }catch (RepoException e)
        {
            MessageAlert.showErrorMessage(null,"Already subscribed to this event!");
        }
    }

    /**
     * Handler for unsubscribing from an event
     * @throws SQLException database
     */
    public void unsubscribe() throws SQLException {
        PublicEvent event = tableViewEventsSubscribed.getSelectionModel().getSelectedItem();
        if(event == null)
            MessageAlert.showErrorMessage(null,"No selected event!");
        else
            controller.unsubscribeFromEventServ(event.getId(), friend.getId());
    }

    /**
     * Initialises the months comboBox with the months
     */
    public void setMonths(){
        //String year = comboBoxYear.getSelectionModel().getSelectedItem();
        if(comboBoxMonth.getItems().isEmpty()) {
            ObservableList<String> months = FXCollections.observableArrayList();
            for (int i = 0; i < Months.values().length; i++) {
                months.add(Months.values()[i].toString());
            }
            comboBoxMonth.setItems(months);
        }
        else {
            comboBoxDay.setItems(null);
            setDays();
        }
    }

    /**
     * The thread run function that verifies the time remaining until the events
     */
    @Override
    public void run() {
        try {
            List<PublicEvent> eventList = controller.getSubscribedEventsForUserOrdered(friend.getId());
            if(eventList != null) {
                for(int i = 0; i < eventList.size(); i++) {
                    LocalDateTime data1 = eventList.get(i).getEventDate();
                    LocalDateTime data2 = LocalDateTime.now();
                    long days = ChronoUnit.DAYS.between(data2, data1);
                    long minutesD = ChronoUnit.MINUTES.between(data2, data1);
                    if (minutesD > 0) {
                        if (days > 366) {
                            Image image = new Image("File:/E:/MAP/ReparareToySocialNetwork/src/main/resources/com/example/toysocialnetwork/Sources/NotificationBellOverYear.jpeg");
                            ImageView imageView2 = new ImageView(image);
                            imageView2.setFitHeight(34);
                            imageView2.setFitWidth(36);
                            imageView=imageView2;
                        }
                        if (days <= 366) {
                            if (days <= 31) {
                                Image image = new Image("File:/E:/MAP/ReparareToySocialNetwork/src/main/resources/com/example/toysocialnetwork/Sources/NotificationBellLessThanMonth.jpeg");
                                ImageView imageView2 = new ImageView(image);
                                imageView2.setFitHeight(34);
                                imageView2.setFitWidth(36);
                                imageView=imageView2;
                            }
                            if (days <= 1) {
                                {
                                    Image image = new Image("File:/E:/MAP/ReparareToySocialNetwork/src/main/resources/com/example/toysocialnetwork/Sources/NotificationBellLessThanDay.jpeg");
                                    ImageView imageView2 = new ImageView(image);
                                    imageView2.setFitHeight(34);
                                    imageView2.setFitWidth(36);
                                    imageView=imageView2;
                                }
                            }
                            long minutes = ChronoUnit.MINUTES.between(data2, data1);
                            if (minutes < 60) {
                                Image image = new Image("File:/E:/MAP/ReparareToySocialNetwork/src/main/resources/com/example/toysocialnetwork/Sources/NotificationBellLessThanHour.jpeg");
                                ImageView imageView2 = new ImageView(image);
                                imageView2.setFitHeight(34);
                                imageView2.setFitWidth(36);
                                imageView=imageView2;
                            }
                        }
                    }
                    else
                    {
                        Image image = new Image("File:/E:/MAP/ReparareToySocialNetwork/src/main/resources/com/example/toysocialnetwork/Sources/NotificationBell.jpeg");
                        ImageView imageView2 = new ImageView(image);
                        imageView2.setFitHeight(34);
                        imageView2.setFitWidth(36);
                        imageView=imageView2;

                    }
                }
            }
            else
            {
                Image image = new Image("File:/E:/MAP/ReparareToySocialNetwork/src/main/resources/com/example/toysocialnetwork/Sources/NotificationBell.jpeg");
                ImageView imageView2 = new ImageView(image);
                imageView2.setFitHeight(34);
                imageView2.setFitWidth(36);
                imageView=imageView2;
            }
            notificationsButton.setGraphic(imageView);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Collects all the data from the combos and save the events
     * @throws SQLException database
     */
    public void saveEvent() throws SQLException {
        if(comboBoxYear.getSelectionModel().getSelectedItem() == null || comboBoxMonth.getSelectionModel().getSelectedItem() == null ||
                comboBoxDay.getSelectionModel().getSelectedItem() == null || comboBoxHour.getSelectionModel().getSelectedItem() == null ||
                comboBoxMinute.getSelectionModel().getSelectedItem() == null || nameEventTextField.getText() == "")
            MessageAlert.showErrorMessage(null,"Incomplete details!");
        else
        {
            Map<String, String> monthInt = new HashMap<>();
            monthInt.put(Months.JANUARY.toString(), "01");
            monthInt.put(Months.FEBRUARY.toString(), "02");
            monthInt.put(Months.MARCH.toString(), "03");
            monthInt.put(Months.APRIL.toString(), "04");
            monthInt.put(Months.MAY.toString(), "05");
            monthInt.put(Months.JUNE.toString(), "06");
            monthInt.put(Months.JULY.toString(), "07");
            monthInt.put(Months.AUGUST.toString(), "08");
            monthInt.put(Months.SEPTEMBER.toString(), "09");
            monthInt.put(Months.OCTOBER.toString(), "10");
            monthInt.put(Months.NOVEMBER.toString(), "11");
            monthInt.put(Months.DECEMBER.toString(), "12");
            String day = comboBoxDay.getSelectionModel().getSelectedItem();
            Integer actualDay = Integer.parseInt(day);
            String goodDay;
            if(actualDay<10)
                goodDay = "0" + day;
            else
                goodDay = day;
            String hour = comboBoxHour.getSelectionModel().getSelectedItem();
            Integer actualHour = Integer.parseInt(hour);
            String goodHour;
            if(actualHour<10)
                goodHour = "0" + hour;
            else
                goodHour = hour;
            String minute = comboBoxMinute.getSelectionModel().getSelectedItem();
            Integer actualMinute = Integer.parseInt(minute);
            String goodMinute;
            if(actualMinute<10)
                goodMinute = "0" + minute;
            else
                goodMinute = minute;
            String date = comboBoxYear.getSelectionModel().getSelectedItem() + "-" + monthInt.get(comboBoxMonth.getSelectionModel().getSelectedItem())
                    + "-" + goodDay + "T" + goodHour
                    + ":" + goodMinute + ":00.0";
            controller.addEventServ(nameEventTextField.getText(),date);
        }
    }

    /**
     * Button handler for going back a page
     * @throws IOException file
     * @throws SQLException database
     */
    @FXML
    public void goBack() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("friendrequests-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        detailStage.setTitle("FriendRequests");

        Scene scene = new Scene(root);
        detailStage.setScene(scene);

        ControllerDetails controllerDetails = loader.getController();
        controllerDetails.setService(controller, detailStage, this.friend);
    }

    /**
     * We need to update the models(observer is notified)
     * @param entityChangeEvent the event that occurred
     * @throws SQLException database
     * @throws IOException file
     */
    @Override
    public void update(EntityChangeEvent entityChangeEvent) throws SQLException, IOException {
        initModel();
    }

    /**
     * The event notification window
     * @throws SQLException database
     * @throws IOException file
     */
    public void buildChatBox() throws SQLException, IOException {
        if (tableViewEventsSubscribed != null) {
            Button backToPublicChat = new Button("<");
            backToPublicChat.setId("backToPublicChat");
            backToPublicChat.setVisible(true);
            backToPublicChat.setOnAction(event -> {
                detailStage.setScene(mainScene);
                detailStage.show();
            });

            VBox vBox = new VBox();
            vBox.setId("Notifications");
            vBox.setPrefWidth(770);
            vBox.setPrefHeight(200);

            vBox.setVisible(true);

            Text eventWindowInfo = new Text("Event notifications");
            eventWindowInfo.setId("eventWindowInfo");


            List<PublicEvent> eventList = controller.getSubscribedEventsForUser(friend.getId());
            for (int i = 0; i < eventList.size(); i++) {
                LocalDateTime data1 = eventList.get(i).getEventDate();
                LocalDateTime data2 = LocalDateTime.now();
                Long days = ChronoUnit.DAYS.between(data2, data1);
                Long minutesD = ChronoUnit.MINUTES.between(data2, data1);
                String timeRemainingActual = "";
                if(minutesD > 0) {
                    if (days > 366)
                        timeRemainingActual = "over a year";
                    if (days <= 366) {
                        Long months = ChronoUnit.MONTHS.between(data2, data1);
                        timeRemainingActual = months + " months remaining";
                        if (days <= 31)
                            timeRemainingActual = days + " days remaining";
                        if (days <= 1) {
                            Long hours = ChronoUnit.HOURS.between(data2, data1);
                            timeRemainingActual = hours + " hours remaining";
                        }
                        long minutes = ChronoUnit.MINUTES.between(data2, data1);
                        if (minutes < 60)
                            timeRemainingActual = minutes + " minutes remaining";
                    }
                }
                else
                    timeRemainingActual = "Expired";
                    Label label = new Label(eventList.get(i).getNameEvent());
                    label.setWrapText(true);
                    label.setId("name");
                    Label label2 = new Label(eventList.get(i).getEventDate().toString());
                    label.setWrapText(true);
                    label.setId("date");
                    //LocalDateTime data1 = eventList.get(i).getEventDate();
                    //LocalDateTime data2 = LocalDateTime.now();
                    //Long days = ChronoUnit.DAYS.between(data2,data1);
                    Label label3 = new Label(timeRemainingActual);
                    label.setWrapText(true);
                    label.setId("name");
                    HBox hBox = new HBox();
                    hBox.getChildren().add(label);
                    hBox.setSpacing(10);
                    hBox.getChildren().add(label2);
                    hBox.setSpacing(10);
                    hBox.getChildren().add(label3);
                    hBox.setAlignment(Pos.BASELINE_LEFT);
                    vBox.getChildren().add(hBox);
                    vBox.setSpacing(10);
                    hBox.setVisible(true);

                timeRemaining = timeRemainingActual;
            }

                ScrollPane scrlPane = new ScrollPane(vBox);
                scrlPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrlPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scrlPane.setId("scrolPane");
                scrlPane.setPrefHeight(400);
                scrlPane.setPrefWidth(785);
                VBox eventBox = new VBox(new HBox(backToPublicChat, eventWindowInfo), scrlPane);
                eventBox.setId("eventBox");
                Scene scene = new Scene(eventBox, 785, 400);
                detailStage.setScene(scene);
                detailStage.show();

        }

    }
}
