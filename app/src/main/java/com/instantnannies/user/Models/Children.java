package com.instantnannies.user.Models;

import java.io.Serializable;

public class Children implements Serializable {

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public Children(String name, String age) {
        Name = name;
        Age = age;
    }

    String Name;
    String Age;
}
