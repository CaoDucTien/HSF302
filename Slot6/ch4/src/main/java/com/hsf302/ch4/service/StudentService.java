package com.hsf302.ch4.service;

import com.hsf302.ch4.pojo.Student;
import java.util.Optional;

public interface StudentService {
    long count();
    Optional<Student> findById(Long id);
}