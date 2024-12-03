//package entities;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.math.BigInteger;
//
//
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "employees")
//@Getter
//@Setter
//public class Employee {
//
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    public Integer id;
//
//
//    @Column(name = "FirstName", length = 255, nullable = false)
//    public String firstname;
//
//    @Column(name = "LastName", length = 255, nullable = false)
//    public String lastname;
//
//    @Column(name = "Email", length = 255, nullable = false)
//    public String email;
//
//    @Column(name = "Phone_No", length = 20, nullable = false)
//    public int phoneno;
//
//    @Column(name = "JobTitle", length = 255, nullable = false)
//    public String job_title;
//
//}
