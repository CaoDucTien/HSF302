package com.example.demo.config;

import com.example.demo.model.KhoaHoc;
import com.example.demo.repository.KhoaHocRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final KhoaHocRepository khoaHocRepository;

    public DataInitializer(KhoaHocRepository khoaHocRepository) {
        this.khoaHocRepository = khoaHocRepository;
    }

    @Override
    public void run(String... args) {
        if (khoaHocRepository.count() == 0) {
            khoaHocRepository.saveAll(List.of(
                    new KhoaHoc("Lập trình Java", "Nguyễn Văn A", 3),
                    new KhoaHoc("Spring Boot cơ bản", "Trần Thị B", 3),
                    new KhoaHoc("Cơ sở dữ liệu", "Lê Văn C", 4)
            ));
        }
    }
}
