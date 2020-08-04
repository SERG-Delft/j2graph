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
            return super.visit(node);
        } else {
            // TODO: it's a sub-class, we ignore it.
            return false;
        }
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
        if (inAMethod() && !nonTerminals.get(currentMethod()).isEmpty()) {
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

    public boolean visit(AnnotationTypeDeclaration node) {
        return false;
    }

    public boolean visit(AnnotationTypeMemberDeclaration node) {
        return false;
    }

    public boolean visit(AnonymousClassDeclaration node) {
        // TODO: visit anonymous classes
        return false;
    }

    public boolean visit(ArrayAccess node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ArrayCreation node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ArrayInitializer node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ArrayType node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(AssertStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(Assignment node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        this.assignmentMode = true;
        addNonTerminal(node);
        return super.visit(node);
    }


    public boolean visit(Block node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(BlockComment node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(BooleanLiteral node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        currentNonTerminal().token("" + node.booleanValue());
        return super.visit(node);
    }

    public boolean visit(BreakStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(CastExpression node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;


        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(CatchClause node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(CharacterLiteral node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        // we add the escaped literal value as a token
        currentNonTerminal().token(node.getEscapedValue());
        return super.visit(node);
    }

    public boolean visit(ClassInstanceCreation node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ConditionalExpression node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ConstructorInvocation node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ContinueStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(CreationReference node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(Dimension node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(DoStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        currentNonTerminal().token("do");
        return super.visit(node);
    }

    public boolean visit(EmptyStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(EnhancedForStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        currentNonTerminal().token("for");
        return super.visit(node);
    }

    public boolean visit(EnumConstantDeclaration node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(EnumDeclaration node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ExportsDirective node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ExpressionMethodReference node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ExpressionStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(FieldAccess node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(FieldDeclaration node) {
        // we do not need to visit field declarations.
        return false;
    }

    public boolean visit(ForStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        currentNonTerminal().token("for");

        return super.visit(node);
    }

    public boolean visit(IfStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        currentNonTerminal().token("if");
        return super.visit(node);
    }

    public boolean visit(ImportDeclaration node) {
        /* do nothing */
        return false;
    }

    public boolean visit(InfixExpression node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;


        // an infix operation is [left operation right]
        // we visit it manually, as to store the operation as a token
        addNonTerminal(node);

        node.getLeftOperand().accept(this);
        currentNonTerminal().token(node.getOperator().toString());
        node.getRightOperand().accept(this);

        return false;
    }

    public boolean visit(Initializer node) {
        // TODO: handle static initializer methods
        return false;
    }

    public boolean visit(InstanceofExpression node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(IntersectionType node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(LabeledStatement node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(LambdaExpression node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(LineComment node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(MarkerAnnotation node) {
        return false;
    }

    public boolean visit(MemberRef node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(Javadoc node) {
        /* do nothing */
        return false;
    }

    public boolean visit(MemberValuePair node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(MethodRef node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(MethodRefParameter node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(NameQualifiedType node) {

        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(NormalAnnotation node) {
        return false;
    }

    public boolean visit(NullLiteral node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        // we add the literal as token
        currentNonTerminal().token("null");
        return super.visit(node);
    }

    public boolean visit(NumberLiteral node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        // we add the literal as token
        currentNonTerminal().token(node.getToken());
        return super.visit(node);
    }

    public boolean visit(OpensDirective node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(PackageDeclaration node) {
        /* do nothing */
        return false;
    }

    public boolean visit(ParameterizedType node) {
        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ParenthesizedExpression node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(PostfixExpression node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(PrefixExpression node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ProvidesDirective node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(PrimitiveType node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        // we add the literal as token
        currentNonTerminal().token(node.toString());
        return super.visit(node);
    }

    public boolean visit(QualifiedName node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(QualifiedType node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(RequiresDirective node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ReturnStatement node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        // add 'return' as a token
        currentNonTerminal().token("return");
        return super.visit(node);
    }

    public boolean visit(SimpleType node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        currentNonTerminal().token(node.getName().toString());
        return false;
    }

    public boolean visit(SingleMemberAnnotation node) {
        return false;
    }

    public boolean visit(SingleVariableDeclaration node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(StringLiteral node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        // we add the literal as token
        // as this is a string, we add it to the vocabulary
        currentNonTerminal().token(node.getLiteralValue(), true, false);
        return super.visit(node);
    }

    public boolean visit(SuperConstructorInvocation node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SuperFieldAccess node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SuperMethodInvocation node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SuperMethodReference node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SwitchCase node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SwitchStatement node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(SynchronizedStatement node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TagElement node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TextElement node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ThisExpression node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(ThrowStatement node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TryStatement node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TypeLiteral node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        // we add the type as a token
        // and parse it as to increase our vocabulary
        currentNonTerminal().token(node.getType().toString(),true,false);
        return super.visit(node);
    }

    public boolean visit(TypeMethodReference node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(TypeParameter node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(UnionType node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(UsesDirective node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(VariableDeclarationExpression node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(VariableDeclarationStatement node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(VariableDeclarationFragment node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

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
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        currentNonTerminal().token("while");
        return super.visit(node);
    }

    public boolean visit(WildcardType node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        return super.visit(node);
    }

    public boolean visit(Modifier node) {
        if(!inAMethod() || nonTerminals.get(currentMethod()).isEmpty())
            return false;

        addNonTerminal(node);
        if (!nonTerminals.get(currentMethod()).isEmpty()) {
            currentNonTerminal().token(node.toString());
        }
        return super.visit(node);
    }

    // ------------------------------------------------------------------------------------
    // the end visits basically pop the current non terminal
    // ------------------------------------------------------------------------------------

    public void endVisit(Modifier node) {

        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(AnnotationTypeDeclaration node) {
        /* do nothing */
    }

    public void endVisit(AnnotationTypeMemberDeclaration node) {
        /* do nothing */
    }

    public void endVisit(AnonymousClassDeclaration node) {
        /* do nothing */
    }

    public void endVisit(ArrayAccess node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ArrayCreation node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ArrayInitializer node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ArrayType node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(AssertStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(Assignment node) {
        cleanVariableAssignment();
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(Block node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(BlockComment node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }


    public void endVisit(BreakStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(CastExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(CatchClause node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ClassInstanceCreation node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ConditionalExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ConstructorInvocation node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ContinueStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(CreationReference node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(DoStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(EmptyStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(EnhancedForStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(EnumConstantDeclaration node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(EnumDeclaration node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ExportsDirective node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ExpressionMethodReference node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ExpressionStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(Dimension node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(FieldAccess node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(FieldDeclaration node) {
        /* do nothing */
    }

    public void endVisit(ForStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(IfStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ImportDeclaration node) {
        /* do nothing */
    }

    public void endVisit(InfixExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(InstanceofExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(Initializer node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(Javadoc node) {
        /* do nothing */
    }

    public void endVisit(LabeledStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(LambdaExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(LineComment node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(MarkerAnnotation node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(MemberRef node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(MemberValuePair node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(MethodRef node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(MethodRefParameter node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ModuleDeclaration node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(NameQualifiedType node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(NormalAnnotation node) {
        /* do nothing */
    }

    public void endVisit(OpensDirective node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(PackageDeclaration node) {
        /* do nothing */
    }

    public void endVisit(ParameterizedType node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ParenthesizedExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(PostfixExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(PrefixExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(PrimitiveType node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ProvidesDirective node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(QualifiedName node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(QualifiedType node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(RequiresDirective node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ReturnStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(SimpleType node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(SingleMemberAnnotation node) {
        /* do nothing */
    }

    public void endVisit(SingleVariableDeclaration node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(SuperConstructorInvocation node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(SuperFieldAccess node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(SuperMethodInvocation node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(SuperMethodReference node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(SwitchCase node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(SwitchStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(SynchronizedStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(TagElement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(TextElement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ThisExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(ThrowStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(TryStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }


    public void endVisit(TypeMethodReference node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(TypeParameter node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(UnionType node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(UsesDirective node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(IntersectionType node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(VariableDeclarationExpression node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(VariableDeclarationStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(VariableDeclarationFragment node) {
        if(!inAMethod())
            return;

        cleanVariableAssignment();
        popNonTerminal();
    }

    public void endVisit(WhileStatement node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    public void endVisit(WildcardType node) {
        if(!inAMethod())
            return;

        popNonTerminal();
    }

    // ------------------------------------------------------------------------------------

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
        if(!nonTerminals.isEmpty() && !methodBuilders.isEmpty() && !nonTerminals.get(currentMethod()).isEmpty())
            nonTerminals.get(currentMethod()).pop();
    }

    private void addNonTerminal(ASTNode n) {
        if (!methodBuilders.isEmpty() && !nonTerminals.get(currentMethod()).isEmpty()) {
            NonTerminalBuilder nonTerminal = currentNonTerminal().nonTerminal(type(n));
            nonTerminals.get(currentMethod()).push(nonTerminal);

            checkAssignmentMode();
        }
    }

    private void addMethodInvocation(ASTNode n, String invokedMethod) {
        if (nonTerminals.get(currentMethod()).isEmpty()) {
            return;
        }
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

    private boolean inAMethod() {
        return !methodBuilders.isEmpty() && !nonTerminals.isEmpty();
    }

    private NonTerminalBuilder currentNonTerminal() {
        return nonTerminals.get(currentMethod()).peek();
    }

    public ClassGraph buildClassGraph() {
        return classBuilder != null ? classBuilder.build() : null;
    }

}
