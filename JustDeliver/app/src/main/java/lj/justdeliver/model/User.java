package lj.justdeliver.model;

import java.io.Serializable;

/**
 * Created by lj on 2/20/2017.
 */

public class User implements Serializable {
    public String uid;
    public String profilePic;
    public String fullName, email, phone;
    public String asDriver; //true for driver or false for sender
    public Driver driver;

    public User() {
        asDriver = "false";
    }

    public User(String fullName, String email, String phone, String asDriver, String uid, String profilePic, Driver driver) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.asDriver = asDriver;
        this.uid = uid;
        this.profilePic = profilePic;
        this.driver = driver;
    }
}



