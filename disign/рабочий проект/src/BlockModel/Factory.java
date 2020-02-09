package com.company.BlockModel;

public class Factory
{
    public AbstractBlock getBlock(BlockType type)
    {
        AbstractBlock block = null;
        switch (type)
        {
            case list:
                return new ListBlock();
            case calendar:
                return new CalendarBlock();
        }
        return null;
    }


}
