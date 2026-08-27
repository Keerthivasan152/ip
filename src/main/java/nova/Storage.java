package nova;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles saving tasks to and loading tasks from a file on disk.
 * Save format: one task per line, fields separated by " | ".
 * e.g., T | NOT_DONE | read book
 *       D | DONE | return book | 2026-08-28
 *       E | NOT_DONE | project meeting | 2026-08-28 | 2026-08-29
 */
public class Storage {
    private static final String DEFAULT_PATH = "data/nova.txt";
    private static final String SEPARATOR = " | ";

    private final String path;

    /**
     * Creates a storage that saves to and loads from the default file.
     */
    public Storage() {
        this.path = DEFAULT_PATH;
    }

    /**
     * Creates a storage that saves to and loads from the given file.
     *
     * @param path the save file path
     */
    public Storage(String path) {
        this.path = path;
    }

    /**
     * Writes all tasks to the save file, one task per line.
     * Creates the parent directory if it does not exist yet.
     *
     * @param tasks the tasks to save
     */
    public void save(ArrayList<Task> tasks) {
        try {
            new File(this.path).getParentFile().mkdirs();
            FileWriter writer = new FileWriter(this.path);
            for (Task task : tasks) {
                writer.write(toFileLine(task) + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads tasks from the save file.
     * Returns an empty list if the file does not exist yet, and
     * silently skips any malformed lines so one bad line cannot break startup.
     *
     * @return the loaded tasks, or an empty list if there is nothing to load
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(this.path));
            while (scanner.hasNextLine()) {
                Task task = parseLine(scanner.nextLine());
                if (task != null) {
                    tasks.add(task);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            // no save file yet; start with an empty list
        }
        return tasks;
    }

    private String toFileLine(Task task) {
        String status = task.isDone() ? "DONE" : "NOT_DONE";
        if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            return "D" + SEPARATOR + status + SEPARATOR + d.getDescription() + SEPARATOR + d.getBy();
        }
        if (task instanceof Event) {
            Event e = (Event) task;
            return "E" + SEPARATOR + status + SEPARATOR + e.getDescription()
                    + SEPARATOR + e.getFrom() + SEPARATOR + e.getTo();
        }
        return "T" + SEPARATOR + status + SEPARATOR + task.getDescription();
    }

    private static Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 2) {
            return null; // malformed line
        }
        Task task;
        try {
            switch (parts[0]) {
            case "T":
                if (parts.length != 3) {
                    return null;
                }
                task = new Todo(parts[2]);
                break;
            case "D":
                if (parts.length != 4) {
                    return null;
                }
                task = new Deadline(parts[2], LocalDate.parse(parts[3]));
                break;
            case "E":
                if (parts.length != 5) {
                    return null;
                }
                task = new Event(parts[2], LocalDate.parse(parts[3]), LocalDate.parse(parts[4]));
                break;
            default:
                return null;
            }
        } catch (DateTimeParseException e) {
            return null; // malformed line
        }
        if (parts[1].equals("DONE")) {
            task.markDone();
        }
        return task;
    }
}