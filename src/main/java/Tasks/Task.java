package Tasks;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    private TaskType taskType;

    public String getName() {
        return name;
    }
    public Task(int id, TaskType type, String name, Status status, String description) {
        this.id = id;
        this.taskType = type;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.taskType = TaskType.TASK;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String toString() {
        return String.format("%d,%s,%s,%s,%s, %s",
                id,
                getTaskType(),   // TASK, EPIC, SUBTASK
                name,
                status,     //NEW, IN_PROGRESS, DONE
                description,
                (this instanceof Subtask subtask ? subtask.getEpicId() : "")
        );
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
}
