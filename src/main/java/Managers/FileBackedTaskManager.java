package Managers;

import Tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String pathOfFile = "csv/save.csv";

    public String getPath() {
        return pathOfFile;
    }

    static File tasksStorage = new File(pathOfFile);

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(tasksStorage.toPath())))) {
            writer.write("ID, TYPE, NAME, STATUS, DESCRIPTION, EPIC");
            writer.newLine();

            for (Task task : InMemoryTaskManager.getTaskMap().values()) {
                writer.write(task.toString() + "\n");
            }
            for (Subtask subtask : InMemoryTaskManager.getSubtaskMap().values()) {
                writer.write(subtask.toString() + "\n");
            }
            for (Epic epic : InMemoryTaskManager.getEpicMap().values()) {
                writer.write(epic.toString() + "\n");
            }
            writer.newLine();

            writer.write(InMemoryTaskManager.historyToString());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении данных в файл");
        }
    }

    public static Task fromString(String value) {
        String[] params = value.split(",");
        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].trim(); // Удаляем пробелы вокруг
        }
        int id = Integer.parseInt(params[0]);
        TaskType type = TaskType.valueOf(params[1]);
        String name = params[2];
        Status status = Status.valueOf(params[3]);
        String description = params[4];

        if (type == TaskType.TASK) {
            Task task = new Task(name, description);
            task.setId(id);
            task.setStatus(status);
            return task;
        } else if (type == TaskType.EPIC) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(params[5]);
            Subtask subtask = new Subtask(name, description, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            return subtask;
        } else {
            throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager();

        Map<Integer, Task> buffer = new HashMap<>();
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }

        int emptyLineIndex = lines.indexOf("");
        if (emptyLineIndex == -1) emptyLineIndex = lines.size();
        for (int i = 1; i < emptyLineIndex; i++) {
            String line = lines.get(i);
            Task task = fromString(line);
            buffer.put(task.getId(), task);

            if (task instanceof Epic) {
                manager.addEpic((Epic) task);
            } else if (task instanceof Subtask) {
                manager.addSubtask((Subtask) task);
            } else {
                manager.addTask(task);
            }
        }

        if (emptyLineIndex + 1 < lines.size()) {
            String historyLine = lines.get(emptyLineIndex + 1);
            String[] ids = historyLine.split(",");
            for (String idStr : ids) {
                int id = Integer.parseInt(idStr);
                if (InMemoryTaskManager.getTaskMap().containsKey(id)) {
                    manager.getTaskById(id);
                } else if (InMemoryTaskManager.getEpicMap().containsKey(id)) {
                    manager.getEpicById(id);
                } else if (InMemoryTaskManager.getSubtaskMap().containsKey(id)) {
                    manager.getSubtaskById(id);
                }
            }
        }

        return manager;
    }


    @Override
    public void clearAll() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void updateTask(int id, Task newTask) {
        super.updateTask(id, newTask);
        save();
    }

    @Override
    public void updateSubtask(int id, Subtask newSubtask) {
        super.updateSubtask(id, newSubtask);
        save();
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        super.updateEpic(id, epic);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    public static void main(String[] args) {
        // 1. Создаём менеджер и добавляем задачи
        FileBackedTaskManager manager = new FileBackedTaskManager();

        Task task = new Task("Проверить код", "Проверить, как всё работает");
        task.setStatus(Status.NEW);
        manager.addTask(task);

        Epic epic = new Epic("Сделать проект", "Разделить на подзадачи");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Написать тест", "Протестировать Epic", epic.getId());
        subtask.setStatus(Status.IN_PROGRESS);
        manager.addSubtask(subtask);

        // 2. Просматриваем задачи (создаём историю)
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        System.out.println("✅ Задачи до сохранения:");
        System.out.println(getTaskMap().values());
        System.out.println(getEpicMap().values());
        System.out.println(getSubtaskMap().values());
        System.out.println("История:");
        System.out.println(manager.getHistoryManager().getHistory());

        // 3. Сохраняем в файл
        manager.save();

        // 4. Загружаем из файла
        File file = new File(manager.getPath());
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // 5. Проверка
        System.out.println("\n✅ Задачи после загрузки из файла:");
        System.out.println(getTaskMap().values());
        System.out.println(getEpicMap().values());
        System.out.println(getSubtaskMap().values());
        System.out.println("История:");
        System.out.println(loadedManager.getHistoryManager().getHistory());
    }

}
