package com.company;

import com.company.BlockModel.AbstractBlock;

import java.util.ArrayList;

public class Page
{
    private String name;
    private ArrayList<AbstractBlock> arrayBlocks;

    public Page(String name, ArrayList<AbstractBlock> arrayBlocks)
    {
        this.name = name;
        this.arrayBlocks = arrayBlocks;
    }
}
