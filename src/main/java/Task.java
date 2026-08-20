public class Task {
    /** The possible states of a task. */
    public enum TaskStatus {
        DONE, NOT_DONE
    }

    private String description;
    private TaskStatus status = TaskStatus.NOT_DONE;

    public Task(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isDone() {
        return this.status == TaskStatus.DONE;
    }

    public void markDone() {
        this.status = TaskStatus.DONE;
    }

    public void markUndone() {
        this.status = TaskStatus.NOT_DONE;
    }

    @Override
    public String toString() {
        String statusIcon = this.status == TaskStatus.DONE ? "[X]" : "[ ]";
        return statusIcon + " " + this.getDescription();
    }
}
