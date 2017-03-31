package lj.justdeliver.model;

import java.io.Serializable;

/**
 * Created by lj on 2/22/2017.
 */

public class AddressModel implements Serializable{
    public String addressName;
    public String address;
    public String lat;
    public String lng;

    public AddressModel() {
    }

    public AddressModel(String addressName, String address, String lat, String lng) {
        this.addressName = addressName;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }
}
