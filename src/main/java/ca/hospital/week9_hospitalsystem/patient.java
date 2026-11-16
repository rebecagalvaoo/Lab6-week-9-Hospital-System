
package ca.hospital.week9_hospitalsystem;

import java.time.LocalDate;

public class patient {

    private int patientId;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String diagnosis;
    private LocalDate admissionDate;

    public patient(int patientId, String name, int age,
                   String gender, String phone,
                   String diagnosis, LocalDate admissionDate) {

        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.diagnosis = diagnosis;
        this.admissionDate = admissionDate;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }
}
