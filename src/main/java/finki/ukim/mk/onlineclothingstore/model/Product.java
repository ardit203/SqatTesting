package finki.ukim.mk.onlineclothingstore.model;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(length = 2000)
    private String description;

    private Double price;

    @ManyToOne
    private Category category;

    @Enumerated(EnumType.STRING)
    private Department department;

    private String image;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variant> variants;

    public Product(String name, String description, Double price, Department department, String image, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.department = department;
        this.image = image;
    }

    public Product(String name, String description, Double price, Department department, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.department = department;
    }
}
