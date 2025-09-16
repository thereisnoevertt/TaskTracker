package Tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.setTaskType(TaskType.EPIC);
    }


    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void clearSubtasksIds() {
        subtasksIds.clear();
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksIds.remove(subtaskId);
    }

    @Override
    public void setStatus(Status status) {
    }
}
