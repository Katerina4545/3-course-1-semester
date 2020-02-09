package com.company;

import com.company.DataBase.DataBaseController;
import com.company.pageModel.Page;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Service {
    String filePath;
    DataBaseController dataBase;
    FileReader reader;
    FileWriter writer;

    public Service(String file) throws IOException, ParseException {
        this.filePath = file;
        JSONObject jsonObject = createJSON();
        this.dataBase = new DataBaseController(this.filePath, jsonObject, writer, reader);
    }

    private JSONObject createJSON() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        reader = null;
        writer = null;
        JSONObject obj= null;
        try {
            reader = new FileReader(filePath);
            obj = (JSONObject) parser.parse(reader);
            writer = new FileWriter(filePath);
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        return obj;
    }

    public void addNewPage(Page page) {
        dataBase.addNewPage(page);
    }
}
