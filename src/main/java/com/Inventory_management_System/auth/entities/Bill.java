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
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Table(name = "bills")
//public class Bill {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @Column(name = "name", length = 255, nullable = false)
//    private String bill_no;
//
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="user_id")
//    private User user;
//
//    @Column(name = "Payment_type", length = 255, nullable = false)
//    private String PaymentType;
//
//    @Column(name = "payment_status", length = 50, nullable = false)
//    private String status;
//
//}
