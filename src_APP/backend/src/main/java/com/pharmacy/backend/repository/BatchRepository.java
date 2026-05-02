package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, String> {
    List<Batch> findByHsdBetween(Date startDate, Date endDate);
}