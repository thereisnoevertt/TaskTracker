package Managers;

import HistoryManagers.HistoryManager;
import HistoryManagers.InMemoryHistoryManager;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.util.*;
import java.util.stream.Collectors;

import static Tasks.Status.*;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 0;
    private static final Map<Integer, Task> tasks = new HashMap<>();
    private static final Map<Integer, Subtask> subtasks = new HashMap<>();
    private static final Map<Integer, Epic> epics = new HashMap<>();
    private static final LinkedList<Task> historyTasks = new LinkedList<>();

    public static HashMap<Integer, Task> getTaskMap() {
        return (HashMap<Integer, Task>) tasks;
    }

    public static HashMap<Integer, Subtask> getSubtaskMap() {
        return (HashMap<Integer, Subtask>) subtasks;
    }

    public static HashMap<Integer, Epic> getEpicMap() {
        return (HashMap<Integer, Epic>) epics;
    }

    public void getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список обычных задач пуст.");
        } else {
            System.out.println("Обычные задачи:");
            for (Task task : tasks.values()) {
                System.out.println(task.getId() + " - " + task.getName());
            }
        }
    }

    public void clearAll() {
        clearAllEpics();
        clearAllSubtasks();
        clearAllTasks();
    }

    public void clearAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyTasks.add(tasks.get(id));
            return tasks.get(id);
        } else {
            throw new NoSuchElementException("Задача с id=" + id + " не найдена.");
        }
    }

    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        task.setStatus(NEW);
    }

    public void updateTask(int id, Task newTask) {
        newTask.setId(id);
        tasks.put(newTask.getId(), newTask);
    }

    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задачи не найдено");
        }
    }

    public void getAllSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Список подзадач пуст.");
        } else {
            System.out.println("Подзадачи:");
            for (Subtask sub : subtasks.values()) {
                System.out.println(sub.getId() + " - " + sub.getName());
            }
        }
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                subtasksByEpic.add(subtask);
            }
        }
        if (subtasksByEpic.isEmpty()) {
            System.out.println("В эпике отсутствуют подзадачи");
            return null;
        } else {
            return subtasksByEpic;
        }
    }

    public void clearAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasksIds();
        }
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyTasks.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            throw new NoSuchElementException("Задача с id=" + id + " не найдена.");
        }
    }

    public void getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список эпиков пуст.");
        } else {
            System.out.println("Эпики:");
            for (Epic epic : epics.values()) {
                System.out.println(epic.getId() + " - " + epic.getName());
            }
        }
    }

    public void clearAllEpics() {
        epics.clear();
        subtasks.values().removeIf(sub -> epics.containsKey(sub.getEpicId()));
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyTasks.add(epics.get(id));
            return epics.get(id);
        } else {
            throw new NoSuchElementException("Задача с id=" + id + " не найдена.");
        }
    }

    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(int id, Epic newEpic) {
        Epic existing = epics.get(id);
        if (existing != null) {
            newEpic.setId(id);
            newEpic.clearSubtasksIds();
            newEpic.getSubtasksIds().addAll(existing.getSubtasksIds());
            epics.put(id, newEpic);
            updateEpicStatus(newEpic);
        }
    }

    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasksIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtasksIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int id : subtaskIds) {
            Status status = subtasks.get(id).getStatus();
            if (status != Status.NEW) {
                allNew = false;
            }
            if (status != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(DONE);
        } else if (allNew) {
            epic.setStatus(NEW);
        } else {
            epic.setStatus(IN_PROGRESS);
        }
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    public void updateSubtask(int id, Subtask newSubtask) {
        if (!subtasks.containsKey(id)) return;

        newSubtask.setId(id);
        subtasks.put(id, newSubtask);
        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    public void removeSubtaskById(int id) {
        Subtask sub = subtasks.remove(id);
        if (sub != null) {
            Epic epic = epics.get(sub.getEpicId());
            if (epic != null) {
                epic.getSubtasksIds().remove(Integer.valueOf(id));
                updateEpicStatus(epic);
            }
        }
    }

    public static LinkedList<Task> getHistory() {
        if (historyTasks.size() > 9) {
            historyTasks.removeFirst();
        }
        return historyTasks;
    }

    public void printHistoryTasks() {
        LinkedList<Task> tasks = getHistory();
        System.out.println("Список просмотренных задач \nID:, Название задачи:");
        for (Task task : tasks) {
            System.out.println(task.getId() + ", " + task.getName());
        }
    }

    public static String historyToString() {
        LinkedList<Task> history = getHistory();
        return history.stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(","));
    }

    public HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
