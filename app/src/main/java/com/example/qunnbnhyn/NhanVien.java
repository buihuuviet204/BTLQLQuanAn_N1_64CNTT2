package com.example.qunnbnhyn;

public class NhanVien {
    private String maNhanVien;
    private String name;
    private String birthDate;
    private String gender;
    private String email;
    private String phone;
    private String hometown;
    private String position;
    private String password;
    private String avatarBase64;

    public NhanVien() {
    }

    public NhanVien(String maNhanVien, String name, String birthDate, String gender, String email,
                    String phone, String hometown, String position, String password, String avatarBase64) {
        this.maNhanVien = maNhanVien;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.hometown = hometown;
        this.position = position;
        this.password = password;
        this.avatarBase64 = avatarBase64;
    }

    // Getters and Setters
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getHometown() { return hometown; }
    public void setHometown(String hometown) { this.hometown = hometown; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAvatarBase64() { return avatarBase64; }
    public void setAvatarBase64(String avatarBase64) { this.avatarBase64 = avatarBase64; }
}