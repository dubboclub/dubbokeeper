package com.dubboclub.dk.storage.lucene;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;

/**
 * Created by bieber on 2015/10/7.
 */
public class StoredAndSortNumericField extends Field {

    static  FieldType TYPE_STORED_SORT = new FieldType();
    static {
        TYPE_STORED_SORT.setOmitNorms(true);
        TYPE_STORED_SORT.setIndexOptions(IndexOptions.DOCS);
        TYPE_STORED_SORT.setStored(true);
        TYPE_STORED_SORT.setTokenized(false);
        TYPE_STORED_SORT.setDocValuesType(DocValuesType.NUMERIC);
        TYPE_STORED_SORT.freeze();
    }

    public StoredAndSortNumericField(String name, long value) {
        super(name, TYPE_STORED_SORT);
        fieldsData = Long.valueOf(value);
    }

    public StoredAndSortNumericField(String name,int value){
        super(name, TYPE_STORED_SORT);
        fieldsData = Integer.valueOf(value);
    }
    public StoredAndSortNumericField(String name,double value){
        super(name, TYPE_STORED_SORT);
        fieldsData = Double.valueOf(value);
    }
}
