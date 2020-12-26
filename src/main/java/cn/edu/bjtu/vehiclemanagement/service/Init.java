package cn.edu.bjtu.vehiclemanagement.service;

import gnu.io.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class Init implements SerialPortEventListener {

    InputStream is;
    public static BlockingDeque<String> msgQueue = new LinkedBlockingDeque<String>();
    SerialPort serialPort;

    @Autowired
    RestTemplate restTemplate;

    public void openPort(String portName, int baudRate, int dataBits, int stopBits, int parity) {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            CommPort commPort = portIdentifier.open(portName, 4000);
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                try {
                    serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
                    serialPort.addEventListener(this);
                    serialPort.notifyOnDataAvailable(true);
                    is = serialPort.getInputStream();
                } catch (UnsupportedCommOperationException e) {
                    e.printStackTrace();
                }
                System.out.println("打开串口成功：" + portName);
            }
        } catch (NoSuchPortException e1) {
            System.out.println("打开串口失败：" + "没有找到" + portName + "串口!");
        } catch (PortInUseException e2) {
            System.out.println("打开串口失败：" + portName + "被占用!");
        } catch (Exception e3) {
            System.out.println("打开串口失败：" + portName + "未知异常!");
        }
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            System.out.println("来数据了");
            int count = 0;
            while (count==0){
                try {
                    count=is.available();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            byte[] buffer = new byte[count];
            try {
                is.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder str = new StringBuilder();
            for(int var7 = 0; var7 < buffer.length; ++var7) {
                str.append(String.valueOf(buffer[var7]));
            }
            System.out.println(str);
            msgQueue.add(str.toString());
        }
    }

}

