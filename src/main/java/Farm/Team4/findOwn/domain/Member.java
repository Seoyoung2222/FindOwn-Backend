package Farm.Team4.findOwn.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Member {
    @Id
    private String id;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    protected Member(){} //JPA 사용을 위한 빈 생성자

}