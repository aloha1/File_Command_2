package com.bgsltd.filecommande.utils;

import java.util.LinkedHashMap;

/**
 * Created by Vishal on 21/12/15.
 */
public class MapEntry implements LinkedHashMap.Entry {

    private KeyMapEntry key;
    private Integer value;

    public MapEntry(KeyMapEntry key, Integer value) {
        this.key = key;
        this.value = value;
    }
    @Override
    public Object getKey() {
        return this.key;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public Object setValue(Object object) {
        // use constructor
        return null;
    }

    public static class KeyMapEntry implements LinkedHashMap.Entry {

        private Integer key, value;

        public KeyMapEntry(Integer key, Integer value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Object getKey() {
            return this.key;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public Object setValue(Object object) {
            // use constructor
            return null;
        }
    }
}
