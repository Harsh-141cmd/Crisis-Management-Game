package com.crisisgame.model;

public class PlayerProfile {
    private String name;
    private String gender;
    private int age;
    private int difficulty;

    public String getName() { return name; }
    public String getGender() { return gender; }
    public int getAge() { return age; }
    public int getDifficulty() { return difficulty; }

    public void setName(String name) { this.name = name; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAge(int age) { this.age = age; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
}