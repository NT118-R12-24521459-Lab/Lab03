package com.example.bt2;

public final class Student {
    private final String id;
    private final String name;
    private final String major;

    public Student(String id, String name) {
        this(id, name, "");
    }

    public Student(String id, String name, String major) {
        this.id = id;
        this.name = name;
        this.major = major;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getMajor() { return major; }
}
