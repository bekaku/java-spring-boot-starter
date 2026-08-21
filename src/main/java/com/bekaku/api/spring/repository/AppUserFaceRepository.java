package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.dto.FaceRecognitionDtos;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.AppUserFace;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserFaceRepository extends BaseRepository<AppUserFace,Long>, JpaSpecificationExecutor<AppUserFace> {


    Optional<AppUserFace> findByAppUserId(Long appUserId);
    boolean existsByAppUserId(Long appUserId);

    @Query(value = """
            SELECT
                a.id AS appUserId,
                a.email AS email,
                a.username AS username,
                fm.id AS fileManagerId,
                fm.file_path AS filePath,
                mime.name AS fileMimeName,
                (af.embedding <=> cast(:targetVector as vector)) AS distance
            FROM app_user_face af
            JOIN app_user a ON af.app_user = a.id
            LEFT JOIN file_manager fm ON af.file_manager = fm.id
            LEFT JOIN file_mime mime ON fm.file_mime_id = mime.id
            WHERE a.active = true 
              AND a.deleted = false
              AND (af.embedding <=> cast(:targetVector as vector)) <= :threshold
            ORDER BY distance ASC
            LIMIT 1
        """, nativeQuery = true)
    List<Object[]> findClosestFaceRaw(
            @Param("targetVector") String targetVector,
            @Param("threshold") double threshold
    );

    // Default method สำหรับ Map ค่า Object[] ไปเป็น DTO (Projection)
    default FaceRecognitionDtos.FaceMatchProjection findClosestFace(String targetVector, double threshold) {
        List<Object[]> rows = findClosestFaceRaw(targetVector, threshold);
        if (rows.isEmpty()) {
            return null;
        }
        Object[] row = rows.get(0);
        return new FaceRecognitionDtos.FaceMatchProjection(
                ((Number) row[0]).longValue(), // appUserId
                (String) row[1],               // email
                (String) row[2],               // username
                row[3] != null ? ((Number) row[3]).longValue() : null, // fileManagerId (เผื่อเป็น null)
                (String) row[4],               // filePath
                (String) row[5],               // fileMimeName
                ((Number) row[6]).doubleValue()// distance
        );
    }
}
