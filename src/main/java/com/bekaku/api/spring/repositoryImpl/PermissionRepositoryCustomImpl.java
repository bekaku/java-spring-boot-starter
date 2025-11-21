package com.bekaku.api.spring.repositoryImpl;

import com.bekaku.api.spring.dto.PermissionDto;
import com.bekaku.api.spring.repository.PermissionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PermissionRepositoryCustomImpl implements PermissionRepositoryCustom {

    private final EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;


    public PermissionRepositoryCustomImpl(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        this.entityManager = entityManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void softDeleteById(Long id) {
        String queryStr = "UPDATE permission e SET deleted = true WHERE e.id=:id AND p.deleted = false";
        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public Object[] findById(Long id) {
        String queryStr = "SELECT * FROM permission p WHERE p.id=:id AND p.deleted = false";
        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("id", id);
        return (Object[]) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAll() {
        String queryStr = "SELECT id, code, front_end FROM permission p WHERE p.deleted = false";
        Query query = entityManager.createNativeQuery(queryStr);
        return query.getResultList();
    }

    @Override
    public Optional<PermissionDto> findDtoBy(Long id) {
        String sql = """
                    SELECT p.id, p.operation_type, p.description,u.code
                    FROM permission p
                    WHERE p.id = ?
                """;

        try {
            PermissionDto dto = jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(PermissionDto.class),
                    id
            );
            /*
              PermissionDto dto =  jdbcTemplate.query(
                sql,
                new Object[]{id},
                (rs, rowNum) -> new PermissionDto(
                    rs.getLong("id"),
                    rs.getString("operation_type"),
                    rs.getString("description"),
                    rs.getString("code")
                )
            );
             */
            return Optional.ofNullable(dto);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }

        /* want return List<PermissionDto>

        //This method no paramiter
          String sql = """
            SELECT id, operation_type, description, code
            FROM permission
            """;

            return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(PermissionDto.class)
            );


            //This method has paramiter
              String sql = """
                SELECT id, operation_type, description, code
                FROM permission
                WHERE company_id = ?
                """;

            return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(PermissionDto.class),
                companyId
            );

         */
    }
}
