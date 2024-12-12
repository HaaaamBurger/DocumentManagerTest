import java.time.Instant;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        DocumentManager documentManager = new DocumentManager();

        DocumentManager.Author author = new DocumentManager.Author("1", "Andriy");

        DocumentManager.Document document1 = new DocumentManager.Document("", "Portfolio", "My name is Andriy", author, Instant.now());
        documentManager.save(document1);

        DocumentManager.Document document2 = new DocumentManager.Document("", "Salary", "My name is Andriy", author, Instant.now());
        documentManager.save(document2);


        List<DocumentManager.Document> port = documentManager.search(
                new DocumentManager.SearchRequest(
                        List.of("Sa", "Port"),
                        List.of("My name is An"),
                        List.of(document1.getAuthor().getId()),
                        Instant.parse("2023-01-01T00:00:00Z"),
                        Instant.parse("2025-12-31T23:59:59Z")
                )
        );

        for (DocumentManager.Document document : port) {
            System.out.println(document);
        }

    }
}