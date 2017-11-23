package org.test.jms;

import java.io.Serializable;

public class Person implements Serializable{
    String name;
    String nic;
    int age;

    public Person(String name, String nic, int age){
        this.name = name;
        this.nic = nic;
        this.age = age;
    }

    @Override
    public String toString() {
        return name + " , " + nic + " , " + age;
    }
}
