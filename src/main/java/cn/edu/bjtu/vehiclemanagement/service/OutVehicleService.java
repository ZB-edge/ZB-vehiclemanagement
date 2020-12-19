package cn.edu.bjtu.vehiclemanagement.service;

import cn.edu.bjtu.vehiclemanagement.entity.OutVehicle;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public interface OutVehicleService {
    void save(OutVehicle outVehicle);
    List<OutVehicle> findByInstitution(String institution);
    List<OutVehicle> findInstitution(String institution);
    List<OutVehicle> findAll();
    void updateCount(String license,int count,Date date) throws ParseException;
    void updateStatus(String license);
    List<OutVehicle> findByInstitutionAndDate(String institution, Date date) throws ParseException;
    LinkedHashMap<String, Integer> sum(String institution) throws ParseException;
}
