// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.trees.expressions;

import org.apache.doris.nereids.trees.expressions.shape.LeafExpression;

import java.util.List;

/**
 * Abstract class for all slot in expression.
 */
public abstract class Slot extends NamedExpression implements LeafExpression {

    @Override
    public Slot toSlot() {
        return this;
    }

    public Slot withNullable(boolean newNullable) {
        throw new RuntimeException("Do not implement");
    }

    public Slot withQualifier(List<String> qualifier) {
        throw new RuntimeException("Do not implement");
    }

    public Slot withName(String name) {
        throw new RuntimeException("Do not implement");
    }

    public Slot withExprId(ExprId exprId) {
        throw new RuntimeException("Do not implement");
    }
}
