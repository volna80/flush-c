package com.volna80.flush.ui;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.volna80.betfair.api.model.Ssoid;
import com.volna80.flush.ui.controllers.IController;
import com.volna80.flush.ui.controllers.LogonController;
import com.volna80.flush.ui.controllers.MarketOverviewController;
import com.volna80.flush.ui.server.FlushAPI;
import com.volna80.flush.ui.server.FlushClient;
import com.volna80.flush.ui.server.IOrdersController;
import com.volna80.flush.ui.windows.BuySellWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ApplicationManager implements IApplicationManager {

    private static final Logger log = LoggerFactory.getLogger(ApplicationManager.class);


    private static IApplicationManager instance;
    private final ISubscriptionService subscribeService = new SubscriptionService();
    //from application
    private final Stage logon = new Stage();
    private final Stage mainMenu = new Stage();
    private final Stage instrumentViewer = new Stage();
    private final Stage preferences = new Stage();
    private final Stage blotter = new Stage();
    private final Stage exit = new Stage();
    private final Stage marketoverview = new Stage();
    //akka client side
    private volatile FlushClient client;
    private volatile FlushAPI api;
    private LogonController logonController;

    public ApplicationManager() {
    }

    public static IApplicationManager getInstance() {
        return instance;
    }

    public static void setInstance(IApplicationManager manager) {
        instance = manager;
    }

    public static void init() {
        instance = new ApplicationManager();
    }

    @Override
    public void start() throws IOException {

        final ResourceBundle resourceBundle = ResourceBundle.getBundle(Constants.BUNDLES_FLUSH);

        loadScene(exit, "/flush/exit-confirm.fxml", new DefaultModule(null, null, null));
        exit.setResizable(false);

        FXMLLoader loader = new FXMLLoader(ApplicationManager.class.getResource("/flush/logon.fxml"));
        loader.load();
        logonController = loader.getController();

        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Constants.FLUSH_CSS);

        logon.setResizable(true);
        logon.setTitle(resourceBundle.getString("logon.title"));
        logon.setScene(scene);
        logon.setOnCloseRequest(event -> {
            event.consume();
            if (!exit.isShowing()) {
                exit.showAndWait();
            }
        });
        logon.showAndWait();


        //====================================================
        // Initialize all core windows

        Preferences.init(resourceBundle);

        mainMenu.setTitle(resourceBundle.getString("main-menu.title"));
        mainMenu.setResizable(false);
        mainMenu.setOnCloseRequest(event -> {
            event.consume();
            if (exit.isShowing()) {
                exit.showAndWait();
            }
        });

        instrumentViewer.setTitle(resourceBundle.getString("instrument-viewer.title"));

        preferences.setTitle(resourceBundle.getString("preferences.title"));
        loadScene(preferences, "/flush/preferences.fxml", new DefaultModule(api.getDirectOrdersController(), api, subscribeService));


        blotter.setTitle(resourceBundle.getString("blotter.title"));
        loadScene(blotter, "/flush/blotter.fxml", new DefaultModule(api.getDirectOrdersController(), api, subscribeService));

        loadScene(mainMenu, "/flush/main-menu.fxml", new DefaultModule(api.getDirectOrdersController(), api, subscribeService));
        loadScene(instrumentViewer, "/flush/instrument-viewer.fxml", new DefaultModule(api.getDirectOrdersController(), api, subscribeService));

        //====================================================

        mainMenu.show();
    }

    @Override
    public void logonPassed(Ssoid ssoid) {

        this.api = FlushAPI.make(ssoid);

        this.client = new FlushClient(ssoid, api.getDictionary());
        this.client.start();

    }

    @Override
    public void showInstrumentViewer() {
        instrumentViewer.show();
    }

    @Override
    public void closeLogon() {
        logon.hide();
        logonController.close();
    }

    //TODO add the reason
    @Override
    public void exit(String reason, boolean isError) {

        if (api != null) {
            api.stop();
        }
        if (client != null) {
            client.stop();
        }

        if (isError) {
            log.error("The application is exiting. Reason: " + reason);
            System.err.println("The application is exiting. Reason: " + reason);
            //TODO open exit window
            System.exit(1);
        } else {
            log.info("The application is exiting. Reason: " + reason);
            System.exit(0);
        }

    }

    @Override
    public void showPreferences() {
        preferences.show();
    }

    @Override
    public void registerOrderController(IOrdersController ordersController) {
        api.setOrderController(ordersController);
    }

    @Override
    public void showMarketOverview() {
        Stage marketoverview = new Stage();
        MarketOverviewController controller = (MarketOverviewController) loadScene(marketoverview, "/flush/market-overview.fxml", new DefaultModule(api.getDirectOrdersController(), api, subscribeService));
        marketoverview.setOnCloseRequest(event -> {
            if (controller != null) controller.close();
        });
        marketoverview.show();
    }

    private Object loadScene(Stage stage, String resource, AbstractModule module) {

        Injector injector = Guice.createInjector(module);
        Object controller = null;


        Parent root;
        try {

            FXMLLoader loader = new FXMLLoader(ApplicationManager.class.getResource(resource));
            //The new part. Give fxmlLoader a callback. Controllers will now be instatiated via the container, not FXMLLoader itself.
            loader.setControllerFactory(injector::getInstance);
            loader.setResources(ResourceBundle.getBundle(Constants.BUNDLES_FLUSH));
            loader.load();

            controller = loader.getController();
            subscribeService.register(controller);

            if (controller instanceof IController) {
                ((IController) controller).setStage(stage);
                ((IController) controller).init();
            }

            root = loader.getRoot();

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }


        Scene scene = new Scene(root);
        scene.getStylesheets().add(Constants.FLUSH_CSS);
        stage.setScene(scene);

        return controller;
    }

    @Override
    public void openBuySell(final String market, final long selectionId) {
        if (api.getOrdersController() != null && api.getOrdersController().isActive()) {
            BuySellWindow buySell = new BuySellWindow(market, selectionId, api.getOrdersController(), api);
            buySell.show();
        } else {
            log.warn("There is no active remote order controller's. Please, wait ...");
            //TODO show an alert window
        }
    }

    @Override
    public void showBlotter() {
        blotter.show();
    }

    public class UTF8Control extends ResourceBundle.Control {
        public ResourceBundle newBundle
                (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            // The below is a copy of the default implementation.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }
}
