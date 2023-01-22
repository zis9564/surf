import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Index index = new Index(Executors.newFixedThreadPool(3));
//        index.indexAllTxtInPath("/path/to/dir/");
    }
}