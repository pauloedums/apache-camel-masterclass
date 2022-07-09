package com.decodedbytes.beans;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "NAME_ADDRESS")
@Data
@NamedQuery(name="fetchAllRows", query = "Select x from NameAddress x")
public class NameAddress implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "house_number")
    private String houseNumber;
    private String city;
    private String province;
    @Column(name = "postal_code")
    private String postalCode;

}
