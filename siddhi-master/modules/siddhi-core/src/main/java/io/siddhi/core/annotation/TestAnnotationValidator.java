package io.siddhi.core.annotation;

import io.siddhi.annotation.Extension;
import io.siddhi.annotation.Parameter;
import io.siddhi.annotation.ParameterOverload;
import io.siddhi.annotation.util.DataType;
import io.siddhi.core.executor.ConstantExpressionExecutor;
import io.siddhi.core.executor.ExpressionExecutor;
import io.siddhi.core.executor.VariableExpressionExecutor;
import io.siddhi.core.query.processor.stream.AbstractStreamProcessor;
import io.siddhi.query.api.definition.Attribute;
import io.siddhi.query.api.exception.SiddhiAppValidationException;

public class TestAnnotationValidator {
    public static void validateAnnotation(AbstractStreamProcessor s, ExpressionExecutor[] attributeExpressionExecutors) throws SiddhiAppValidationException {

//        TimeWindowProcessor tWP = new TimeWindowProcessor();
//        Extension annotation = tWP.getClass().getAnnotation(Extension.class);
//        ParameterOverload1[] parameterOverloads = annotation.parameterOverloads1();
//        int count = 0;
//        for (ParameterOverload1 parameterOverload : parameterOverloads) {
//            count++;
//        }
//
//        if(count == attributeExpressionExecutors.length) {
//            for (ParameterOverload1 parameterOverload : parameterOverloads) {
//                Parameter[] parameters = parameterOverload.parameters();
//                for (Parameter parameter : parameters) {
//                    for (DataType dt : parameter.type()) {
//                        if (dt.equals(attributeExpressionExecutors[0].getReturnType())) {
//
//                        }
//                    }
//                }meter
//            }
//        } else {
//            throw new SiddhiAppValidationException("Time window should have constant parameter attribute but " +
//                    "found a dynamic attribute " + attributeExpressionExecutors[0].getClass().getCanonicalName());
//        }
//
//        for (ParameterOverload1 parameterOverload : parameterOverloads) {
//            Parameter[] parameters = parameterOverload.parameters();
//            for (Parameter parameter : parameters) {
//
//            }
//        }

//        TimeWindowProcessor tWP = new TimeWindowProcessor();
        Extension annotation = s.getClass().getAnnotation(Extension.class);
        ParameterOverload[] parameterOverloads1 = annotation.parameterOverloads();
        Parameter[] parameters = annotation.parameters();
        int maxCount = 0;
        int count = 0;
        for (ParameterOverload parameterOverload : parameterOverloads1) {
            String[] parameterNames = parameterOverload.parameterNames();
            count = parameterNames.length;
//            for (String  parameterName : parameterNames) {
//                count++;
//            }
            if(count > maxCount) {
                maxCount = count;
            }
            count = 0;
        }
        int mandatoryCount = 0;
        for (Parameter parameter : parameters) {
            if(!parameter.optional()) {
                mandatoryCount++;
            }
        }

//        int i = -1;
        if((attributeExpressionExecutors.length >= mandatoryCount) && (attributeExpressionExecutors.length <= maxCount)) {

//            int m = -1;
//            boolean atleastOne = false;
//            boolean isCorrect;
//            for (ParameterOverload parameterOverload : parameterOverloads1) {
//                isCorrect = true;
//                String[] parameterNames = parameterOverload.parameterNames();
//                if (parameterNames.length == attributeExpressionExecutors.length) {
//                    for (String parameterName : parameterNames) {
//                        if(isCorrect) {
//                            m++;
//                            for (Parameter parameter : parameters) {
//                                String parameterName1 = parameter.name();
//                                if (parameterName1.equals(parameterName)) {
//                                    boolean dynamic = parameter.dynamic();
//                                    boolean adynamic = attributeExpressionExecutors[m] instanceof ConstantExpressionExecutor;
//                                    if (dynamic == !adynamic) {
//                                        isCorrect = true;
//                                    } else {
//                                        isCorrect = false;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    m = -1;
//                    if(isCorrect) { atleastOne = true; }
//                }
//
//            }
//            if(!atleastOne){
//                throw new SiddhiAppValidationException("Dynamic Constant doesn't match");
//            }


            if (!(attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor)) {
                throw new SiddhiAppValidationException("(1st) parameter should be a constant but found a dynamic " +
                        "attribute " + attributeExpressionExecutors[0].getClass().getCanonicalName());
            }
            for (int i = 1, parametersLength = attributeExpressionExecutors.length; i < parametersLength; i++) {
                if (!(attributeExpressionExecutors[i] instanceof VariableExpressionExecutor)) {
                    throw new UnsupportedOperationException("Required a variable, but found a string parameter");
                }
            }

            int m = -1;
            boolean atleastOne = false;
            boolean isCorrect;
            for (ParameterOverload parameterOverload : parameterOverloads1) {
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
                                    boolean adynamic = attributeExpressionExecutors[m] instanceof ConstantExpressionExecutor;
                                    if (dynamic == !adynamic) {
                                        isCorrect = true;
                                    } else {
                                        isCorrect = false;
                                    }
                                }
                            }
                        }
                    }
                    m = -1;
                    if(isCorrect) { atleastOne = true; }
                }

            }
            if(!atleastOne){
                throw new SiddhiAppValidationException("Dynamic Constant doesn't match");
            }

            int n = -1;
            boolean atleastOne1 = false;
            boolean isCorrect1;
            for (ParameterOverload parameterOverload : parameterOverloads1) {
                isCorrect1 = true;
                String[] parameterNames = parameterOverload.parameterNames();
                if (parameterNames.length == attributeExpressionExecutors.length) {
                    for (String parameterName : parameterNames) {
                        if(isCorrect1) {
                            n++;
                            boolean found = false;
                            for (Parameter parameter : parameters) {
                                String parameterName1 = parameter.name();
                                if (parameterName1.equals(parameterName)) {
                                    DataType[] types = parameter.type();
                                    for (DataType type : types) {
                                        if (attributeExpressionExecutors[n].getReturnType().toString() == type.toString()) {
                                            found = true;
                                        }
                                    }
                                }
                            }
                            if(found){
                                isCorrect1 = true;
                            } else {
                                isCorrect1 = false;
                            }
                        }
                    }
                    n = -1;
                    if(isCorrect1) { atleastOne1 = true; }
                }

            }
            if(!atleastOne1){
                throw new SiddhiAppValidationException("Data Type doesn't match");
            }

//
//            for (ParameterOverload parameterOverload : parameterOverloads1) {
//                String[] parameterNames = parameterOverload.parameterNames();
//                if (parameterNames.length == attributeExpressionExecutors.length) {
//                    for (String parameterName : parameterNames) {
//                        i++;
//                        boolean found = false;
//                        for (Parameter parameter : parameters) {
//                            String parameterName1 = parameter.name();
//                            if (parameterName1.equals(parameterName)) {
//                                DataType[] types = parameter.type();
//                                for (DataType type : types) {
//                                    if (attributeExpressionExecutors[i].getReturnType().toString() == type.toString()) {
//                                        found = true;
//                                    }
//                                }
//                            }
//                        }
//
//                        if (!found) {
//                            throw new SiddhiAppValidationException("Data Type doesn't match");
//                        }
//
//                    }
//                }
//            }

//            int m = -1;
//            for (ParameterOverload parameterOverload : parameterOverloads1) {
//                String[] parameterNames = parameterOverload.parameterNames();
//                if (parameterNames.length == attributeExpressionExecutors.length) {
//                    for (String parameterName : parameterNames) {
//                        m++;
//                        boolean found = false;
//                        for (Parameter parameter : parameters) {
//                            String parameterName1 = parameter.name();
//                            if (parameterName1.equals(parameterName)) {
//                                boolean dynamic = parameter.dynamic();
//                                boolean adynamic = attributeExpressionExecutors[m] instanceof ConstantExpressionExecutor;
//                                if (dynamic == !adynamic) {
//                                    found = true;
//                                }
//                            }
//                        }
//
//                        if (!found) {
//                            throw new SiddhiAppValidationException("Dynamic Constant doesn't match");
//                        }
//
//                    }
//                }
//            }



        } else {
            throw new SiddhiAppValidationException("window should only have max" + maxCount + " parameters " +
                    "(<int|long|time>), but found " + attributeExpressionExecutors.length + " input attributes");
        }
    }
}

