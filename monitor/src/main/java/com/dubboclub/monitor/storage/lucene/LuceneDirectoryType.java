package com.dubboclub.monitor.storage.lucene;

/**
 * Created by bieber on 2015/9/25.
 */
public enum LuceneDirectoryType {
    
    RAM("ram"),MMAP("mmap"),NIOFS("niofs"),SIMPLE("simple");
    
    private String name;
    
    LuceneDirectoryType(String name){
        this.name = name;
    }
    
    public static LuceneDirectoryType typeOf(String name){
        LuceneDirectoryType[] luceneDirectoryType = LuceneDirectoryType.class.getEnumConstants();
        for(LuceneDirectoryType type:luceneDirectoryType){
            if(type.name.equals(name)){
                return type;
            }
        }
        return RAM;
    }
}
