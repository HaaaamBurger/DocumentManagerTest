import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

public class DocumentManager {

    public final Map<String, Document> storage = new HashMap<>();

    public Document save(Document document) {

        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
        } else {
            findById(document.getId()).ifPresent(existingDocument -> {
                throw new DocumentAlreadyExistsException("Document with ID " + existingDocument.getId() + " already exists");
            });
        }

        storage.put(document.getId(), document);

        return document;
    }

    public List<Document> search(SearchRequest request) {

        return storage.values().stream()
                .filter(document -> matchesTitlePrefixes(document, request.getTitlePrefixes()))
                .filter(document -> matchesContainsContents(document, request.getContainsContents()))
                .filter(document -> matchesAuthorIds(document, request.getAuthorIds()))
                .filter(document -> matchesCreatedRange(document, request.getCreatedFrom(), request.getCreatedTo()))
                .toList();
    }

    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }

    private boolean matchesTitlePrefixes(Document document, List<String> titlePrefixes) {
        if (titlePrefixes == null || titlePrefixes.isEmpty()) {
            return true;
        }

        return titlePrefixes.stream().anyMatch(prefix -> document.getTitle().startsWith(prefix));
    }

    private boolean matchesContainsContents(Document document, List<String> containsContents) {
        if (containsContents == null || containsContents.isEmpty()) {
            return true;
        }

        return containsContents.stream().anyMatch(content -> document.getContent().contains(content));
    }

    private boolean matchesAuthorIds(Document document, List<String> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return true;
        }

        return authorIds.contains(document.getAuthor().getId());
    }

    private boolean matchesCreatedRange(Document document, Instant createdFrom, Instant createdTo) {
        Instant created = document.getCreated();
        if (createdFrom != null && created.isBefore(createdFrom)) {
            return false;
        }
        return createdTo == null || !created.isAfter(createdTo);
    }
}


class DocumentAlreadyExistsException extends RuntimeException {
    public DocumentAlreadyExistsException(String message) {
        super(message);
    }

}