/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openjpa.kernel;

import org.apache.openjpa.lib.util.UUIDGenerator;
import org.apache.openjpa.meta.ClassMetaData;

/**
 * Sequence for generating 16-character UUID strings.
 *
 * @author Jeremy Bauer
 */
public class UUIDType4StringSeq
    implements Seq {

    private static final UUIDType4StringSeq _instance =
        new UUIDType4StringSeq();

    private String _last = null;

    /**
     * Return the singleton instance.
     */
    public static UUIDType4StringSeq getInstance() {
        return _instance;
    }

    /**
     * Hide constructor.
     */
    private UUIDType4StringSeq() {
    }

    public void setType(int type) {
    }

    public synchronized Object next(StoreContext ctx, ClassMetaData meta) {
        _last = UUIDGenerator.nextString(UUIDGenerator.TYPE4);
        return _last;
    }

    public synchronized Object current(StoreContext ctx, ClassMetaData meta) {
        return _last;
    }

    public void allocate(int additional, StoreContext ctx, ClassMetaData meta) {
    }

    public void close() {
	}
}
