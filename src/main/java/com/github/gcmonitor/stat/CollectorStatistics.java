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

package com.github.gcmonitor.stat;

import java.util.Arrays;

public class CollectorStatistics implements PrettyPrintable {

    private final CollectorWindow[] windows;

    CollectorStatistics(CollectorWindow[] windows) {
        this.windows = windows;
    }

    public CollectorWindow[] getWindows() {
        return windows;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CollectorStatistics{");
        sb.append("windows=").append(Arrays.toString(windows));
        sb.append('}');
        return sb.toString();
    }

    @Override
    public void printItself(StringBuilder builder, String indent) {
        builder.append(indent + "CollectorStatistics{");
        for (CollectorWindow window : windows) {
            builder.append("\n");
            window.printItself(builder, indent + "\t");
        }
        builder.append("\n" + indent + "}");
    }

}
