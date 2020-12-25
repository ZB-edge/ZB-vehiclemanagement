package cn.edu.bjtu.vehiclemanagement.controller;

import cn.edu.bjtu.vehiclemanagement.entity.InVehicle;
import cn.edu.bjtu.vehiclemanagement.entity.OutVehicle;
import cn.edu.bjtu.vehiclemanagement.service.CloudService;
import cn.edu.bjtu.vehiclemanagement.service.InVehicleService;
import cn.edu.bjtu.vehiclemanagement.service.OutVehicleService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@RequestMapping("/api/vehicle")
@RestController
public class PerceptionController {

    @Autowired
    InVehicleService inVehicleService;
    @Autowired
    OutVehicleService outVehicleService;
    @Autowired
    CloudService cloudService;
    @Autowired
    RestTemplate restTemplate;

    @CrossOrigin
    @PostMapping("/test1")
    public String save1(@RequestParam(value = "license") String license, @RequestParam(value = "institution") String institution,
                        @RequestParam(value = "date") String date, @RequestParam(value = "count") int count,
                        @RequestParam(value = "status") int status) throws ParseException {
        InVehicle inVehicle = new InVehicle();
        SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inVehicle.setLicense(license);
        inVehicle.setDate(df.parse(date));
        inVehicle.setInstitution(institution);
        inVehicle.setCount(count);
        inVehicle.setStatus(status);
        inVehicleService.save(inVehicle);
        return "成功！";
    }

    @CrossOrigin
    @PostMapping("/test2")
    public String save2(@RequestParam(value = "license") String license, @RequestParam(value = "institution") String institution,
                        @RequestParam(value = "date") String date, @RequestParam(value = "count") int count,
                        @RequestParam(value = "status") int status) throws ParseException {
        OutVehicle outVehicle = new OutVehicle();
        SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        outVehicle.setLicense(license);
        outVehicle.setDate(df.parse(date));
        outVehicle.setInstitution(institution);
        outVehicle.setCount(count);
        outVehicle.setStatus(status);
        outVehicleService.save(outVehicle);
        return "成功！";
    }

    @CrossOrigin
    @PostMapping("/add1")
    public String add1(@RequestParam(value = "result") String result) throws ParseException {
        if (result.equals("8270200-1280168014112-300029-119311012032-114-9018")){
            inVehicleService.updateCount("08式步战车-1-1","装甲兵1旅",new Date());
            return "成功！";
        }
        return "查无此车辆！";
    }

    @CrossOrigin
    @PostMapping("/add2")
    public String add2(@RequestParam(value = "result") String result) throws ParseException {
        if (result.equals("8270200-1280168014112-300029-119311012032-114-9018")){
            outVehicleService.updateCount("08式步战车-1-1","装甲兵1旅",new Date());
            return "成功！";
        }
        return "查无此车辆！";
    }

    @CrossOrigin
    @GetMapping("/list/{institution}")
    public JSONArray list(@PathVariable String institution){
        JSONArray result = new JSONArray();
        List<InVehicle> inVehicles = inVehicleService.findByInstitution(institution);
        List<OutVehicle> outVehicles = outVehicleService.findByInstitution(institution);
        result.add(0,inVehicles);
        result.add(1,outVehicles);
        return result;
    }

    @CrossOrigin
    @GetMapping("/sum/{institution}")
    public JSONObject sum(@PathVariable String institution) throws ParseException {
        JSONObject js = new JSONObject();
        LinkedHashMap<String, Integer> in;
        LinkedHashMap<String, Integer> out;
        in = inVehicleService.sum(institution);
        out = outVehicleService.sum(institution);
        js.put("车辆驶入情况",in);
        js.put("车辆驶出情况",out);
        return js;
    }

    @CrossOrigin
    @PostMapping("/exportIn/{institution}")
    public String exportIn(@PathVariable String institution) throws ParseException {
        List<InVehicle> inVehicles = inVehicleService.findInstitution(institution);
        String ip = cloudService.findIp("cloud");
        String url = "http://" + ip + ":8101/api/vehicle/test1";
        SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(InVehicle inVehicle : inVehicles){
            if (inVehicle.getStatus()==0){
                MultiValueMap<String,Object> js = new LinkedMultiValueMap<>();
                inVehicleService.updateStatus(inVehicle.getLicense(),inVehicle.getDate());
                js.add("license",inVehicle.getLicense());
                js.add("institution",inVehicle.getInstitution());
                js.add("date",df.format(inVehicle.getDate()));
                js.add("count",inVehicle.getCount());
                js.add("status",1);
                try{
                    restTemplate.postForObject(url,js,String.class);
                }catch(Exception ignored){}
            }
        }
        return "导出成功！";
    }

    @CrossOrigin
    @PostMapping("/exportOut/{institution}")
    public String exportOut(@PathVariable String institution) throws ParseException {
        List<OutVehicle> outVehicles = outVehicleService.findInstitution(institution);
        String ip = cloudService.findIp("cloud");
        String url = "http://" + ip + ":8101/api/vehicle/test2";
        SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(OutVehicle outVehicle : outVehicles){
            if (outVehicle.getStatus()==0){
                outVehicleService.updateStatus(outVehicle.getLicense(),outVehicle.getDate());
                MultiValueMap<String,Object> js = new LinkedMultiValueMap<>();
                js.add("license",outVehicle.getLicense());
                js.add("institution",outVehicle.getInstitution());
                js.add("date",df.format(outVehicle.getDate()));
                js.add("count",outVehicle.getCount());
                js.add("status",1);
                try{
                    restTemplate.postForObject(url,js,String.class);
                }catch(Exception ignored){}
            }
        }
        return "导出成功！";
    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
