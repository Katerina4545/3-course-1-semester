package com.company.View;

import com.company.Service;
import com.company.View.PageGUI.PagePainter;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class MainGui
{
    private PagePainter pagePainter;

    public MainGui()
    {
       // pagePainter = new PagePainter();
        //создание сервиса (связь контроллера и модели)
        pagePainter = new PagePainter();

    }
}
