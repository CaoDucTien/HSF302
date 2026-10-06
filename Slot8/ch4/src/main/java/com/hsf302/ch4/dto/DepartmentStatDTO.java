package com.hsf302.ch4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStatDTO {
    private String departmentCode;
    private String departmentName;
    private Long studentCount;
    private Double averageGpa;

    @Override
    public String toString() {
        return String.format("%-4s| %-25s | %2d | %s",
                departmentCode,
                departmentName,
                studentCount,
                averageGpa == null ? "null" : String.format("%.3f", averageGpa));
    }
}