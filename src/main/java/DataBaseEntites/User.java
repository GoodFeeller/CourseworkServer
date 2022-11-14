package DataBaseEntites;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonAutoDetect
@Entity
@Table(name="accounts")
public class User {
    @Id
    @Column(name = "Login")
    private String login;
    @Column(name="Password")
    private String password;
    @Column(name="Name")
    private String name;
    @Column(name = "Surname")
    private String surname;
    @Column(name="SecondName")
    private String secondName;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FamilyID",referencedColumnName = "id",nullable = true)
    private Family family;

    @Column(name = "Admin")
    private boolean admin;
    @Column(name = "Connected")
    private boolean connected;
}
