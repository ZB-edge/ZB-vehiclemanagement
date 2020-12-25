package cn.edu.bjtu.vehiclemanagement.service.Impl;

import cn.edu.bjtu.vehiclemanagement.entity.InVehicle;
import cn.edu.bjtu.vehiclemanagement.service.InVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class InVehicleServiceImpl implements InVehicleService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void save(InVehicle inVehicle) {
        InVehicle in = new InVehicle();
        in.setLicense(inVehicle.getLicense());
        in.setDate(inVehicle.getDate());
        in.setInstitution(inVehicle.getInstitution());
        in.setCount(inVehicle.getCount());
        in.setStatus(0);
        mongoTemplate.save(in,"invehicle");
    }

    @Override
    public List<InVehicle> findByInstitution(String institution) {
        Query query = Query.query(Criteria.where("institution").is(institution));
        query.with(Sort.by(Sort.Order.desc("date"))).limit(20);
        return mongoTemplate.find(query, InVehicle.class,"invehicle");
    }

    @Override
    public List<InVehicle> findInstitution(String institution) {
        Query query = Query.query(Criteria.where("institution").is(institution));
        return mongoTemplate.find(query, InVehicle.class,"invehicle");
    }

    @Override
    public List<InVehicle> findAll() {
        return mongoTemplate.findAll(InVehicle.class,"invehicle");
    }

    @Override
    public void updateCount(String license,String institution,Date date) throws ParseException {
        SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat ds =  new SimpleDateFormat("yyyy-MM-dd ");
        Date lastDay = df.parse(ds.format(date)+"23:59:59");
        Date firstDay = df.parse(ds.format(date)+"00:00:00");
        Query query = Query.query(Criteria.where("license").is(license).and("date").gte(firstDay).lte(lastDay));
        List<InVehicle> inVehicles = mongoTemplate.find(query,InVehicle.class,"invehicle");
        if(inVehicles.equals(new LinkedList<>())){
            InVehicle in = new InVehicle();
            in.setLicense(license);
            in.setDate(date);
            in.setInstitution(institution);
            in.setCount(1);
            in.setStatus(0);
            save(in);
        }else {
            for (InVehicle inVehicle:inVehicles){
                Update update = new Update();
                update.set("count",inVehicle.getCount()+1);
                update.set("status",0);
                update.set("date",date);
                mongoTemplate.upsert(query,update,InVehicle.class,"invehicle");
            }
        }
    }

    @Override
    public void updateStatus(String license,Date date) throws ParseException {
        SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat ds =  new SimpleDateFormat("yyyy-MM-dd ");
        Date lastDay = df.parse(ds.format(date)+"23:59:59");
        Date firstDay = df.parse(ds.format(date)+"00:00:00");
        Query query = Query.query(Criteria.where("license").is(license).and("date").gte(firstDay).lte(lastDay));
        Update update = new Update();
        update.set("status",1);
        mongoTemplate.upsert(query,update,InVehicle.class,"invehicle");
    }

    @Override
    public List<InVehicle> findByInstitutionAndDate(String institution,Date date) throws ParseException {
        SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat ds =  new SimpleDateFormat("yyyy-MM-dd ");
        Date lastDay = df.parse(ds.format(date)+"23:59:59");
        Date firstDay = df.parse(ds.format(date)+"00:00:00");
        Query query = Query.query(Criteria.where("institution").is(institution).and("date").gte(firstDay).lte(lastDay));
        return mongoTemplate.find(query, InVehicle.class,"invehicle");
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
        Date date = getBeforeOrAfterDate(new Date(),-6);
        LinkedHashMap<String, Integer> tem = new LinkedHashMap<>();
        for (int i=0;i<7;i++){
            int sums = 0;
            List<InVehicle> inVehicles = findByInstitutionAndDate(institution,date);
            for (InVehicle inVehicle : inVehicles){
                sums += inVehicle.getCount();
            }
            SimpleDateFormat dss =  new SimpleDateFormat("yyyy-MM-dd");
            String key = dss.format(date);
            tem.put(key,sums);
            date = getBeforeOrAfterDate(date,1);
        }
        return tem;
    }
}
