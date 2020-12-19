package cn.edu.bjtu.vehiclemanagement.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cloud")
public class Cloud {
    String ip;
    String name;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
