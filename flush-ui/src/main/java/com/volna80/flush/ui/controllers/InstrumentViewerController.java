package com.volna80.flush.ui.controllers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.volna80.betfair.api.model.*;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.Preferences;
import com.volna80.flush.ui.model.events.PreferenceEvent;
import com.volna80.flush.ui.server.IFlushAPI;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.volna80.flush.ui.server.IFlushAPI.executor;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class InstrumentViewerController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(InstrumentViewerController.class);

    @FXML
    public ChoiceBox<EventType> eventTypes;
    @FXML
    public ChoiceBox<String> countries;
    @FXML
    public ListView<CompetitionResult> competitions;
    @FXML
    public Accordion events;
    @FXML
    public Accordion markets;
    @FXML
    public Button reset;
    @Inject
    public IFlushAPI api;
    private ResourceBundle resourceBundle;
    private Service<List<MarketCatalogue>> loadMarkets;
    private volatile Service<List<CompetitionResult>> loadCompetitions;
    private volatile Service<Set<Event>> loadEvents;

    @Override
    public void initialize(URL url, final ResourceBundle resourceBundle) {

        log.debug("initialize()");
        this.resourceBundle = resourceBundle;

        initChoiceBoxes(resourceBundle);

        eventTypes.setConverter(
                new StringConverter<EventType>() {
                    @Override
                    public String toString(EventType eventType) {
                        return resourceBundle.getString("event-type." + eventType.getId());
                    }

                    @Override
                    public EventType fromString(String s) {
                        throw new UnsupportedOperationException();
                    }
                }
        );

        countries.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String countryCode) {
                return resourceBundle.getString("country." + countryCode);
            }

            @Override
            public String fromString(String s) {
                throw new UnsupportedOperationException();
            }
        });


        competitions.setCellFactory((view) -> new ListCell<CompetitionResult>() {
            @Override
            protected void updateItem(CompetitionResult item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText("");
                else setText(item.getCompetition().getName());
            }
        });


        competitions.getSelectionModel().selectedIndexProperty().addListener(
                (observableValue, number, number2) -> {
                    showEvents();
                }
        );

    }

    private void initChoiceBoxes(ResourceBundle resourceBundle) {
        countries.getItems().clear();
        countries.getItems().addAll(Preferences.getCountries().stream()
                .sorted((v1, v2) -> resourceBundle.getString("country." + v1).compareTo(resourceBundle.getString("country." + v2)))
                .collect(Collectors.toList()));

        eventTypes.getItems().clear();
        eventTypes.getItems().addAll(
                Preferences.getEventTypes().stream()
                        .map(EventType::new)
                        .sorted((v1, v2) -> resourceBundle.getString("event-type." + v1.getId()).compareTo(resourceBundle.getString("event-type." + v2.getId())))
                        .collect(Collectors.toList())
        );

    }

    public void show() {

        final EventType event = eventTypes.getValue();
        final String country = countries.getValue();

        log.debug("show() eventType:{}, country:{}", event, country);

        loadCompetitions(event, country);
    }

    public void showEvents() {
        final EventType event = eventTypes.getValue();
        final String country = countries.getValue();
        final CompetitionResult competition = competitions.getSelectionModel().getSelectedItem();

        log.debug("showEvents() eventType:{}, country:{}", event, country);

        loadEvents(event, country, competition);
    }

    private void showMarkets(final Event event2) {

        if (loadMarkets != null) {
            loadMarkets.cancel();
        }

        final EventType event = eventTypes.getValue();
        final String country = countries.getValue();
        final CompetitionResult competition = competitions.getSelectionModel().getSelectedItem();

        log.debug("showEvents() eventType:{}, country:{}, event:{}", event, country, event2);

        loadMarkets = new Service<List<MarketCatalogue>>() {
            @Override
            protected Task<List<MarketCatalogue>> createTask() {
                return new Task<List<MarketCatalogue>>() {
                    @Override
                    protected List<MarketCatalogue> call() throws Exception {
                        return api.getMarkets(event, country, null, null, competition, event2);
                    }
                };
            }
        };
        loadMarkets.setExecutor(executor);

        loadMarkets.setOnSucceeded(
                new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        List<MarketCatalogue> result = (List<MarketCatalogue>) workerStateEvent.getSource().getValue();

                        if (result.size() == 0) {
                            return;
                        }

                        List<TitledPane> panes = new ArrayList<>();
                        result.stream()
                                .sorted((m1, m2) -> -Double.compare(m1.getTotalMatched(), m2.getTotalMatched()))
                                .forEachOrdered(market -> {
                                    final ListView<RunnerCatalog> list = new ListView<>();


                                    list.setCellFactory(view -> new ListCell<RunnerCatalog>() {
                                        @Override
                                        protected void updateItem(RunnerCatalog item, boolean empty) {
                                            super.updateItem(item, empty);
                                            if (empty) setText("");
                                            else setText(item.getRunnerName());
                                        }
                                    });


                                    list.setOnMouseClicked(
                                            mouseEvent -> {
                                                if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                                                    openBuySell(market, list.getSelectionModel().getSelectedItem());
                                                }
                                            }
                                    );

                                    RunnerCatalog[] tmp2 = market.getRunners().toArray(new RunnerCatalog[market.getRunners().size()]);
                                    Arrays.sort(tmp2);
                                    list.getItems().addAll(tmp2);


                                    TitledPane pane = new TitledPane(market.getMarketName() + " (" + Math.round(market.getTotalMatched()) + ")", list);
                                    panes.add(pane);
                                    pane.setMinHeight(100);
                                    pane.setPrefHeight(100);
                                });


                        markets.getPanes().clear();
                        markets.getPanes().addAll(panes);


                    }
                }
        );

        loadMarkets.setOnFailed(
                workerStateEvent -> log.error("couldn't load markets [" + event2 + "]", workerStateEvent.getSource().getException())
        );

        loadMarkets.start();

    }

    private void openBuySell(MarketCatalogue market, RunnerCatalog runner) {
        ApplicationManager.getInstance().openBuySell(market.getMarketId(), runner.getSelectionId());
    }

    private void loadCompetitions(final EventType event, final String country) {

        if (loadCompetitions != null) {
            loadCompetitions.cancel();
        }

        loadCompetitions = new Service<List<CompetitionResult>>() {
            @Override
            protected Task<List<CompetitionResult>> createTask() {
                return new Task<List<CompetitionResult>>() {
                    @Override
                    protected List<CompetitionResult> call() throws Exception {
                        return api.getCompetitions(event, country, null, null);
                    }
                };
            }
        };
        loadCompetitions.setExecutor(executor);

        loadCompetitions.setOnSucceeded(
                workerStateEvent -> {
                    List<CompetitionResult> list = (List<CompetitionResult>) workerStateEvent.getSource().getValue();
                    competitions.getItems().clear();
                    //sort
                    CompetitionResult[] tmp = list.toArray(new CompetitionResult[list.size()]);
                    Arrays.sort(tmp, (o1, o2) -> o1.getCompetition().getName().compareTo(o2.getCompetition().getName()));
                    competitions.getItems().addAll(Arrays.asList(tmp));
                }
        );

        loadCompetitions.setOnFailed(
                workerStateEvent -> log.error("couldn't load competitions", workerStateEvent.getSource().getException())
        );

        loadCompetitions.start();
    }

    private void loadEvents(final EventType event, final String country, final CompetitionResult competition) {

        if (loadEvents != null) {
            loadEvents.cancel();
        }


        loadEvents = new Service<Set<Event>>() {
            @Override
            protected Task<Set<Event>> createTask() {
                return new Task<Set<Event>>() {
                    @Override
                    protected Set<Event> call() throws Exception {
                        return api.getSoccerEvents(event, country, competition, null, null);
                    }
                };
            }
        };
        loadEvents.setExecutor(executor);

        loadEvents.setOnSucceeded(
                workerStateEvent -> {
                    Set<Event> list = (Set<Event>) workerStateEvent.getSource().getValue();

                    final List<TitledPane> panes = new ArrayList<>();

                    Multimap<LocalDate, Event> map = ArrayListMultimap.create();
                    list.stream().forEach(e -> map.put(e.getOpenDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), e));

                    final LocalDate today = LocalDate.now();
                    final LocalDate tomorrow = today.plusDays(1);

                    map.keySet().stream().sorted().forEachOrdered(d -> {

                        final ListView<Event> l = new ListView<>();


                        l.setCellFactory(view -> new ListCell<Event>() {
                            @Override
                            protected void updateItem(Event item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setText("");
                                } else {
                                    setText(item.getName());
                                }
                                ;
                            }
                        });


                        l.setOnMouseClicked(
                                mouseEvent -> {
                                    showMarkets(l.getSelectionModel().getSelectedItem());
                                }
                        );

                        l.getItems().addAll(map.get(d));

                        TitledPane pane = new TitledPane();
                        String title = d + "";
                        if (d.isEqual(today)) {
                            //TODO locale, may be another css style?
                            title += " (today)";
                            pane.getStyleClass().removeAll();
                        } else if (d.isEqual(tomorrow)) {
                            title += " (tomorrow)";
                            pane.getStyleClass().removeAll();
                        }

                        pane.setText(title);
                        pane.setContent(l);

                        panes.add(pane);
                        pane.setMinHeight(map.get(d).size() * 15);

                    });

                    events.getPanes().clear();
                    events.getPanes().addAll(panes);

                }
        );

        loadEvents.start();
    }

    public void reset() {
        eventTypes.getSelectionModel().clearSelection();
        countries.getSelectionModel().clearSelection();
    }

    @Subscribe
    public void onPreferenceUpdate(PreferenceEvent event) {
        log.info("onPreferenceUpdate");
        initChoiceBoxes(resourceBundle);
    }

}
