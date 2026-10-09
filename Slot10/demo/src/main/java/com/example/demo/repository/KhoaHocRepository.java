package com.example.demo.repository;

import com.example.demo.model.KhoaHoc;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhoaHocRepository extends JpaRepository<KhoaHoc, Long> {
    List<KhoaHoc> findByTenKhoaHocContainingIgnoreCase(String keyword, Sort sort);
}
