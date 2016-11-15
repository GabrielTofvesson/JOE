package com.tofvesson.joe;

import java.io.File;
import java.util.HashMap;

public class Dataset<T> {

    HashMap<String, T> data = new HashMap<>();
    private final Type type;

    public Dataset(Type type, File input){
        this.type = type;
    }
}
