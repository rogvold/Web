package ru.cardio.core.jpa.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author rogvold
 */
@Entity
public class UserCard implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(length = 3000)
    private String description;
    @Column(length = 3000)
    private String diagnosis;
    private Long userId;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date birthDate;
    @Column(length = 3000)
    private String aboutMe;
    @Column
    private Integer sex;
    @Column
    private Float height;
    @Column
    private Float weight;
    @Column
    private Integer age;
    @Column(length = 300)
    private String phone;
    // here will be json serialize object
    @Column(length = 3000)
    private String jsonTimetable;

    public UserCard() {
        super();
    }

    public UserCard(String firstName, String lastName, String description, String diagnosis, String aboutMe) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.diagnosis = diagnosis;
        this.aboutMe = aboutMe;
    }

    public UserCard(String firstName, String lastName, String description, String diagnosis, String aboutMe, Integer sex, Float height, Float weight, Integer age, Date birthDate, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.diagnosis = diagnosis;
        this.aboutMe = aboutMe;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.birthDate = birthDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return (description == null) ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getHeight() {
        return (height == null) ? 0 : height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Integer getSex() {
        return (sex == null) ? 1 : sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Float getWeight() {
        return (weight == null) ? 0 : weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getDiagnosis() {
        return (diagnosis == null) ? "" : diagnosis;
    }

    public void setDiagnosis(String diadnoses) {
        this.diagnosis = diadnoses;
    }

    public String getFirstName() {
        return (firstName == null) ? "" : firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return (lastName == null) ? "" : lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAboutMe() {
        return (aboutMe == null) ? "" : aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public Date getBirthDate() {
        return (birthDate == null) ? new Date() : birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJsonTimetable() {
        return jsonTimetable;
    }

    public void setJsonTimetable(String jsonTimetable) {
        this.jsonTimetable = jsonTimetable;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserCard)) {
            return false;
        }
        UserCard other = (UserCard) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserCard{" + "id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", description=" + description + ", diagnosis=" + diagnosis + ", userId=" + userId + ", birthDate=" + birthDate + ", aboutMe=" + aboutMe + ", sex=" + sex + ", height=" + height + ", weight=" + weight + ", age=" + age + '}';
    }
}
