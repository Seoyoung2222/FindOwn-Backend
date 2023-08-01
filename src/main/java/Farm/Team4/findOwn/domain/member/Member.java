package Farm.Team4.findOwn.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @Column(name = "member_id")
    private String id;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private Date membershipDate;
    @OneToMany(mappedBy = "member")
    private List<MemberDesignHistory> designHistories = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    private List<MemberTrademarkHistory> trademarkHistories = new ArrayList<>();
    public Member(String id, String password, String name, String phoneNumber, String email, Date now){
        this.id = id;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.membershipDate = now;
    }
    public String changePassword(String newPassword){
        this.password = newPassword;
        return this.id;
    }
    public String changeEmail(String newEmail){
        this.email = newEmail;
        return this.email;
    }

}