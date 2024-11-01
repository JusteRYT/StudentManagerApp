package model;

import java.util.Date;

/**
 * Класс, представляющий данные студента.
 */
public class Student {


    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Date birthDate;
    private String groupName;
    private String uniqueNumber;

    //Конструкторы, геттеры и сеттеры. Есть Lombok, но тут реализация как работает под капотом.
    public Student(int id, String firstName, String lastName, String patronymic, Date birthDate, String groupName, String uniqueNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.groupName = groupName;
        this.uniqueNumber = uniqueNumber;
    }
    public Student(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(String uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }




}
