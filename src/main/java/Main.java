import Managers.*;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();
    }
}