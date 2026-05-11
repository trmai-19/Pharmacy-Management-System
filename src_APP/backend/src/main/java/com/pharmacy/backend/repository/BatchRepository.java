package com.pharmacy.backend.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pharmacy.backend.model.Batch;

@Repository
public interface BatchRepository extends JpaRepository<Batch, String> {
    List<Batch> findByHsdBetween(Date startDate, Date endDate);
    List<Batch> findByMasp(String masp);
}