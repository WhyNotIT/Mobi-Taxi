package com.whynotit.MobiTaxi.Models;

/**
 * Created by Harzallah on 29/11/2015.
 */
public class User {
    private String id;
    private String telephone;
    private String password;
    private String name;
    private String lastName;
    private String gender;
    private String birthday;
    private int level;

    public String getProfileImage() {
        return "http://graph.facebook.com/"+id+"/picture?type=large";
    }

    public User(String id,String telephone, String name, String lastName, String gender, String birthday) {
        this.id = id;
        this.telephone = telephone;
        this.name = name;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.level = 1;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "birthday='" + birthday + '\'' +
                ", telephone='" + telephone + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public String getReferralLink ()
    {
        return "http://whynotdev.com/getTheApp?id="+getId();
    }

    public String getPayment() {
        switch (level) {
            case 2: return "5$";
            case 3: return "10$";
            default: return "2$";
        }
    }

    public String getLevelToString() {
        return "Level: " + this.level;
    }
}
