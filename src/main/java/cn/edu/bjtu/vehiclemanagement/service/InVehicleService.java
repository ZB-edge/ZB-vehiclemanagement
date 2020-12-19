package cn.edu.bjtu.vehiclemanagement.service;

import cn.edu.bjtu.vehiclemanagement.entity.InVehicle;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public interface InVehicleService {
    void save(InVehicle inVehicle);
    List<InVehicle> findByInstitution(String institution);
    List<InVehicle> findInstitution(String institution);
    List<InVehicle> findAll();
    void updateCount(String license,int count,Date date) throws ParseException;
    void updateStatus(String license);
    List<InVehicle> findByInstitutionAndDate(String institution, Date date) throws ParseException;
    LinkedHashMap<String, Integer> sum(String institution) throws ParseException;
}
