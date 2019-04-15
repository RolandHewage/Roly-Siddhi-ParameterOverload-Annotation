package io.siddhi.core.annotation;

import io.siddhi.annotation.Extension;
import io.siddhi.annotation.Parameter;
import io.siddhi.annotation.ParameterOverload;
import io.siddhi.annotation.util.DataType;
import io.siddhi.core.executor.ConstantExpressionExecutor;
import io.siddhi.core.executor.ExpressionExecutor;
import io.siddhi.core.query.processor.stream.AbstractStreamProcessor;
import io.siddhi.core.query.processor.stream.window.TimeWindowProcessor;
import io.siddhi.core.query.processor.stream.window.WindowProcessor;
import io.siddhi.query.api.exception.SiddhiAppValidationException;

public class TimeWindowProcessorAnnotationValidator {

    public static void validateAnnotation(AbstractStreamProcessor wp, ExpressionExecutor[] attributeExpressionExecutors) throws SiddhiAppValidationException{

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
//                }
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
        Extension annotation = wp.getClass().getAnnotation(Extension.class);
        ParameterOverload[] parameterOverloads1 = annotation.parameterOverloads();
        Parameter[] parameters = annotation.parameters();
        int max = 0;
        int count = 0;
        for (ParameterOverload parameterOverload : parameterOverloads1) {
            String[] parameterNames = parameterOverload.parameterNames();
            for (String  parameterName : parameterNames) {
                count++;
            }
            if(count > max) {
                max = count;
            }
            count = 0;
        }
        int i = -1;
//        if(max >= attributeExpressionExecutors.length) {
//            for (ParameterOverload parameterOverload : parameterOverloads1) {
//                String[] parameterNames = parameterOverload.parameterNames();
//                if(parameterNames.length == attributeExpressionExecutors.length) {
//                    for (String parameterName : parameterNames) {
//                        i++;
//
//                        for (Parameter parameter : parameters) {
//                            String parameterName1 = parameter.name();
//                            if(parameterName1.equals(parameterName)) {
//                                DataType[] types = parameter.type();
//                                for(DataType type : types){
//                                    if(attributeExpressionExecutors[i].getReturnType().equals(type)){
//
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//                }
//            }
//        } else {
        if(max >= attributeExpressionExecutors.length) {
            for (int n=0; i<attributeExpressionExecutors.length; i++) {
                for (ParameterOverload parameterOverload : parameterOverloads1) {
                    String[] parameterNames = parameterOverload.parameterNames();
                    for (String parameterName : parameterNames) {
                        for (Parameter parameter : parameters) {
                            String parameterName1 = parameter.name();
                            if(parameterName1.equals(parameterName)) {
                                DataType[] types = parameter.type();
                                for(DataType type : types) {
                                    if(attributeExpressionExecutors[n].getReturnType().equals(type)) {

                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (ParameterOverload parameterOverload : parameterOverloads1){
                String[] parameterNames = parameterOverload.parameterNames();
                for (int n=0; i<attributeExpressionExecutors.length; i++){
                    for (String parameterName : parameterNames) {
                        for (Parameter parameter : parameters) {
                            String parameterName1 = parameter.name();
                            if(parameterName1.equals(parameterName)) {
                                DataType[] types = parameter.type();
                                for(DataType type : types){
                                    if(attributeExpressionExecutors[n].getReturnType().equals(type)){

                                    }
                                }
                            }
                        }
                    }
                }
            }


            for (ParameterOverload parameterOverload : parameterOverloads1) {
                String[] parameterNames = parameterOverload.parameterNames();
                if(parameterNames.length == attributeExpressionExecutors.length) {
                    for (String parameterName : parameterNames) {
                        i++;
                        boolean found = false;
                        for (Parameter parameter : parameters) {
                            String parameterName1 = parameter.name();
                            if(parameterName1.equals(parameterName)) {
                                DataType[] types = parameter.type();
                                for(DataType type : types){
                                    if(attributeExpressionExecutors[i].getReturnType().equals(type)){
                                        found = true;
                                    }
                                }
                            }
                        }

                        if(!found) {
                            throw new SiddhiAppValidationException("Data Type doesn't match");
                        }

                    }
                }
            }

            int m = -1;
            for (ParameterOverload parameterOverload : parameterOverloads1) {
                String[] parameterNames = parameterOverload.parameterNames();
                if (parameterNames.length == attributeExpressionExecutors.length) {
                    for (String parameterName : parameterNames) {
                        m++;
                        boolean found = false;
                        for (Parameter parameter : parameters) {
                            String parameterName1 = parameter.name();
                            if (parameterName1.equals(parameterName)) {
                                boolean dynamic = parameter.dynamic();
                                boolean adynamic = attributeExpressionExecutors[m] instanceof ConstantExpressionExecutor;
                                if (dynamic == !adynamic) {
                                    found = true;
                                }
                            }
                        }

                        if (!found) {
                            throw new SiddhiAppValidationException("Dynamic Constant doesn't match");
                        }

                    }
                }
            }

            //            for (ParameterOverload parameterOverload : parameterOverloads1) {
//                String[] parameterNames = parameterOverload.parameterNames();
//                if (parameterNames.length == attributeExpressionExecutors.length) {
//                    for (int m=0; m<attributeExpressionExecutors.length; m++){
//                        for (String parameterName : parameterNames) {
//                            boolean found = false;
//                            for (Parameter parameter : parameters) {
//                                String parameterName1 = parameter.name();
//                                if (parameterName1.equals(parameterName)) {
//                                    boolean dynamic = parameter.dynamic();
//                                    boolean adynamic = attributeExpressionExecutors[m] instanceof ConstantExpressionExecutor;
//                                    if (dynamic == !adynamic) {
//                                        found = true;
//                                    }
//                                }
//                            }
//                            if (!found) {
//                                throw new SiddhiAppValidationException("Dynamic Constant doesn't match");
//                            }
//                        }
//                    }
//                }
//            }

        } else {
            throw new SiddhiAppValidationException("Time window should only have max" + max + " parameters " +
                    "(<int|long|time>), but found " + attributeExpressionExecutors.length + " input attributes");
        }
    }

}
