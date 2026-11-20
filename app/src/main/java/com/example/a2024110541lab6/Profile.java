package com.example.a2024110541lab6;

import java.io.Serializable;

/**
 * 个人信息对象，实现 {@link Serializable} 接口后可以直接用对象流保存到文件中。
 */
public class Profile implements Serializable {

    private static final long serialVersionUID = 2024110541L;

    private String name;
    private String studentId;
    private String college;
    private String major;
    private String hobby;
    private long savedAt;

    public Profile(String name, String studentId, String college, String major, String hobby) {
        this.name = name;
        this.studentId = studentId;
        this.college = college;
        this.major = major;
        this.hobby = hobby;
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCollege() {
        return college;
    }

    public String getMajor() {
        return major;
    }

    public String getHobby() {
        return hobby;
    }

    public long getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(long savedAt) {
        this.savedAt = savedAt;
    }
}
