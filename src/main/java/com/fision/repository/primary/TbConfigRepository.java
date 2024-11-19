package com.fision.repository.primary;

import com.fision.entity.primary.TbConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TbConfigRepository extends JpaRepository<TbConfig, Long> {
    @Query("SELECT tbc.value as value, tbc.desc as desc " +
            "FROM TbConfig tbc " +
            "WHERE tbc.key = :key")
    List<Map<String, Object>> findValueAndDescByKey(@Param("key") String key);
}
