package com.example.readery.utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InitService {
    private final boolean initDB_enabled;

    @Autowired
    InitializeDatabase initializeDatabase;

    @Autowired
    public InitService(@Value("${initDB.enabled}") boolean initDB_enabled){
        this.initDB_enabled = initDB_enabled;
    }

    public void setUp( ){
        if(initDB_enabled){
            System.out.println("---Start initialization of the database---");
             initializeDatabase.setUp();
        } else {
            System.out.println("---Database setup omitted---");
        }

    }

}
