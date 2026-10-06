package fu.se123456.pojo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(precision = 10, scale = 2)
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate hireDate;

    private boolean active = true;

    // Quan he OneToMany cu voi Department (cho phep null de doc lap voi Project)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;

    // TODO 5.2 — Owning side trong Employee:
    // KHONG dung cascade = ALL hay CascadeType.REMOVE o quan he N-N!
    // Giai thich: Trong quan he Many-to-Many, viec xoa 1 Employee khong duoc phep xoa luon Project
    // (vi Project do con co nhieu nhan vien khac dang tham gia), va nguoc lai xoa 1 Project khong duoc xoa Employee.
    @ManyToMany
    @JoinTable(
            name = "employee_project",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private Set<Project> projects = new HashSet<>();

    @Transient
    private int yearsOfService;

    public Employee() {
    }

    public Employee(String email, String fullName, Gender gender, BigDecimal salary, LocalDate hireDate) {
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.salary = salary;
        this.hireDate = hireDate;
        this.active = true;
    }

    public Employee(String email, String fullName, Gender gender, BigDecimal salary, LocalDate hireDate, boolean active) {
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.salary = salary;
        this.hireDate = hireDate;
        this.active = active;
    }

    public Employee(Long id, String fullName, String email, BigDecimal salary, Gender gender, LocalDate hireDate, boolean active, Department department) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.salary = salary;
        this.gender = gender;
        this.hireDate = hireDate;
        this.active = active;
        this.department = department;
    }

    // TODO 5.5 — Helper method assignToProject dong bo 2 chieu
    public void assignToProject(Project p) {
        if (p != null) {
            this.projects.add(p);
            p.getEmployees().add(this);
        }
    }

    // TODO 5.9 — Helper method unassignFromProject go khoi du an dong bo 2 chieu
    public void unassignFromProject(Project p) {
        if (p != null) {
            this.projects.remove(p);
            p.getEmployees().remove(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public int getYearsOfService() {
        if (this.hireDate != null) {
            return Period.between(this.hireDate, LocalDate.now()).getYears();
        }
        return 0;
    }

    public void setYearsOfService(int yearsOfService) {
        this.yearsOfService = yearsOfService;
    }

    // TODO 5.4 — Override equals()/hashCode():
    // Employee dua tren email (business key) — khong dung id.
    // Giai thich ly do: Khi entity o trang thai Transient (chua luu xuong DB), id luon la null.
    // Neu dung id, nhieu entity vua khoi tao deu co id == null va bi Set/HashSet coi la 1 object trung lap.
    // Email la truong unique, not null, dai dien duy nhat cho Employee xuyen suot cac trang thai cua JPA.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", salary=" + salary +
                ", gender=" + gender +
                ", hireDate=" + hireDate +
                ", active=" + active +
                ", projectCount=" + (projects != null ? projects.size() : 0) +
                '}';
    }
}