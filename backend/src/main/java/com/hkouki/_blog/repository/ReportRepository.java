package com.hkouki._blog.repository;

import com.hkouki._blog.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByHandledFalse();
}
