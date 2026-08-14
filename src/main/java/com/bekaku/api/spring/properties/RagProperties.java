package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rag")
public record RagProperties(int topK,
                            double similarityThreshold,
                            int chunkSize,
                            int chunkOverlap,
                            int minChunkSizeChars,
                            int minChunkLengthToEmbed,
                            int maxNumChunks,
                            boolean deleteSourceAfterIngest
) {
}
