package lj.justdeliver.model;

import java.io.Serializable;

/**
 * Created by lj on 2/21/2017.
 */

public class OneGig implements Serializable {
    public float charge;
    public String id;
    public String gigName;
    public String gigDesc;
    public User sender;
    public AddressModel senderLocation;
    public User receiver;
    public AddressModel deliverLocation;
    public String size;
    public String paymentType;
    public String deliveryDate;
    public String creatorID;
    public String gigImage;
    public String driverID = "no";
    public String deliveryStatus;

    public OneGig(String gigName, String gigDesc, User sender, AddressModel senderLocation, User receiver, AddressModel deliverLocation, String size, String paymentType, String deliveryDate, float charge, String creatorID) {
        this.gigName = gigName;
        this.gigDesc = gigDesc;
        this.sender = sender;
        this.senderLocation = senderLocation;
        this.receiver = receiver;
        this.deliverLocation = deliverLocation;
        this.size = size;
        this.paymentType = paymentType;
        this.deliveryDate = deliveryDate;
        this.charge = charge;
        this.creatorID = creatorID;
    }

    public OneGig() {

    }
}