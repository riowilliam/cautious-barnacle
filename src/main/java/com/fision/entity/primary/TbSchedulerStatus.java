package com.fision.entity.primary;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tb_scheduler_status")
@Data
public class TbSchedulerStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduler_name", unique = true)
    private String schedulerName;

    @Column(name = "last_run_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRunTime;

    @Column(name = "is_running")
    private Boolean isRunning;

    @Column(name = "status_message")
    private String statusMessage;
}
