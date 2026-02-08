package finki.ukim.mk.onlineclothingstore.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(length = 2000)
    private String description;

//    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Discount discount;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}