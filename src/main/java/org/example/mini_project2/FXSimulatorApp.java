package org.example.mini_project2;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class FXSimulatorApp extends Application {

    private PageTableLogic simulator;
    private ObservableList<PageTableEntry> tableData;
    private Label statusLabel;
    private int currentEventIndex = 0;
    private List<PageAccessEvent> simulationEvents;
    private Timeline timeline;
    private TableView<PageTableEntry> tableView;

    @Override
    public void start(Stage primaryStage) {
        // --- 1. Определение входных данных для симуляции ---
        int capacity = 3;
        List<Integer> pagesToRef = Arrays.asList(2, 2, 0, 2, 3, 4, 0, 3, 2, 1);
        List<Boolean> operations = Arrays.asList(false, true, false, true, false, true, false, false, true, false);

        // Создание всех событий доступа
        simulationEvents = new ArrayList<>();
        for (int i = 0; i < pagesToRef.size(); i++) {
            simulationEvents.add(new PageAccessEvent(pagesToRef.get(i), operations.get(i)));
        }

        // Список всех страниц в процессе (для инициализации PTE)
        List<Integer> allPages = pagesToRef.stream().distinct().collect(Collectors.toList());
        allPages.add(1); // Добавим страницу 1, которая появляется только в конце

        // Инициализация логики
        simulator = new PageTableLogic(allPages, capacity);

        // --- 2. Создание GUI элементов ---
        statusLabel = new Label("Симуляция ожидает. Размер кадра: " + capacity);
        statusLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10;");

        tableView = createTableView();

        // Загрузка начальных данных в таблицу
        tableData = FXCollections.observableArrayList(simulator.getPageTable().values());
        tableView.setItems(tableData);

        Button startButton = new Button("Начать Симуляцию");
        startButton.setOnAction(e -> startSimulation(startButton));

        VBox root = new VBox(10, statusLabel, startButton, tableView);
        root.setStyle("-fx-padding: 10;");

        primaryStage.setTitle("Mini-Project: Visual Page Table (LRU)");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    private TableView<PageTableEntry> createTableView() {
        TableView<PageTableEntry> table = new TableView<>();

        // Колонки Page Numbers
        TableColumn<PageTableEntry, Integer> pageCol = new TableColumn<>("Page #");
        pageCol.setCellValueFactory(new PropertyValueFactory<>("pageNumber"));
        pageCol.setPrefWidth(70);

        // Колонки Frame Mappings
        TableColumn<PageTableEntry, Integer> frameCol = new TableColumn<>("Frame #");
        frameCol.setCellValueFactory(new PropertyValueFactory<>("frameNumber"));
        frameCol.setCellFactory(column -> new FrameNumberCell()); // Кастомный рендеринг -1
        pageCol.setPrefWidth(70);

        // Колонки Valid/Invalid
        TableColumn<PageTableEntry, String> validCol = new TableColumn<>("Valid");
        validCol.setCellValueFactory(new PropertyValueFactory<>("validBit"));

        // Колонки Dirty/Reference
        TableColumn<PageTableEntry, String> dirtyCol = new TableColumn<>("Dirty");
        dirtyCol.setCellValueFactory(new PropertyValueFactory<>("dirtyBit"));

        TableColumn<PageTableEntry, String> refCol = new TableColumn<>("Reference");
        refCol.setCellValueFactory(new PropertyValueFactory<>("referenceBit"));

        table.getColumns().addAll(pageCol, frameCol, validCol, dirtyCol, refCol);

        return table;
    }

    private void startSimulation(Button startButton) {
        startButton.setDisable(true);
        statusLabel.setText("Симуляция запущена...");

        // Использование Timeline для пошагового выполнения с задержкой (динамическое обновление)
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (currentEventIndex < simulationEvents.size()) {
                PageAccessEvent event = simulationEvents.get(currentEventIndex);

                // Вызов логики симулятора
                String resultMessage = simulator.referencePage(event.pageNumber, event.isWriteOperation);

                // Обновление GUI
                statusLabel.setText(String.format("Шаг %d: %s", currentEventIndex + 1, resultMessage));
                tableView.refresh(); // Обновить таблицу, чтобы показать изменения свойств

                currentEventIndex++;
            } else {
                timeline.stop();
                statusLabel.setText("Симуляция завершена!");
                startButton.setText("Завершено");
            }
        }));

        timeline.setCycleCount(simulationEvents.size() + 1);
        timeline.play();
    }

    // Кастомный рендер ячейки для отображения N/A вместо -1
    private static class FrameNumberCell extends TableCell<PageTableEntry, Integer> {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else if (item.intValue() == -1) {
                setText("N/A");
            } else {
                setText(String.valueOf(item.intValue()));
            }
        }
    }
}