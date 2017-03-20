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

package com.github.gcmonitor;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Snapshot;


public class ReadOnlyHistogram extends Histogram {

    private final Histogram target;

    public ReadOnlyHistogram(Histogram target) {
        super(null);
        this.target = target;
    }

    @Override
    public void update(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getCount() {
        return target.getCount();
    }

    @Override
    public Snapshot getSnapshot() {
        return target.getSnapshot();
    }
}
