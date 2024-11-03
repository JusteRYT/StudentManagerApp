package model;

import java.sql.Date;

/**
 * Класс, представляющий данные студента.
 */
public class Student {

    private int id; // Уникальный идентификатор студента
    private String firstName; // Имя студента
    private String lastName; // Фамилия студента
    private String patronymic; // Отчество студента
    private String birthDate; // Дата рождения студента в формате YYYY-MM-DD
    private String groupName; // Название группы студента
    private String uniqueNumber; // Уникальный номер студента

    //Конструктор, геттеры и сеттеры. Есть Lombok, но тут реализация как работает под капотом.
    // Конструктор по умолчанию
    public Student() {}


    /**
     * Конструктор для создания объекта Student с уникальным номером и данными о студенте.
     *
     * @param uniqueNumber Уникальный номер студента.
     * @param firstName Имя студента.
     * @param lastName Фамилия студента.
     * @param patronymic Отчество студента.
     * @param birthDate Дата рождения студента в формате YYYY-MM-DD.
     * @param groupName Название группы студента.
     */
    public Student(String uniqueNumber, String firstName, String lastName, String patronymic, String birthDate, String groupName) {
        this.uniqueNumber = uniqueNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.groupName = groupName;
    }

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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
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
