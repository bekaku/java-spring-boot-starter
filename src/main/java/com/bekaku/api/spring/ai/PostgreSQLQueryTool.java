package com.bekaku.api.spring.ai;


import com.bekaku.api.spring.dto.ChatSourceReference;
import com.bekaku.api.spring.dto.DatabaseQueryResult;
import com.bekaku.api.spring.enumtype.AiChatSourceType;
import com.bekaku.api.spring.properties.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PostgreSQLQueryTool {

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseQueryValidator queryValidator;
    private final AppProperties appProperties;

    public PostgreSQLQueryTool(
            JdbcTemplate jdbcTemplate,
            DatabaseQueryValidator queryValidator,
            AppProperties appProperties
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryValidator = queryValidator;
        this.appProperties =appProperties;
    }

    @Tool(description = """
            Execute a read-only PostgreSQL query.
            
            IMPORTANT:
            - Call searchSchema BEFORE this tool.
            - Use only tables and columns returned by searchSchema.
            - Only SELECT or WITH ... SELECT queries are allowed.
            - Never modify database data or structure.
            - The returned rows are the actual database results.
            - Never invent or estimate database results.
            """)
    @Transactional(readOnly = true)
    public DatabaseQueryResult executeSelect(
            @ToolParam(
                    description = """
                            PostgreSQL read-only SELECT SQL query.
                            Example:
                            SELECT COUNT(*) AS total
                            FROM public.customer
                            """
            )
            String sql,
            ToolContext toolContext
    ) {

        if (!appProperties.rag().databaseTools().enabled()) {
            return DatabaseQueryResult.empty();
        }
        log.info("AI called executeSelect: {}", sql);

        // 1. Validate SQL
        queryValidator.validate(sql);

        // 2. Execute
        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList(sql);

        // 3. Add source
        AiChatToolContext chatContext =
                getChatContext(toolContext);

        chatContext.addSource(
                ChatSourceReference.builder()
                        .type(AiChatSourceType.DATABASE_QUERY)
                        .query(sql)
                        .build()
        );

        return DatabaseQueryResult.builder()
                .rowCount(rows.size())
                .rows(rows)
                .build();
    }

    private AiChatToolContext getChatContext(
            ToolContext toolContext) {

        Object context =
                toolContext.getContext()
                        .get("chatToolContext");

        if (!(context instanceof AiChatToolContext)) {
            throw new IllegalStateException(
                    "chatToolContext is missing"
            );
        }

        return (AiChatToolContext) context;
    }
}