package org.curvedrawer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.io.IOException;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.curvedrawer.controller.SettingController;

public class Main extends Application {

	public static final String IP_ADDRESS = "10.0.0.24"; //IP address to connect to when in server mode
	public static final String NETWORK_TABLE_TABLE_KEY = "SmartDashboard"; //network table to send the data to
	public static final int NUMBER_OF_STEPS = 200; // default resolution of paths
	public static final ObservableDoubleValue ZOOM_FACTOR = new SimpleDoubleProperty(
		0.1); // how much scaling changes by
	public static final ObservableDoubleValue SCALE_FACTOR = new SimpleDoubleProperty(
		100.0); // 10 px == 1 m
	private static final boolean IS_CLIENT = true; // if the program will send to robotRIO or not
	private static final int TEAM_NUMBER = 2974; // team number
	public static NetworkTable networkTable;

	static {
//        SettingController.addNumber("NUMBER_OF_STEPS", 50, NumberType.INTEGER);
//        SettingController.addString("IP_ADDRESS", 100.0);
//        SettingController.addString("NETWORK_TABLE_KEY", 100.0);
//        SettingController.addNumber("SCALE_FACTOR", 100.0, NumberType.DOUBLE);
//        SettingController.addNumber("ZOOM_FACTOR", 0.1, NumberType.DOUBLE);
//        SettingController.addBoolean("IS_CLIENT", IS_CLIENT);

//        SettingController.addSpecialSetting(new SimpleValue<Integer>("Hello", 3) {
//            @Override
//            protected Integer handleTextChange(KeyEvent keyEvent) {
//                return null; //TODO
//            }
//
//        });

	}

	private final TabPane tabPane = new TabPane();

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Initializes the network table with ip address settings etc...
	 */
	private static void initNetworkTable() {
//        SettingController.addNumber("TEAM_NUMBER", 2974, NumberType.INTEGER);
//        SettingController.<Integer>getValue("TEAM_NUMBER").addListener((observable, oldValue, newValue) -> {
//            if (!newValue.equals(oldValue))
//            {
//                NetworkTable.setTeam(newValue);
//            }
//        });

		if (IS_CLIENT) {
			NetworkTable.setClientMode();
			NetworkTable.setTeam(TEAM_NUMBER);
		} else {
			NetworkTable.setServerMode();
			NetworkTable.setIPAddress(IP_ADDRESS);
		}

		networkTable = NetworkTable.getTable(NETWORK_TABLE_TABLE_KEY);
	}

	@Override
	public final void start(Stage primaryStage) throws IOException {
		initNetworkTable();

		//////////////////////  TAB PANE SETUP   //////////////////////////////

		tabPane.setMaxSize(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		tabPane.setMinSize(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		tabPane.setPrefSize(640.0, 480.0);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		tabPane.setOnKeyPressed(event ->
		{
			if (event.isControlDown() && (event.getCode() == KeyCode.S)) {
				System.out.println("SAVED"); //TODO make it save the current progress
			}
		});

		ContextMenu contextMenu = new ContextMenu();

		MenuItem setting = new MenuItem("Settings");
		setting.setOnAction(e -> SettingController.showSettings());

		contextMenu.getItems().add(setting);

		tabPane.setContextMenu(contextMenu);

		////////////////////////////////////////////////////////////////////////////

		addTab(createDrawingTab("Curve Drawer")); //adds a pane to the TabPane

		primaryStage.setTitle("Bezier Curve Creator"); //sets title
		primaryStage.setScene(new Scene(tabPane));
		primaryStage.getScene().getStylesheets().add("/assets/css/stylesheet.css");

		primaryStage.show();

		primaryStage.setOnCloseRequest(event -> NetworkTable.shutdown());
	}

	/**
	 * Adds a Tab to the TabPane and sets the tab closing policy (if > 1 tab then you can close tabs
	 * otherwise no)
	 *
	 * @param tab tab to add to tab pane
	 */
	private void addTab(Tab tab) {
		tabPane.getTabs().add(tab);

		if (tabPane.getTabs().size() > 1) {
			tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		}
	}

	/**
	 * Creates and sets up a tab that will be used for drawing curves
	 *
	 * @param name the name of the tab to be displayed
	 * @return the configured tab
	 * @throws IOException if it cannot find a the "curve_drawing_tab.fxml" file
	 */
	private Tab createDrawingTab(String name) throws IOException {
		Parent tabContentController = FXMLLoader.load(
			getClass().getResource("/assets/fxml/curve_drawing_tab.fxml")); //TODO improve upon this

		Tab tab = new Tab(name);
		tab.setContent(tabContentController);
		tab.setOnClosed(event -> {
			if (tabPane.getTabs().size() <= 1) {
				tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
			}
		});

		return tab;
	}

	@Override
	public String toString() {
		return "Main{" +
			"tabPane=" + tabPane +
			'}';
	}
}
