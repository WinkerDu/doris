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

import org.apache.doris.common.Config;
import org.apache.doris.nereids.analyzer.Unbound;
import org.apache.doris.nereids.exceptions.AnalysisException;
import org.apache.doris.nereids.trees.AbstractTreeNode;
import org.apache.doris.nereids.trees.expressions.functions.ExpressionTrait;
import org.apache.doris.nereids.trees.expressions.functions.Nondeterministic;
import org.apache.doris.nereids.trees.expressions.functions.agg.AggregateFunction;
import org.apache.doris.nereids.trees.expressions.literal.Literal;
import org.apache.doris.nereids.trees.expressions.literal.NullLiteral;
import org.apache.doris.nereids.trees.expressions.shape.LeafExpression;
import org.apache.doris.nereids.trees.expressions.typecoercion.ExpectsInputTypes;
import org.apache.doris.nereids.trees.expressions.typecoercion.TypeCheckResult;
import org.apache.doris.nereids.trees.expressions.visitor.ExpressionVisitor;
import org.apache.doris.nereids.types.ArrayType;
import org.apache.doris.nereids.types.DataType;
import org.apache.doris.nereids.types.MapType;
import org.apache.doris.nereids.types.StructField;
import org.apache.doris.nereids.types.StructType;
import org.apache.doris.nereids.types.coercion.AnyDataType;
import org.apache.doris.nereids.util.Utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Abstract class for all Expression in Nereids.
 */
public abstract class Expression extends AbstractTreeNode<Expression> implements ExpressionTrait {
    public static final String DEFAULT_EXPRESSION_NAME = "expression";
    // Mask this expression is generated by rule, should be removed.
    public boolean isGeneratedIsNotNull = false;
    protected Optional<String> exprName = Optional.empty();
    private final int depth;
    private final int width;
    // Mark this expression is from predicate infer or something else infer
    private final boolean inferred;

    protected Expression(Expression... children) {
        super(children);
        depth = Arrays.stream(children)
                .mapToInt(e -> e.depth)
                .max().orElse(0) + 1;
        width = Arrays.stream(children)
                .mapToInt(e -> e.width)
                .sum() + (children.length == 0 ? 1 : 0);
        checkLimit();
        this.inferred = false;
    }

    protected Expression(List<Expression> children) {
        super(children);
        depth = children.stream()
                .mapToInt(e -> e.depth)
                .max().orElse(0) + 1;
        width = children.stream()
                .mapToInt(e -> e.width)
                .sum() + (children.isEmpty() ? 1 : 0);
        checkLimit();
        this.inferred = false;
    }

    protected Expression(List<Expression> children, boolean inferred) {
        super(children);
        depth = children.stream()
                .mapToInt(e -> e.depth)
                .max().orElse(0) + 1;
        width = children.stream()
                .mapToInt(e -> e.width)
                .sum() + (children.isEmpty() ? 1 : 0);
        checkLimit();
        this.inferred = inferred;
    }

    private void checkLimit() {
        if (depth > Config.expr_depth_limit) {
            throw new AnalysisException(String.format("Exceeded the maximum depth of an "
                    + "expression tree (%s).", Config.expr_depth_limit));
        }
        if (width > Config.expr_children_limit) {
            throw new AnalysisException(String.format("Exceeded the maximum children of an "
                    + "expression tree (%s).", Config.expr_children_limit));
        }
    }

    public Alias alias(String alias) {
        return new Alias(this, alias);
    }

    // Name of expr, this is used by generating column name automatically when there is no
    // alias
    public String getExpressionName() {
        if (!this.exprName.isPresent()) {
            this.exprName = Optional.of(Utils.normalizeName(this.getClass().getSimpleName(), DEFAULT_EXPRESSION_NAME));
        }
        return this.exprName.get();
    }

    /**
     * check input data types
     */
    public TypeCheckResult checkInputDataTypes() {
        // check all of its children recursively.
        for (Expression expression : this.children) {
            TypeCheckResult childResult = expression.checkInputDataTypes();
            if (childResult.failed()) {
                return childResult;
            }
        }
        if (this instanceof ExpectsInputTypes) {
            ExpectsInputTypes expectsInputTypes = (ExpectsInputTypes) this;
            TypeCheckResult commonCheckResult = checkInputDataTypesWithExpectTypes(
                    children, expectsInputTypes.expectedInputTypes());
            if (commonCheckResult.failed()) {
                return commonCheckResult;
            }
        }
        return checkInputDataTypesInternal();
    }

    protected TypeCheckResult checkInputDataTypesInternal() {
        return TypeCheckResult.SUCCESS;
    }

    private boolean checkInputDataTypesWithExpectType(DataType input, DataType expected) {
        if (input instanceof ArrayType && expected instanceof ArrayType) {
            return checkInputDataTypesWithExpectType(
                    ((ArrayType) input).getItemType(), ((ArrayType) expected).getItemType());
        } else if (input instanceof MapType && expected instanceof MapType) {
            return checkInputDataTypesWithExpectType(
                    ((MapType) input).getKeyType(), ((MapType) expected).getKeyType())
                    && checkInputDataTypesWithExpectType(
                    ((MapType) input).getValueType(), ((MapType) expected).getValueType());
        } else if (input instanceof StructType && expected instanceof StructType) {
            List<StructField> inputFields = ((StructType) input).getFields();
            List<StructField> expectedFields = ((StructType) expected).getFields();
            if (inputFields.size() != expectedFields.size()) {
                return false;
            }
            for (int i = 0; i < inputFields.size(); i++) {
                if (!checkInputDataTypesWithExpectType(
                        inputFields.get(i).getDataType(),
                        expectedFields.get(i).getDataType())) {
                    return false;
                }
            }
            return true;
        } else {
            return checkPrimitiveInputDataTypesWithExpectType(input, expected);
        }
    }

    private boolean checkPrimitiveInputDataTypesWithExpectType(DataType input, DataType expected) {
        // These type will throw exception when invoke toCatalogDataType()
        if (expected instanceof AnyDataType) {
            return expected.acceptsType(input);
        }
        // TODO: complete the cast logic like FunctionCallExpr.analyzeImpl
        boolean legacyCastCompatible = false;
        try {
            legacyCastCompatible = input.toCatalogDataType().matchesType(expected.toCatalogDataType());
        } catch (Throwable t) {
            // ignore.
        }
        if (!legacyCastCompatible && !expected.acceptsType(input)) {
            return false;
        }
        return true;
    }

    private TypeCheckResult checkInputDataTypesWithExpectTypes(
            List<Expression> inputs, List<DataType> expectedTypes) {
        Preconditions.checkArgument(inputs.size() == expectedTypes.size());
        List<String> errorMessages = Lists.newArrayList();
        for (int i = 0; i < inputs.size(); i++) {
            Expression input = inputs.get(i);
            DataType expected = expectedTypes.get(i);
            if (!checkInputDataTypesWithExpectType(input.getDataType(), expected)) {
                errorMessages.add(String.format("argument %d requires %s type, however '%s' is of %s type",
                        i + 1, expected.simpleString(), input.toSql(), input.getDataType().simpleString()));
            }
        }
        if (!errorMessages.isEmpty()) {
            return new TypeCheckResult(false, StringUtils.join(errorMessages, ", "));
        }
        return TypeCheckResult.SUCCESS;
    }

    public abstract <R, C> R accept(ExpressionVisitor<R, C> visitor, C context);

    @Override
    public List<Expression> children() {
        return children;
    }

    @Override
    public Expression child(int index) {
        return children.get(index);
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isInferred() {
        return inferred;
    }

    @Override
    public Expression withChildren(List<Expression> children) {
        throw new RuntimeException();
    }

    public Expression withInferred(boolean inferred) {
        throw new RuntimeException("current expression has not impl the withInferred method");
    }

    /**
     * Whether the expression is a constant.
     */
    public boolean isConstant() {
        if (this instanceof AggregateFunction) {
            // agg_fun(literal) is not constant, the result depends on the group by keys
            return false;
        }
        if (this instanceof LeafExpression) {
            return this instanceof Literal;
        } else {
            return !(this instanceof Nondeterministic) && children().stream().allMatch(Expression::isConstant);
        }
    }

    public final Expression castTo(DataType targetType) throws AnalysisException {
        return uncheckedCastTo(targetType);
    }

    public Expression checkedCastTo(DataType targetType) throws AnalysisException {
        return castTo(targetType);
    }

    protected Expression uncheckedCastTo(DataType targetType) throws AnalysisException {
        throw new RuntimeException("Do not implement uncheckedCastTo");
    }

    /**
     * Get all the input slots of the expression.
     * <p>
     * Note that the input slots of subquery's inner plan is not included.
     */
    public final Set<Slot> getInputSlots() {
        return collect(Slot.class::isInstance);
    }

    /**
     * Get all the input slot ids of the expression.
     * <p>
     * Note that the input slots of subquery's inner plan is not included.
     */
    public final Set<ExprId> getInputSlotExprIds() {
        ImmutableSet.Builder<ExprId> result = ImmutableSet.builder();
        foreach(node -> {
            if (node instanceof Slot) {
                result.add(((Slot) node).getExprId());
            }
        });
        return result.build();
    }

    public boolean isLiteral() {
        return this instanceof Literal;
    }

    public boolean isNullLiteral() {
        return this instanceof NullLiteral;
    }

    public boolean isSlot() {
        return this instanceof Slot;
    }

    public boolean isColumnFromTable() {
        return (this instanceof SlotReference) && ((SlotReference) this).getColumn().isPresent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Expression that = (Expression) o;
        return Objects.equals(children(), that.children());
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * This expression has unbound symbols or not.
     */
    public boolean hasUnbound() {
        if (this instanceof Unbound) {
            return true;
        }
        for (Expression child : children) {
            if (child.hasUnbound()) {
                return true;
            }
        }
        return false;
    }

    public String shapeInfo() {
        return toSql();
    }
}
