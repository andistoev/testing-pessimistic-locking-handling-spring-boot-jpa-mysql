package com.andistoev.psmlockingservice;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity
public class Item {

    @Id
    @Column(length = 16)
    private UUID id = UUID.randomUUID();

    private int amount = 0;
}
