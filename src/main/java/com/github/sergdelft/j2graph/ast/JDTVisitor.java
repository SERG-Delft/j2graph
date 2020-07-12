package com.github.sergdelft.j2graph.ast;

import com.github.sergdelft.j2graph.builder.ClassGraphBuilder;
import com.github.sergdelft.j2graph.builder.MethodGraphBuilder;
import com.github.sergdelft.j2graph.builder.NonTerminalBuilder;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import com.github.sergdelft.j2graph.graph.Symbol;
import com.github.sergdelft.j2graph.graph.Token;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

import static com.github.sergdelft.j2graph.ast.JDTUtils.getQualifiedMethodFullName;

public class JDTVisitor extends ASTVisitor {

    // the builder of the class we are visiting
    // for now, it does not support sub-classes
    private ClassGraphBuilder classBuilder;

    // the method builder of the method being visited
    // a stack as to support nested methods.
    private final Stack<MethodGraphBuilder> methodBuilders = new Stack<>();

    // the list of non terminal nodes of a given method builder.
    // it's a stack as to keep the current node being visited
    private final Map<MethodGraphBuilder, Stack<NonTerminalBuilder>> nonTerminals = new HashMap<>();

    // keep a map with all method invocations
    // as to link the 'return' edges later
    // key=method, value=methods it invokes
    private final Map<String, Set<String>> methodInvocations = new HashMap<>();

    // assignment mode, on or off
    // the assignment mode tracks symbols visited, so that we can assign
    // the 'assigned from' edge later
    // (see where these variables are used to understand how we do)
    private Pair<Symbol, Token> assignmentVariable;
    private boolean assignmentMode;

    @Override
    public boolean visit(TypeDeclaration node) {
        // only if no class was detected
        // in the future, if we plan to support sub-classes, this needs to change
        if(classBuilder==null) {
            classBuilder = new ClassGraphBuilder(node.getName().getFullyQualifiedName(), methodInvocations);
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        // whenever we visit a method, we create a builder for it
        // and set the MethodDeclaration as its NonTerminal root
        String methodQualifiedName = getQualifiedMethodFullName(node);
        MethodGraphBuilder builder = new MethodGraphBuilder(classBuilder, methodQualifiedName);
        NonTerminalBuilder root = builder.root(type(node));

        // we push it to the list of method builders.
        // this list, in most cases, will have a single element only.
        // More elements, only when methods are declared inside methods,
        // which, in this case, we separate them in two different methods.
        methodBuilders.push(builder);
        nonTerminals.put(builder, new Stack<>());
        nonTerminals.get(builder).push(root);

        // create an entry in the method invocations map
        // as we'll keep score of all its method invocations
        methodInvocations.put(methodQualifiedName, new HashSet<>());

        return super.visit(node);
    }

    @Override
    public void endVisit(MethodDeclaration node) {
        // add the method builder to the class graph builder
        classBuilder.addMethod(currentMethod());
        popMethod();
    }

    @Override
    public boolean visit(MethodInvocation node) {
        String invokedMethod = getQualifiedMethodFullName(node);

        addMethodInvocation(node, invokedMethod);

        // add to the map
        methodInvocations.get(currentMethod().getMethodName())
                .add(invokedMethod);

        return super.visit(node);
    }

    @Override
    public void endVisit(MethodInvocation node) {
        popNonTerminal();
    }


    public boolean visit(SimpleName node) {
        // whenever we visit a SimpleName, we mark it as a symbol.
        // however, it might be that we are not inside a method
        // e.g., field declaration
        // so, we only collect it if we are inside a method
        if (inAMethod()) {
            Pair<Symbol, Token> pair = currentNonTerminal().symbol(node.getIdentifier());

            // this symbol might appear as part of an assignment.
            // we need to 'mark it' so that we can create the
            // 'assigned from' edge.
            // note that we only 'mark' the first symbol we visit, while
            // in this mode. This is more to avoid any strange cases.
            if(assignmentMode && assignmentVariable == null) {
                assignmentVariable = pair;
            }
        }
        return true;
    }

    private boolean inAMethod() {
        return !methodBuilders.isEmpty() && !nonTerminals.isEmpty();
    }

    public boolean visit(AnnotationTypeDeclaration node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(AnnotationTypeMemberDeclaration node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(AnonymousClassDeclaration node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ArrayAccess node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ArrayCreation node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ArrayInitializer node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ArrayType node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(AssertStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(Assignment node) {
        this.assignmentMode = true;
        addNonTerminal(node);
        return super.visit(node);
    }


    public boolean visit(Block node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(BlockComment node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(BooleanLiteral node) {
        currentNonTerminal().token("" + node.booleanValue());
        return super.visit(node);
    }

    public boolean visit(BreakStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(CastExpression node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(CatchClause node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(CharacterLiteral node) {
        // we add the escaped literal value as a token
        currentNonTerminal().token(node.getEscapedValue());
        return super.visit(node);
    }

    public boolean visit(ClassInstanceCreation node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ConditionalExpression node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ConstructorInvocation node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ContinueStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(CreationReference node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(Dimension node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(DoStatement node) {
        addNonTerminal(node);
        currentNonTerminal().token("do");
        return super.visit(node);
    }

    public boolean visit(EmptyStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(EnhancedForStatement node) {
        addNonTerminal(node);
        currentNonTerminal().token("for");
        return super.visit(node);
    }

    public boolean visit(EnumConstantDeclaration node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(EnumDeclaration node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ExportsDirective node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ExpressionMethodReference node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ExpressionStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(FieldAccess node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(FieldDeclaration node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ForStatement node) {
        addNonTerminal(node);
        currentNonTerminal().token("for");

        return super.visit(node);
    }

    public boolean visit(IfStatement node) {
        addNonTerminal(node);
        currentNonTerminal().token("if");
        return super.visit(node);
    }

    public boolean visit(ImportDeclaration node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(InfixExpression node) {

        // an infix operation is [left operation right]
        // we visit it manually, as to store the operation as a token
        addNonTerminal(node);

        node.getLeftOperand().accept(this);
        currentNonTerminal().token(node.getOperator().toString());
        node.getRightOperand().accept(this);

        return false;
    }

    public boolean visit(Initializer node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(InstanceofExpression node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(IntersectionType node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(LabeledStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(LambdaExpression node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(LineComment node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(MarkerAnnotation node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(MemberRef node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(MemberValuePair node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(MethodRef node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(MethodRefParameter node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(NameQualifiedType node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(NormalAnnotation node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(NullLiteral node) {
        // we add the literal as token
        currentNonTerminal().token("null");
        return super.visit(node);
    }

    public boolean visit(NumberLiteral node) {
        // we add the literal as token
        currentNonTerminal().token(node.getToken());
        return super.visit(node);
    }

    public boolean visit(OpensDirective node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(PackageDeclaration node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ParameterizedType node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ParenthesizedExpression node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(PostfixExpression node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(PrefixExpression node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ProvidesDirective node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(PrimitiveType node) {
        addNonTerminal(node);
        // we add the literal as token
        currentNonTerminal().token(node.toString());
        return super.visit(node);
    }

    public boolean visit(QualifiedName node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(QualifiedType node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(RequiresDirective node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ReturnStatement node) {
        addNonTerminal(node);
        // add 'return' as a token
        currentNonTerminal().token("return");
        return super.visit(node);
    }

    public boolean visit(SimpleType node) {
        addNonTerminal(node);
        currentNonTerminal().token(node.getName().toString());
        return false;
    }

    public boolean visit(SingleMemberAnnotation node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SingleVariableDeclaration node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(StringLiteral node) {
        // we add the literal as token
        // as this is a string, we add it to the vocabulary
        currentNonTerminal().token(node.getLiteralValue(), true, false);
        return super.visit(node);
    }

    public boolean visit(SuperConstructorInvocation node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SuperFieldAccess node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SuperMethodInvocation node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SuperMethodReference node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SwitchCase node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SwitchStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SynchronizedStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TagElement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TextElement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ThisExpression node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ThrowStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TryStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TypeLiteral node) {
        // we add the type as a token
        // and parse it as to increase our vocabulary
        currentNonTerminal().token(node.getType().toString(),true,false);
        return super.visit(node);
    }

    public boolean visit(TypeMethodReference node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TypeParameter node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(UnionType node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(UsesDirective node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(VariableDeclarationExpression node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(VariableDeclarationStatement node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(VariableDeclarationFragment node) {
        // a variable was declared, which means we might need to be
        // in assignment mode.
        // unfortunately, JDT doesn't give in 'node' the right part
        // of the expression, so we need to do this 'assignmentMode' workaround.
        // we turn it on here, and later, in future visits, we collect the
        // required information for the 'assigned from' edge.
        this.assignmentMode = true;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(WhileStatement node) {
        addNonTerminal(node);
        currentNonTerminal().token("while");
        return super.visit(node);
    }

    public boolean visit(WildcardType node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(Modifier node) {
        addNonTerminal(node);
        currentNonTerminal().token(node.toString());
        return super.visit(node);
    }

    public void endVisit(Modifier node) {
        popNonTerminal();
    }

    public void endVisit(AnnotationTypeDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(AnnotationTypeMemberDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(AnonymousClassDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(ArrayAccess node) {
        popNonTerminal();
    }

    public void endVisit(ArrayCreation node) {
        popNonTerminal();
    }

    public void endVisit(ArrayInitializer node) {
        popNonTerminal();
    }

    public void endVisit(ArrayType node) {
        popNonTerminal();
    }

    public void endVisit(AssertStatement node) {
        popNonTerminal();
    }

    public void endVisit(Assignment node) {
        cleanVariableAssignment();
        popNonTerminal();
    }

    public void endVisit(Block node) {
        popNonTerminal();
    }

    public void endVisit(BlockComment node) {
        popNonTerminal();
    }


    public void endVisit(BreakStatement node) {
        popNonTerminal();
    }

    public void endVisit(CastExpression node) {
        popNonTerminal();
    }

    public void endVisit(CatchClause node) {
        popNonTerminal();
    }

    public void endVisit(ClassInstanceCreation node) {
        popNonTerminal();
    }

    public void endVisit(ConditionalExpression node) {
        popNonTerminal();
    }

    public void endVisit(ConstructorInvocation node) {
        popNonTerminal();
    }

    public void endVisit(ContinueStatement node) {
        popNonTerminal();
    }

    public void endVisit(CreationReference node) {
        popNonTerminal();
    }

    public void endVisit(DoStatement node) {
        popNonTerminal();
    }

    public void endVisit(EmptyStatement node) {
        popNonTerminal();
    }

    public void endVisit(EnhancedForStatement node) {
        popNonTerminal();
    }

    public void endVisit(EnumConstantDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(EnumDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(ExportsDirective node) {
        popNonTerminal();
    }

    public void endVisit(ExpressionMethodReference node) {
        popNonTerminal();
    }

    public void endVisit(ExpressionStatement node) {
        popNonTerminal();
    }

    public void endVisit(Dimension node) {
        popNonTerminal();
    }

    public void endVisit(FieldAccess node) {
        popNonTerminal();
    }

    public void endVisit(FieldDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(ForStatement node) {
        popNonTerminal();
    }

    public void endVisit(IfStatement node) {
        popNonTerminal();
    }

    public void endVisit(ImportDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(InfixExpression node) {
        popNonTerminal();
    }

    public void endVisit(InstanceofExpression node) {
        popNonTerminal();
    }

    public void endVisit(Initializer node) {
        popNonTerminal();
    }

    public void endVisit(Javadoc node) {
        popNonTerminal();
    }

    public void endVisit(LabeledStatement node) {
        popNonTerminal();
    }

    public void endVisit(LambdaExpression node) {
        popNonTerminal();
    }

    public void endVisit(LineComment node) {
        popNonTerminal();
    }

    public void endVisit(MarkerAnnotation node) {
        popNonTerminal();
    }

    public void endVisit(MemberRef node) {
        popNonTerminal();
    }

    public void endVisit(MemberValuePair node) {
        popNonTerminal();
    }

    public void endVisit(MethodRef node) {
        popNonTerminal();
    }

    public void endVisit(MethodRefParameter node) {
        popNonTerminal();
    }

    public void endVisit(ModuleDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(NameQualifiedType node) {
        popNonTerminal();
    }

    public void endVisit(NormalAnnotation node) {
        popNonTerminal();
    }

    public void endVisit(OpensDirective node) {
        popNonTerminal();
    }

    public void endVisit(PackageDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(ParameterizedType node) {
        popNonTerminal();
    }

    public void endVisit(ParenthesizedExpression node) {
        popNonTerminal();
    }

    public void endVisit(PostfixExpression node) {
        popNonTerminal();
    }

    public void endVisit(PrefixExpression node) {
        popNonTerminal();
    }

    public void endVisit(PrimitiveType node) {
        popNonTerminal();
    }

    public void endVisit(ProvidesDirective node) {
        popNonTerminal();
    }

    public void endVisit(QualifiedName node) {
        popNonTerminal();
    }

    public void endVisit(QualifiedType node) {
        popNonTerminal();
    }

    public void endVisit(RequiresDirective node) {
        popNonTerminal();
    }

    public void endVisit(ReturnStatement node) {
        popNonTerminal();
    }

    public void endVisit(SimpleType node) {
        popNonTerminal();
    }

    public void endVisit(SingleMemberAnnotation node) {
        popNonTerminal();
    }

    public void endVisit(SingleVariableDeclaration node) {
        popNonTerminal();
    }

    public void endVisit(SuperConstructorInvocation node) {
        popNonTerminal();
    }

    public void endVisit(SuperFieldAccess node) {
        popNonTerminal();
    }

    public void endVisit(SuperMethodInvocation node) {
        popNonTerminal();
    }

    public void endVisit(SuperMethodReference node) {
        popNonTerminal();
    }

    public void endVisit(SwitchCase node) {
        popNonTerminal();
    }

    public void endVisit(SwitchStatement node) {
        popNonTerminal();
    }

    public void endVisit(SynchronizedStatement node) {
        popNonTerminal();
    }

    public void endVisit(TagElement node) {
        popNonTerminal();
    }

    public void endVisit(TextElement node) {
        popNonTerminal();
    }

    public void endVisit(ThisExpression node) {
        popNonTerminal();
    }

    public void endVisit(ThrowStatement node) {
        popNonTerminal();
    }

    public void endVisit(TryStatement node) {
        popNonTerminal();
    }


    public void endVisit(TypeMethodReference node) {
        popNonTerminal();
    }

    public void endVisit(TypeParameter node) {
        popNonTerminal();
    }

    public void endVisit(UnionType node) {
        popNonTerminal();
    }

    public void endVisit(UsesDirective node) {
        popNonTerminal();
    }

    public void endVisit(IntersectionType node) {
        popNonTerminal();
    }

    public void endVisit(VariableDeclarationExpression node) {
        popNonTerminal();
    }

    public void endVisit(VariableDeclarationStatement node) {
        popNonTerminal();
    }

    public void endVisit(VariableDeclarationFragment node) {
        cleanVariableAssignment();
        popNonTerminal();
    }

    public void endVisit(WhileStatement node) {
        popNonTerminal();
    }

    public void endVisit(WildcardType node) {
        popNonTerminal();
    }

    // ------
    private void popMethod() {
        nonTerminals.remove(currentMethod());
        methodBuilders.pop();
    }

    private void cleanVariableAssignment() {
        this.assignmentMode = false;
        this.assignmentVariable = null;
    }

    private static String type(ASTNode n) {
        return n.getClass().getSimpleName();
    }

    private void popNonTerminal() {
        nonTerminals.get(currentMethod()).pop();
    }

    private void addNonTerminal(ASTNode n) {
        NonTerminalBuilder nonTerminal = currentNonTerminal().nonTerminal(type(n));
        nonTerminals.get(currentMethod()).push(nonTerminal);

        checkAssignmentMode();
    }

    private void addMethodInvocation(ASTNode n, String invokedMethod) {
        NonTerminalBuilder nonTerminal = currentNonTerminal().methodInvocation(type(n), invokedMethod);
        nonTerminals.get(currentMethod()).push(nonTerminal);

        checkAssignmentMode();
    }

    private void checkAssignmentMode() {
        // if we are in 'assignment mode', this means the assigned variable
        // has this new non terminal as 'assigned from'.
        // we now make the link!
        boolean thereIsAnAssignedVariable = assignmentVariable != null;
        if(assignmentMode && thereIsAnAssignedVariable) {
            assignmentVariable.getRight().assignedFrom(currentNonTerminal().getNode());
            cleanVariableAssignment();
        }
    }

    private MethodGraphBuilder currentMethod() {
        return methodBuilders.peek();
    }

    private NonTerminalBuilder currentNonTerminal() {
        return nonTerminals.get(currentMethod()).peek();
    }

    public ClassGraph buildClassGraph() {
        return classBuilder.build();
    }

}
