package DataBaseEntites;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonAutoDetect()
@Builder
@Table(name = "family")
public class Family {
    @Id
    @Column(name = "id", nullable = false,unique = true)
    private int id;
    @Column(name = "имя", nullable = false)
    private String name;
    @Column(name = "доходы", nullable = false)
    private double income;
    @Column(name = "расходы", nullable = false)
    private double expenditure;
    @Column(name ="Создатель", nullable = false)
    private String creator;
    @JsonIgnore
    @OneToMany(mappedBy = "family",fetch = FetchType.EAGER)
    private List<User> members;
}
