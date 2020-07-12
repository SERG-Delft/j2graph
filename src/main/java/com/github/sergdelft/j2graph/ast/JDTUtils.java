package com.github.sergdelft.j2graph.ast;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// this is copied from my own CK project
// https://github.com/mauricioaniche/ck/blob/master/src/main/java/com/github/mauricioaniche/ck/util/JDTUtils.java
// in the future, I should create a library with these helpers...
public class JDTUtils {

    //Get the fully qualified method name with parameter count and types, e.g. rfc.GO.m1/1[int]
    public static String getQualifiedMethodFullName(IMethodBinding binding){
        String methodName = binding.getName();
        if(binding.getDeclaringClass() != null){
            methodName = binding.getDeclaringClass().getQualifiedName() + "." + binding.getName();
        }
        return methodName + "/" + getMethodSignature(binding);
    }

    //Get the fully qualified method name with parameter count and types, e.g. rfc.GO.m1/1[int]
    public static String getQualifiedMethodFullName(MethodInvocation node) {
        IMethodBinding binding = node.resolveMethodBinding();
        if(binding != null){
            return getQualifiedMethodFullName(binding);
        } else {
            return node.getName().getFullyQualifiedName() + "/" + getMethodSignature(node.arguments(), node.typeArguments());
        }
    }

    //Get the fully qualified method name with parameter count and types, e.g. rfc.GO.m1/1[int]
    public static String getQualifiedMethodFullName(MethodDeclaration node) {
        if(node.resolveBinding() != null){
            return getQualifiedMethodFullName(node.resolveBinding());
        }
        String methodName = node.getName().getFullyQualifiedName();
        return methodName + "/" + getMethodSignature(node);
    }

    //Get the signature of a method with parameter count and types, e.g. 1[int]
    public static String getMethodSignature(MethodDeclaration node){
        int parameterCount = node.parameters()==null ? 0 : node.parameters().size();
        List<String> parameterTypes = new ArrayList<>();

        if(parameterCount > 0) {
            for(Object p0 : node.parameters()) {
                SingleVariableDeclaration parameter = (SingleVariableDeclaration) p0;

                ITypeBinding binding = parameter.getType().resolveBinding();

                String v;
                if(binding == null || binding.isRecovered())
                    v = parameter.getType().toString();
                else
                    v = binding.getQualifiedName();

                if(parameter.isVarargs()) v+="[]";

                parameterTypes.add(v);
            }
        }

        return formatSignature(parameterTypes);
    }

    //Get the signature of a method with parameter count and types, e.g. 1[int]
    public static String getMethodSignature(IMethodBinding node){
        int parameterCount = node.getParameterTypes()==null ? 0 : node.getParameterTypes().length;
        List<String> parameterTypes = new ArrayList<>();

        if(parameterCount > 0) {
            for(ITypeBinding binding : node.getParameterTypes()) {

                String v = binding.getQualifiedName();

                parameterTypes.add(v);
            }
        }
        return formatSignature(parameterTypes);
    }

    private static String formatSignature(List<String> parameters){
        int parameterCount = parameters.size();
        return String.format("%d%s%s%s",
                parameterCount,
                (parameterCount > 0 ? "[" : ""),
                (parameterCount > 0 ? String.join(",", parameters) : ""),
                (parameterCount > 0 ? "]" : "")
        );
    }

    //Helper method to extract the number of arguments from an argument list used to generate the method signature for MethodInvocation nodes
    private static String getMethodSignature(List<?> arguments, List<?> typeArguments) {
        int argumentCount = arguments != null ? arguments.size() : 0;
        List<String> parameterTypes = typeArguments.stream().map(object -> object.toString()).collect(Collectors.toList());
        return formatSignature(parameterTypes);
    }

}
