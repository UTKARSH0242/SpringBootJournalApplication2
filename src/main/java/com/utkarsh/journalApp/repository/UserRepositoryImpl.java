package com.utkarsh.journalApp.repository;

import com.utkarsh.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepositoryImpl {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> getUserforSA(){
         Query query = new Query();
//         query.addCriteria(Criteria.where("email").exists(true));
         query.addCriteria(Criteria.where("email").ne(null).ne(""));
         query.addCriteria(Criteria.where("sentimentAnalysis").is(true));
        List<User> users = mongoTemplate.find(query, User.class);
        return users;
    }
}
