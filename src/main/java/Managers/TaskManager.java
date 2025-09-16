package Managers;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import java.util.ArrayList;

public interface TaskManager {

    public void getAllTasks();

    public void clearAllTasks();

    public Task getTaskById(int id);

    public void addTask(Task task);

    public void updateTask(int id, Task newTask);

    public void removeTaskById(int id);

    public void getAllSubtasks();

    public ArrayList<Subtask> getSubtasksByEpicId(int id);

    public void clearAllSubtasks();

    public Subtask getSubtaskById(int id);

    public void getAllEpics();

    public void clearAllEpics();

    public Epic getEpicById(int id);

    public void addEpic(Epic epic);

    public void updateEpic(int id, Epic newEpic);

    public void removeEpicById(int id);

    public int getNextId();

    public void setNextId(int nextId);

    public void updateEpicStatus(Epic epic);

    public void addSubtask(Subtask subtask);

    public void updateSubtask(int id, Subtask newSubtask);

    public void removeSubtaskById(int id);

    public void printHistoryTasks();
}
