/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.siddhi.core.annotation;

import io.siddhi.annotation.Extension;
import io.siddhi.annotation.Parameter;
import io.siddhi.annotation.ParameterOverload;
import io.siddhi.annotation.util.DataType;
import io.siddhi.core.executor.ConstantExpressionExecutor;
import io.siddhi.core.executor.ExpressionExecutor;
import io.siddhi.core.query.processor.stream.AbstractStreamProcessor;
import io.siddhi.query.api.exception.SiddhiAppValidationException;

/**
 * Validates the extension specific parameters of siddhi App with the patterns specified in the
 * {@literal @}ParameterOverload annotation in the extension class
 */
public class ProcessorAnnotationValidator {

    /**
     * The method which validates the extension specific parameters of siddhi App with the pattern specified in the
     * {@link ParameterOverload} annotation in the extension class
     *
     * @param s                             the object which extends AbstractStreamProcessor
     * @param attributeExpressionExecutors  the executors of each function parameters
     *
     * @throws SiddhiAppValidationException SiddhiAppValidation exception
     */
    public static void validateAnnotation(AbstractStreamProcessor s, ExpressionExecutor[] attributeExpressionExecutors)
            throws SiddhiAppValidationException {

        Extension annotation = s.getClass().getAnnotation(Extension.class);
        ParameterOverload[] parameterOverloads = annotation.parameterOverloads();
        Parameter[] parameters = annotation.parameters();
        //Count the maximum number of parameter names specified in @ParameterOverload
        int maxCount = 0;
        for (ParameterOverload parameterOverload : parameterOverloads) {
            String[] parameterNames = parameterOverload.parameterNames();
            if(parameterNames.length > maxCount) {
                maxCount = parameterNames.length;
            }
        }
        //Count the mandatory number of parameters specified in @Extension
        int mandatoryCount = 0;
        for (Parameter parameter : parameters) {
            if(!parameter.optional()) {
                mandatoryCount++;
            }
        }

        //Check if parameter Count is greater than or equal to the mandatory number of fields
        //and less than or equal to the maximum number of parameter names specified in @ParameterOverload
        if((attributeExpressionExecutors.length >= mandatoryCount) &&
                (attributeExpressionExecutors.length <= maxCount)) {

            //Check if dynamic properties of parameters match atleast one pattern specified in @ParameterOverload
            int m = -1;
            boolean atleastOne = false;
            boolean isCorrect;
            for (ParameterOverload parameterOverload : parameterOverloads) {
                isCorrect = true;
                String[] parameterNames = parameterOverload.parameterNames();
                if (parameterNames.length == attributeExpressionExecutors.length) {
                    for (String parameterName : parameterNames) {
                        if(isCorrect) {
                            m++;
                            for (Parameter parameter : parameters) {
                                String parameterName1 = parameter.name();
                                if (parameterName1.equals(parameterName)) {
                                    boolean dynamic = parameter.dynamic();
                                    boolean notDynamic = attributeExpressionExecutors[m]
                                            instanceof ConstantExpressionExecutor;
                                    isCorrect = (dynamic == !notDynamic);
                                }
                            }
                        }
                    }
                    m = -1;
                    if(isCorrect) { atleastOne = true; }
                }
            }
            if(!atleastOne){
                throw new SiddhiAppValidationException("Input parameters dynamic property doesn't match");
            }

            //Check if data types of parameters match atleast one pattern specified in @ParameterOverload
            int n = -1;
            boolean atleastOnce = false;
            boolean isCorrectOne;
            for (ParameterOverload parameterOverload : parameterOverloads) {
                isCorrectOne = true;
                String[] parameterNames = parameterOverload.parameterNames();
                if (parameterNames.length == attributeExpressionExecutors.length) {
                    for (String parameterName : parameterNames) {
                        if(isCorrectOne) {
                            n++;
                            boolean found = false;
                            for (Parameter parameter : parameters) {
                                String parameterName1 = parameter.name();
                                if (parameterName1.equals(parameterName)) {
                                    DataType[] types = parameter.type();
                                    for (DataType type : types) {
                                        if (attributeExpressionExecutors[n].getReturnType().toString().
                                                equals(type.toString())) {
                                            found = true;
                                        }
                                    }
                                }
                            }
                            isCorrectOne = (found);
                        }
                    }
                    n = -1;
                    if(isCorrectOne) { atleastOnce = true; }
                }

            }
            if(!atleastOnce){
                throw new SiddhiAppValidationException("Input parameters data type doesn't match");
            }

        } else {
            throw new SiddhiAppValidationException("Input should only have minimum of " + mandatoryCount + " and " +
                    "maximum of " + maxCount + " parameters, but found " + attributeExpressionExecutors.length +
                    " input attributes");
        }
    }
}
