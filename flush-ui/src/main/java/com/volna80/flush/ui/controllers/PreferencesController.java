package com.volna80.flush.ui.controllers;

import com.google.inject.Inject;
import com.volna80.flush.ui.ISubscriptionService;
import com.volna80.flush.ui.Preferences;
import com.volna80.flush.ui.model.events.PreferenceEvent;
import com.volna80.flush.ui.util.ResourceUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class PreferencesController implements Initializable {

    public static final String EN = "en";
    public static final String RU = "ru";
    private static Logger logger = LoggerFactory.getLogger(PreferencesController.class);
    @Inject
    ISubscriptionService subscriptionService;

    @Inject
    Stage stage;

    @FXML
    GridPane eventTypes;
    @FXML
    GridPane countries;
    @FXML
    ChoiceBox<LocaleItem> locale;

    @FXML
    TextField betSize1;
    @FXML
    TextField betSize2;
    @FXML
    TextField betSize3;

    Map<String, CheckBox> countryByCode = new HashMap<>();
    Map<String, CheckBox> eventTypesByCode = new HashMap<>();

    private static void initGridPane(ResourceBundle resources, List<String> userValues, List<String> allValues, Map<String, CheckBox> map, GridPane gridPane, String prefix) {
        int i = 0;

        allValues = allValues.stream().sorted((v1, v2) -> resources.getString(prefix + v1).compareTo(resources.getString(prefix + v2))).collect(Collectors.toList());

        for (String value : allValues) {

            StackPane p1 = new StackPane();
            p1.setAlignment(Pos.CENTER_LEFT);
            p1.setPadding(new Insets(5));
            CheckBox cb = new CheckBox(resources.getString(prefix + value));
            p1.getChildren().add(cb);
            if (userValues.contains(value)) {
                cb.selectedProperty().setValue(true);
            }
            gridPane.add(p1, i % 2, i / 2);
            map.put(value, cb);
            i++;
        }
    }

    @Override
    public void initialize(URL location, final ResourceBundle resources) {
        logger.debug("initialize() {}, {}", location, resources);

        LocaleItem en = new LocaleItem("English", EN);
        LocaleItem ru = new LocaleItem("Русский", RU);

        locale.getItems().addAll(en, ru);


        switch (Preferences.getLocale().getLanguage()) {
            case EN:
                locale.getSelectionModel().select(en);
                break;
            case RU:
                locale.getSelectionModel().select(ru);
                break;
            default:
                logger.error("unsupported locale " + Preferences.getLocale());
                locale.getSelectionModel().select(en);
        }

        betSize1.setText(Preferences.getBetSize1() + "");
        betSize2.setText(Preferences.getBetSize2() + "");
        betSize3.setText(Preferences.getBetSize3() + "");

        {
            final List<String> usersCountries = Preferences.getCountries();
            final List<String> allCountries = ResourceUtil.getCountries(resources);


            initGridPane(resources, usersCountries, allCountries, countryByCode, countries, "country.");
        }

        {
            final List<String> usersEventTypes = Preferences.getEventTypes();
            final List<String> allEventTypes = ResourceUtil.getEventTypes(resources);

            initGridPane(resources, usersEventTypes, allEventTypes, eventTypesByCode, eventTypes, "event-type.");

        }

    }

    public void onSave(ActionEvent event) {
        logger.debug("onSave");

        Preferences.saveLocale(locale.getSelectionModel().getSelectedItem().code);

        Preferences.saveCountries(countryByCode.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(entry -> entry.getKey())
                .collect(Collectors.toList()));

        Preferences.saveEventTypes(eventTypesByCode.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));

        try {
            Preferences.setBetSize1(Integer.valueOf(betSize1.getText()));
        } catch (NumberFormatException e) {
            //do nothing
        }
        try {
            Preferences.setBetSize2(Integer.valueOf(betSize2.getText()));
        } catch (NumberFormatException e) {
            //do nothing
        }
        try {
            Preferences.setBetSize3(Integer.valueOf(betSize3.getText()));
        } catch (NumberFormatException e) {
            //do nothing
        }


        subscriptionService.post(new PreferenceEvent());
        stage.hide();

    }

    public void onCancel(ActionEvent event) {
        stage.hide();
    }

    private static class LocaleItem {
        final String name;
        final String code;

        public LocaleItem(String name, String code) {
            this.name = name;
            this.code = code;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
