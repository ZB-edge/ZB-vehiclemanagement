package cn.edu.bjtu.vehiclemanagement.service.Impl;

import cn.edu.bjtu.vehiclemanagement.entity.Cloud;
import cn.edu.bjtu.vehiclemanagement.service.CloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class CloudServiceImpl implements CloudService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public String findIp(String name) {
        Query query = Query.query(Criteria.where("name").is(name));
        Cloud cloud = mongoTemplate.findOne(query, Cloud.class,"cloud");
        String address = cloud.getIp();
        return address;
    }
}
