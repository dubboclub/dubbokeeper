package com.dubboclub.dk.storage.lucene;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

/**
 * Created by bieber on 2015/10/7.
 */
public class StoredAndSortStringField extends Field {

    static  FieldType TYPE_STORED_SORT = new FieldType();
    static {
        TYPE_STORED_SORT.setOmitNorms(true);
        TYPE_STORED_SORT.setIndexOptions(IndexOptions.DOCS);
        TYPE_STORED_SORT.setStored(true);
        TYPE_STORED_SORT.setTokenized(false);
        TYPE_STORED_SORT.setDocValuesType(DocValuesType.SORTED);
        TYPE_STORED_SORT.freeze();
    }

    public StoredAndSortStringField(String name, String value) {
        super(name, new BytesRef(value), TYPE_STORED_SORT);
    }
}
