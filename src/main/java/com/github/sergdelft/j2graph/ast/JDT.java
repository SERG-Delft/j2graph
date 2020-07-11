package com.github.sergdelft.j2graph.ast;

import com.github.sergdelft.j2graph.graph.MethodGraph;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.List;
import java.util.Map;

public class JDT {

    public List<MethodGraph> parse(String sourceCode) {
        ASTParser parser = ASTParser.newParser(AST.JLS11);

        parser.setResolveBindings(false);
        parser.setBindingsRecovery(false);

        Map<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_11, options);
        parser.setSource(sourceCode.toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        JDTVisitor visitor = new JDTVisitor();
        cu.accept(visitor);

        JDTDebuggingVisitor d = new JDTDebuggingVisitor();
        cu.accept(d);

        return visitor.getGraphs();
    }
}
