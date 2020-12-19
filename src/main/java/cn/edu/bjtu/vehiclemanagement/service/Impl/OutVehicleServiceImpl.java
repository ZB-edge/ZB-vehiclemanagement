package cn.edu.bjtu.vehiclemanagement.service.Impl;

import cn.edu.bjtu.vehiclemanagement.entity.OutVehicle;
import cn.edu.bjtu.vehiclemanagement.service.OutVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class OutVehicleServiceImpl implements OutVehicleService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void save(OutVehicle outVehicle) {
        OutVehicle out = new OutVehicle();
        out.setLicense(outVehicle.getLicense());
        out.setDate(outVehicle.getDate());
        out.setInstitution(outVehicle.getInstitution());
        out.setCount(outVehicle.getCount());
        out.setStatus(0);
        mongoTemplate.save(out,"outvehicle");
    }

    @Override
    public List<OutVehicle> findByInstitution(String institution) {
        Query query = Query.query(Criteria.where("institution").is(institution));
        query.with(Sort.by(Sort.Order.desc("date"))).limit(20);
        return mongoTemplate.find(query, OutVehicle.class,"outvehicle");
    }

    @Override
    public List<OutVehicle> findInstitution(String institution) {
        Query query = Query.query(Criteria.where("institution").is(institution));
        return mongoTemplate.find(query, OutVehicle.class,"outvehicle");
    }

    @Override
    public List<OutVehicle> findAll() {
        return mongoTemplate.findAll(OutVehicle.class,"outvehicle");
    }

    @Override
    public void updateCount(String license, int count,Date date) throws ParseException {
        SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat ds =  new SimpleDateFormat("yyyy-MM-dd ");
        Date lastDay = df.parse(ds.format(date)+"23:59:59");
        Date firstDay = df.parse(ds.format(date)+"00:00:00");
        Query query = Query.query(Criteria.where("license").is(license).and("date").gte(firstDay).lte(lastDay));
        Update update = new Update();
        update.set("count",count);
        update.set("status",0);
        mongoTemplate.upsert(query,update,OutVehicle.class,"outvehicle");
    }

    @Override
    public void updateStatus(String license) {
        Query query = Query.query(Criteria.where("license").is(license));
        Update update = new Update();
        update.set("status",1);
        mongoTemplate.upsert(query,update,OutVehicle.class,"outvehicle");
    }

    @Override
    public List<OutVehicle> findByInstitutionAndDate(String institution,Date date) throws ParseException {
        SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat ds =  new SimpleDateFormat("yyyy-MM-dd ");
        Date lastDay = df.parse(ds.format(date)+"23:59:59");
        Date firstDay = df.parse(ds.format(date)+"00:00:00");
        Query query = Query.query(Criteria.where("institution").is(institution).and("date").gte(firstDay).lte(lastDay));
        return mongoTemplate.find(query, OutVehicle.class,"outvehicle");
    }

    public static Date getBeforeOrAfterDate(Date date, int num) {
        Calendar calendar = Calendar.getInstance();//获取日历
        calendar.setTime(date);//当date的值是当前时间，则可以不用写这段代码。
        calendar.add(Calendar.DATE, num);
        Date d = calendar.getTime();//把日历转换为Date
        return d;
    }

    @Override
    public LinkedHashMap<String, Integer> sum(String institution) throws ParseException {
        Date date = new Date();
        date = getBeforeOrAfterDate(date,-6);
        LinkedHashMap<String, Integer> tem = new LinkedHashMap<>();
        for (int i=0;i<7;i++){
            int sums = 0;
            List<OutVehicle> outVehicles = findByInstitutionAndDate(institution,date);
            for (OutVehicle outVehicle : outVehicles){
                sums += outVehicle.getCount();
            }
            SimpleDateFormat dss =  new SimpleDateFormat("yyyy-MM-dd");
            String key = dss.format(date);
            tem.put(key,sums);
            date = getBeforeOrAfterDate(date,i);
        }
        return tem;
    }
}
