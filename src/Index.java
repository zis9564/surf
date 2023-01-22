import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Index {

    private final ExecutorService pool;
    private final Map<String, List<Pointer>> invertedIndex;

    public Index() {
        this.pool = Executors.newFixedThreadPool(3);
        this.invertedIndex = new ConcurrentHashMap<>();
    }

    public Index(ExecutorService pool) {
        this.pool = pool;
        this.invertedIndex = new ConcurrentHashMap<>();
    }

    public void indexAllTxtInPath(String path) {
        Objects.requireNonNull(path, "path is required.");
        var tasks = findTextFilesInDir(path)
                .stream()
                .map(p -> new IndexTask(p, invertedIndex))
                .collect(Collectors.toList());
        try {
            pool.invokeAll(tasks);
        } catch (InterruptedException e) {
            System.err.println("indexAllTxtInPath(): Error has occurred. message: " + e.getMessage());
        }
        shutdownAndAwaitTermination(pool);
    }

    public Map<String, List<Pointer>> getInvertedIndex() {
        return invertedIndex;
    }

    public List<Pointer> GetRelevantDocuments(String term) {
        Objects.requireNonNull(term, "term is required");
        return invertedIndex.get(term);
    }

    public Optional<Pointer> getMostRelevantDocument(String term) {
        Objects.requireNonNull(term, "term is required");
        return invertedIndex.get(term)
                .stream()
                .max(Comparator.comparing(Pointer::getCounter));
    }

    private List<String> findTextFilesInDir(String path) {
        Objects.requireNonNull(path, "path is required.");
        try (var stream = Files.list(Paths.get(path).toAbsolutePath())) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::toString)
                    .filter(name -> name.endsWith(".txt"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("indexAllTxtInDir(): problem occurred with path: " + path);
        }
        return List.of();
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("shutdownAndAwaitTermination(): Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    private static class IndexTask implements Callable<Map<String, List<Pointer>>> {
        private final String path;
        private final Map<String, List<Pointer>> invertedIndex;

        public IndexTask(String path,
                         Map<String, List<Pointer>> map) {
            this.path = Objects.requireNonNull(path, "path is required");
            this.invertedIndex = Objects.requireNonNull(map, "map is required");
        }

        @Override
        public Map<String, List<Pointer>> call() {
            try (Scanner scanner = new Scanner(new File(path))) {
                while (scanner.hasNext()) {
                    var word = scanner.next();
                    invertedIndex.compute(word, (key, val) -> {
                        if (val == null) {
                            return List.of(new Pointer(path));
                        } else {
                            List<Pointer> pointers = new ArrayList<>();
                            val.stream()
                                    .filter(p -> p.getFilePath().equals(path))
                                    .findFirst()
                                    .ifPresentOrElse(
                                            Pointer::incrementCounter,
                                            () -> pointers.add(new Pointer(path)));
                            pointers.addAll(val);
                            return pointers;
                        }
                    });
                }
            } catch (IOException e) {
                System.err.println("call(): Error has occurred. message: " + e.getMessage());
            }
            return null;
        }
    }
}

class Pointer {
    private Integer counter;
    private final String filePath;

    public Pointer(String filePath) {
        this.counter = 1;
        this.filePath = filePath;
    }

    public void incrementCounter() {
        this.counter++;
    }

    public Integer getCounter() {
        return this.counter;
    }

    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public String toString() {
        return "{" + "counter=" + counter + ", filePath='" + filePath + '\'' + '}';
    }
}