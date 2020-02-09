package com.company.BlockModel;

import java.util.List;

public class ListBlock extends AbstractBlock
{
    private List<String> data;

    public ListBlock()
    {

    }

    public void setData(String string)
    {
        data.add(string);
    }
    public void setAll(List<String> data)
    {
        this.data.addAll(data);
    }
}
