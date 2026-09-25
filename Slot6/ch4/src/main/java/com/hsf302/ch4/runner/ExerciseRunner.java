package com.hsf302.ch4.runner;

import com.hsf302.ch4.pojo.Gender;
import com.hsf302.ch4.pojo.Student;
import com.hsf302.ch4.service.DepartmentService;
import com.hsf302.ch4.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
public class ExerciseRunner implements CommandLineRunner {

    private final DepartmentService departmentService;
    private final StudentService studentService;

    @Override
    public void run(String... args) {
        System.out.println("========== EXERCISE RUNNER STARTED ==========");
        todo6();
        todo7();
        todo8();
        todo9();
        todo10();
        todo11();
        todo12();
        todo13();
        todo14();
    }

    private void todo6() {
        title("TODO 6: count / findById / existsById");
        System.out.println("Departments: " + departmentService.count());
        System.out.println("Students   : " + studentService.count());

        studentService.findById(1L).ifPresentOrElse(
                s -> System.out.println("findById(1)  -> " + s),
                () -> System.out.println("findById(1)  -> Not found"));

        System.out.println("findById(99) -> " + studentService.findById(99L)
                .map(Object::toString)
                .orElse("Not found"));

        System.out.println("existsById(4) department -> " + departmentService.existsById(4L));
    }

    private void todo7() {
        title("TODO 7: Sort & Pageable");

        printList("All students order by GPA desc", studentService.findAllOrderByGpaDesc());

        Page<Student> page = studentService.findPage(1, 3, "fullName");
        printList("Page index " + page.getNumber() + " (size " + page.getSize() + ")", page.getContent());
        System.out.println("totalElements=" + page.getTotalElements()
                + ", totalPages=" + page.getTotalPages()
                + ", hasNext=" + page.hasNext()
                + ", hasPrevious=" + page.hasPrevious());
    }

    private void todo8() {
        title("TODO 8: findBy / existsBy / countBy");
        for (String code : List.of("AI002", "XX999")) {
            System.out.println("findByStudentCode(" + code + ") -> " +
                    studentService.findByStudentCode(code).map(Object::toString).orElse("Not found"));
        }
        System.out.println("isEmailExisted(binh.tt@fpt.edu.vn) -> "
                + studentService.isEmailExisted("binh.tt@fpt.edu.vn"));
        System.out.println("countActive -> " + studentService.countActive());
    }

    private void todo9() {
        title("TODO 9: ContainingIgnoreCase / EndingWith / IsNull");
        printList("Name contains 'thi' (case-insensitive)", studentService.searchByName("thi"));
        printList("Email ending with '@gmail.com'", studentService.findByEmailDomain("@gmail.com"));
        printList("Students without email", studentService.findWithoutEmail());
    }

    private void todo10() {
        title("TODO 10: Between / And / True / After");
        printList("GPA in [3.0, 3.6] desc", studentService.findByGpaRange(3.0, 3.6));
        printList("MALE & active", studentService.findActiveByGender(Gender.MALE));
        printList("dob after 2005-01-01", studentService.findBornAfter(LocalDate.of(2005, 1, 1)));
    }

    private void todo11() {
        title("TODO 11: Nested property / Top / IsEmpty");
        printList("Students of SE (order by name)", studentService.findByDepartment("SE"));
        System.out.println("count students of AI -> " + studentService.countByDepartment("AI"));
        printList("Top 3 GPA", studentService.findTop3ByGpa());
        printList("Departments without students", departmentService.findDepartmentsWithoutStudents());
    }

    private void todo12() {
        title("TODO 12: JPQL + named parameter");
        printList("SE, GPA >= 3.0", studentService.findGoodStudents("SE", 3.0));
    }

    private void todo13() {
        title("TODO 13: JPQL LIKE");
        printList("Search 'an'", studentService.searchByKeyword("an"));
        printList("Search '@gmail.com'", studentService.searchByKeyword("@gmail.com"));
        printList("Search empty keyword", studentService.searchByKeyword("   "));
    }

    private void todo14() {
        title("TODO 14: DTO projection + aggregate + LEFT JOIN");
        printList("Department statistics", departmentService.getStatistics());
    }

    private void title(String t) {
        System.out.println("\n===== " + t + " =====");
    }

    private void printList(String label, Collection<?> list) {
        System.out.println("-- " + label + ":");
        if (list == null || list.isEmpty()) {
            System.out.println("   (empty)");
            return;
        }
        list.forEach(e -> System.out.println("   " + e));
        System.out.println("   -> " + list.size() + " record(s)");
    }
}