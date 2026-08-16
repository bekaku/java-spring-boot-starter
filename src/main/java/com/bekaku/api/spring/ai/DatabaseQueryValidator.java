package com.bekaku.api.spring.ai;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class DatabaseQueryValidator {

    private static final Pattern COMMENT_PATTERN =
            Pattern.compile(
                    "(--|/\\*|\\*/)",
                    Pattern.CASE_INSENSITIVE
            );

    private static final Pattern FORBIDDEN_PATTERN =
            Pattern.compile(
                    "\\b(" +
                            "INSERT|UPDATE|DELETE|UPSERT|MERGE|" +
                            "DROP|ALTER|CREATE|TRUNCATE|" +
                            "GRANT|REVOKE|CALL|" +
                            "COMMENT|VACUUM|ANALYZE|" +
                            "REFRESH|REINDEX|" +
                            "COPY|DO" +
                            ")\\b",
                    Pattern.CASE_INSENSITIVE
            );

    public void validate(String sql) {

        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException(
                    "SQL must not be empty"
            );
        }

        String normalized =
                sql.trim();

        // Must start with SELECT or WITH
        if (!normalized.matches(
                "(?is)^(SELECT|WITH)\\b.*")) {

            throw new IllegalArgumentException(
                    "Only SELECT queries are allowed"
            );
        }

        // Reject comments
        if (COMMENT_PATTERN.matcher(normalized).find()) {

            throw new IllegalArgumentException(
                    "SQL comments are not allowed"
            );
        }

        // Reject dangerous statements
        if (FORBIDDEN_PATTERN
                .matcher(normalized)
                .find()) {

            throw new IllegalArgumentException(
                    "Only read-only SELECT queries are allowed"
            );
        }

        // Prevent multiple statements
        String withoutTrailingSemicolon =
                normalized.replaceFirst(
                        ";\\s*$",
                        ""
                );

        if (withoutTrailingSemicolon.contains(";")) {

            throw new IllegalArgumentException(
                    "Multiple SQL statements are not allowed"
            );
        }
    }
}