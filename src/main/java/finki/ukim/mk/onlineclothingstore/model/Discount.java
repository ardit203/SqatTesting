//package finki.ukim.mk.onlineclothingstore.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//public class Discount {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @OneToOne
//    @JoinColumn(name = "category_id", unique = true)
//    private Category category;
//
//    @Column(name = "discount_value")
//    private double value;
//    private boolean active;
//
//    private LocalDateTime validFrom;
//    private LocalDateTime validTo;
//
//    public void deactivate(){
//        active = false;
//    }
//
//    public void activate(){
//        active = true;
//    }
//
//    public Discount(Category category, double value, LocalDateTime validFrom, LocalDateTime validTo) {
//        this.category = category;
//        this.value = value;
//        this.validFrom = validFrom;
//        this.validTo = validTo;
//        this.active = false;
//    }
//}
