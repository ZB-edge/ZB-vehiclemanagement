package cn.edu.bjtu.vehiclemanagement.service;

import org.springframework.stereotype.Service;

@Service
public interface CloudService {
    String findIp(String name);
}
