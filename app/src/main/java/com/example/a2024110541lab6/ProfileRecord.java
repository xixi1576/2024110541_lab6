package com.example.a2024110541lab6;

import java.io.Serializable;

/**
 * 个人信息的历史记录，实现 {@link Serializable}，随个人信息一起保存到文件。
 */
public class ProfileRecord implements Serializable {

    private static final long serialVersionUID = 2024110542L;

    private final long time;
    private final String name;
    private final String studentId;
    private final String college;
    private final String major;

    public ProfileRecord(long time, String name, String studentId, String college, String major) {
        this.time = time;
        this.name = name;
        this.studentId = studentId;
        this.college = college;
        this.major = major;
    }

    public long getTime() {
        return time;
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
}
