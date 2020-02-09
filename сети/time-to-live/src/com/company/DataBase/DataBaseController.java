package com.company.DataBase;

import com.company.pageModel.Page;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseController {
    private String filePath;
    private JSONObject jsonObject;
    private FileWriter writer;
    private FileReader reader;

    private HashMap<String, Page> pagesMap;

    public DataBaseController(String filePath_, JSONObject jsonObject_, FileWriter writer_, FileReader reader_) throws IOException {
        this.filePath = filePath_;
        this.jsonObject = jsonObject_;
        this.writer = new FileWriter("C:\\Users\\Катерина\\Desktop\\programming\\сети\\time-to-live\\src\\example.json");
        this.reader = reader_;
    }

    public void addNewPage(Page page) {

        String namePage = page.getName();
        jsonObject.put("name", namePage);
        jsonObject.put("data", "");

        pagesMap.put(page.getName(), page);
        try {
            System.out.println(jsonObject.toJSONString());
            writer.write(jsonObject.toJSONString());
            writer.close();
        } catch (IOException var16) {
            var16.printStackTrace();
        }

    }

    public void addNewBlock(String type, String data) throws IOException {
        JSONArray list = new JSONArray();
        list.add(type);
        list.add(data);
        jsonObject.put("block", list);
        writer.write(jsonObject.toJSONString());
        writer.close();
    }

    public Object getPageInfo(String name){
        //Page page = pagesMap.get(name);
        return jsonObject.get("data");
    }
}
