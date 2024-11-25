package com.fision.repository.primary;

import com.fision.entity.primary.TbSchedulerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbSchedulerStatusRepository extends JpaRepository<TbSchedulerStatus, Long> {
    TbSchedulerStatus findBySchedulerName(String schedulerName);
}
