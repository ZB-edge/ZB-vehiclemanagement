package cn.edu.bjtu.vehiclemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Order(1)
public class GetRridInfo implements ApplicationRunner {

    @Value("${RFID.port}")
    private String port;
    @Value("${RFID.bitRate}")
    private int bitRate;
    @Value("${RFID.stopBit}")
    private int stopBit;
    @Value("${RFID.dataBits}")
    private int dataBits;
    @Value("${RFID.parity}")
    private int parity;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Init init = new Init();
        init.openPort(port,bitRate,dataBits,stopBit,parity);
        try {
            System.out.println("--------------开始接收数据了-----------");
            while (true){
                if (init.msgQueue.size()>0){
                    String url = "http://localhost:8101/api/vehicle/add2";
                    MultiValueMap<String,Object> js = new LinkedMultiValueMap<>();
                    String j = init.msgQueue.take();
                    System.out.println("要发送了");
                    js.add("result",j);
                    try {
                        String result = restTemplate.postForObject(url,js,String.class);
                        System.out.println(result);
                    }catch (Exception e){
                        System.out.println(e);
                    }
                }
                Thread.sleep(5);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
