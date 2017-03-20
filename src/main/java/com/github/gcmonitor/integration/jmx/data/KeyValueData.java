/*
 *  Copyright 2017 Vladimir Bukhtoyarov
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.gcmonitor.integration.jmx.data;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class KeyValueData implements CompositeData, Serializable {

    private final Map<String, Object> values;
    private final CompositeType type;

    public KeyValueData(Map<String, Object> values, CompositeType type) {
        this.values = values;
        this.type = type;
    }

    @Override
    public CompositeType getCompositeType() {
        return type;
    }

    @Override
    public Object get(String key) {
        return values.get(key);
    }

    @Override
    public Object[] getAll(String[] keys) {
        Object[] values = new Object[keys.length];
        for (int i = 0; i < keys.length; i++) {
            values[i] = this.values.get(keys[i]);
        }
        return values;
    }

    @Override
    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public Collection<?> values() {
        return values.values();
    }

}
