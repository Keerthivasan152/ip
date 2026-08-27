package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void saveThenLoad_roundTripsAllTypes() throws IOException {
        Storage storage = new Storage(tempDir.resolve("data/nova.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markDone();
        tasks.add(deadline);
        tasks.add(new Event("meeting", LocalDate.of(2026, 8, 28), LocalDate.of(2026, 8, 29)));
        storage.save(tasks);

        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0) instanceof Todo);
        assertTrue(loaded.get(1).isDone());
        assertEquals("[D][X] return book (by: Oct 15 2019)", loaded.get(1).toString());
    }

    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("no-such-dir/nova.txt").toString());
        assertEquals(0, storage.load().size());
    }

    @Test
    public void load_corruptedLine_skipsIt() throws IOException {
        Path dir = tempDir.resolve("data");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("nova.txt"),
                "T | DONE | valid task\nD | NOT_DONE | x | Sunday\nGARBAGE\n");
        Storage storage = new Storage(dir.resolve("nova.txt").toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
    }
}