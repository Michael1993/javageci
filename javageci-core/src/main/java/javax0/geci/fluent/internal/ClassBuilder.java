package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;
import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.JavaSource;
import javax0.geci.tools.MethodTool;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ClassBuilder {
    private final InterfaceNameFactory ifNameFactory;
    private final MethodCollection methods;
    private final FluentBuilderImpl fluent;
    private String interfaceName;
    private static final Logger LOG = LoggerFactory.getLogger(ClassBuilder.class);

    /**
     * Create a ClassBuilder that builds the interface and class structure from the fluent definition.
     *
     * @param fluent the fluent definition.
     */
    public ClassBuilder(FluentBuilderImpl fluent) {
        this.ifNameFactory = new InterfaceNameFactory();
        this.fluent = fluent;
        this.methods = fluent.getMethods();
    }

    /**
     * Create a new ClassBuilder that is essentially the clone of the other one.
     *
     * @param that the other class builder
     */
    private ClassBuilder(ClassBuilder that) {
        this.ifNameFactory = that.ifNameFactory;
        this.methods = that.methods;
        this.fluent = that.fluent;
        this.interfaceName = that.interfaceName;
    }


    /**
     * Build the interface and class structure that implements the fluent interface.
     *
     * @return the string of the code that was built up.
     * @throws Exception when there is an error in the grammar
     */
    public String build() throws Exception {
        LOG.debug("Class building started for the class %s", fluent.getKlass().getSimpleName());
        List<Node> list = fluent.getNodes();
        if (list.size() == 0) {
            throw new GeciException("There are no actual calls in the fluent structure.");
        }
        LOG.debug("There are %d nodes on the top level", list.size());
        final Tree tree = new Tree(Node.ONCE, list);
        final String exitType = NodeTypeCalculator.from(methods).getReturnType(getLastNode(list));
        final String lastInterface = GeciReflectionTools.normalizeTypeName(exitType);
        LOG.debug("The last type is %s", lastInterface);
        final String interfaces = build(tree, lastInterface);
        final JavaSource.Builder code = JavaSource.builder();
        writeStartMethod(code);
        writeWrapperInterface(code);
        writeWrapperClass(code);
        code.write(interfaces);
        return code.toString();
    }

    /**
     * Get the last node of the list of nodes.
     *
     * @param list a non-empty list of nodes
     * @return the last node
     */
    private Node getLastNode(List<Node> list) {
        return list.get(list.size() - 1);
    }


    /**
     * Write the source code of the start method into the source builder.
     * <p>
     * The start method is a public static method with no argument that creates a new instance of the wrapper
     * class and returns it as the interface type that can be used to start the fluent API structure.
     *
     * @param code to write the start method into
     * @throws Exception never, signature inherited from {@code }AutoCloseable}
     */
    private void writeStartMethod(JavaSource.Builder code) throws Exception {
        final String startMethod = fluent.getStartMethod() == null ? "start" : fluent.getStartMethod();
        LOG.debug("Creating start method %s()", startMethod);
        final String lastType;
        if (fluent.getLastType() != null) {
            lastType = fluent.getLastType();
            code.write("public interface %s extends %s {}", lastType, ifNameFactory.getLastName());
        } else {
            lastType = ifNameFactory.getLastName();
        }
        try (final JavaSource.MethodBody mtBl = code.method(startMethod).modifiers("public static").returnType(lastType).noArgs()) {
            mtBl.returnStatement("new Wrapper()");
        }
    }

    private void writeWrapperInterface(JavaSource.Builder code) throws Exception {
        if (methods.needWrapperInterface()) {
            try (JavaSource.Ukeg ignored = code.open("public interface WrapperInterface")) {
            }
        }
    }

    /**
     * Write the source code of the wrapper class into the source builder.
     *
     * @param code to write the start method into
     */
    private void writeWrapperClass(JavaSource.Builder code) throws Exception {
        try (JavaSource.Ukeg klBl = code.open("public static class Wrapper implements %s", setJoin(ifNameFactory.getAllNames(), fluent.getLastType(), fluent.getInterfaces()))) {
            klBl.statement("private final %s that", fluent.getKlass().getCanonicalName());
            if (fluent.getCloner() != null) {
                try (JavaSource.Ukeg coBl = klBl.open("public Wrapper(%s that)", fluent.getKlass().getCanonicalName())) {
                    coBl.statement("this.that = that");
                }
            }
            try (JavaSource.Ukeg coBl = klBl.open("public Wrapper()")) {
                coBl.statement("this.that = new %s()", fluent.getKlass().getCanonicalName());
            }
            writeWrapperMethods(code);
        }
    }


    private void writeWrapperMethods(JavaSource.Builder code) throws Exception {
        for (String signature : methods.methodSignatures()) {
            Method method = methods.get(signature);
            if (fluent.getCloner() == null || !fluent.getCloner().equals(method)) {
                Boolean notFluent = methods.isExitNode(signature) || !methods.isFluentNode(signature);
                String actualReturnType = notFluent ? null : "Wrapper";
                String signatureString = FluentMethodTool
                    .from(fluent.getKlass())
                    .asPublic()
                    .forThe(method)
                    .withType(actualReturnType)
                    .signature();
                try (JavaSource.MethodBody methodBody = (JavaSource.MethodBody) code.open(signatureString)) {
                    if (notFluent) {
                        writeNonFluentMethodWrapper(method, methodBody);
                    } else {
                        writeWrapperMethodBody(method, methodBody);
                    }
                }
            }
        }
    }

    private void writeWrapperMethodBody(Method method, JavaSource.MethodBody mtBl) {
        String callString = FluentMethodTool.from(fluent.getKlass()).forThe(method).call();
        if (fluent.getCloner() != null) {
            mtBl.statement("var next = new Wrapper(that.%s)", MethodTool.with(fluent.getCloner()).call())
                .statement("next.that.%s", callString)
                .returnStatement("next");

        } else {
            mtBl.statement("that.%s", callString)
                .returnStatement("this");
        }
    }

    private void writeNonFluentMethodWrapper(Method method, JavaSource.MethodBody mtBl) {
        String callString = FluentMethodTool.from(fluent.getKlass()).forThe(method).call();
        if (method.getReturnType() == Void.TYPE) {
            mtBl.statement("that.%s", callString);
        } else {
            mtBl.returnStatement("that.%s", callString);
        }
    }

    private String build(Node node, String nextInterface) {
        if (node instanceof Terminal) {
            return build((Terminal) node, nextInterface);
        } else {
            return build((Tree) node, nextInterface);
        }
    }

    private String build(Terminal terminal, String nextInterface) {
        interfaceName = ifNameFactory.getNewName(terminal);
        JavaSource code = new JavaSource();
        String list = InterfaceSet.builderFor(methods)
            .when((terminal.getModifier() & (Node.OPTIONAL | Node.ZERO_OR_MORE)) != 0).then(nextInterface, fluent.getInterfaces())
            .buildList();
        try (JavaSource ifcB = code.open("public interface %s%s ", interfaceName, list)) {
            ifcB.statement(FluentMethodTool
                .from(fluent.getKlass())
                .forThe(methods.get(terminal.getMethod()))
                .withType((terminal.getModifier() & Node.ZERO_OR_MORE) != 0 ? interfaceName : nextInterface)
                .asInterface()
                .signature());
        }
        return code.toString();
    }

    private String build(Tree tree, String nextInterface) {
        int modifier = tree.getModifier();
        if (modifier == Node.ONCE) {
            return buildOnce(tree, nextInterface);
        }
        if (modifier == Node.OPTIONAL) {
            return buildOptional(tree, nextInterface);
        }
        if (modifier == Node.ZERO_OR_MORE) {
            return buildZeroOrMore(tree, nextInterface);
        }
        if (modifier == Node.ONE_OF) {
            return buildOneOf(tree, nextInterface);
        }
        if (modifier == Node.ONE_TERMINAL_OF) {
            return buildOneTerminalOf(tree, nextInterface);
        }
        throw new GeciException("Internal error tree " + tree.toString() + " modifier is " + modifier);
    }

    private String buildOneTerminalOf(Tree tree, String nextInterface) {
        this.interfaceName = ifNameFactory.getNewName(tree);
        JavaSource code = new JavaSource();
        try (
            final JavaSource ifcB = code.open("public interface %s", this.interfaceName)) {
            for (Node node : tree.getList()) {
                if (node instanceof Tree) {
                    throw new GeciException("Internal error, ON_TERMINAL_OF contains a non-terminal sub.");
                } else {
                    Terminal terminal = (Terminal) node;
                    ifcB.statement(FluentMethodTool
                        .from(fluent.getKlass())
                        .forThe(methods.get(terminal.getMethod()))
                        .withType(nextInterface)
                        .asInterface()
                        .signature());
                }
            }
        }
        return code.toString();
    }

    private String buildOneOf(Tree tree, String nextInterface) {
        final List<Node> list = tree.getList();
        final JavaSource code = new JavaSource();
        final Set<String> alternativeInterfaces = new HashSet<String>();
        for (Node node : list) {
            ClassBuilder builder = new ClassBuilder(this);
            code.write(builder.build(node, nextInterface));
            alternativeInterfaces.add(builder.interfaceName);
        }
        this.interfaceName = ifNameFactory.getNewName(tree);
        final String ifs = InterfaceSet.builderFor(methods)
            .set(fluent.getInterfaces())
            .set(alternativeInterfaces)
            .buildList();
        try (final JavaSource ifcB = code.open("public interface %s%s", this.interfaceName, ifs)) {
        }
        return code.toString();
    }

    private String setJoin(Set<String> set, String... other) {
        if (other != null && other.length > 0) {
            Set<String> local = new HashSet<>(set);
            local.addAll(Arrays.stream(other).filter(Objects::nonNull).collect(Collectors.toSet()));
            return String.join(",", local);
        } else {
            return String.join(",", set);
        }
    }

    private String buildZeroOrMore(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        JavaSource code = new JavaSource();
        this.interfaceName = ifNameFactory.getNewName(tree);
        ClassBuilder lastBuilder = buildNodeList(this.interfaceName, list, code);
        String ifs = InterfaceSet.builderFor(methods).set(nextInterface, lastBuilder.interfaceName, fluent.getInterfaces()).buildList();
        code.write("public interface %s%s {}", this.interfaceName, ifs);
        return code.toString();
    }

    private String buildOptional(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        JavaSource code = new JavaSource();
        this.interfaceName = ifNameFactory.getNewName(tree);
        ClassBuilder lastBuilder = buildNodeList(nextInterface, list, code);
        String ifs = InterfaceSet.builderFor(methods).set(nextInterface, lastBuilder.interfaceName, fluent.getInterfaces()).buildList();
        code.write("public interface %s%s {}", this.interfaceName, ifs);
        return code.toString();
    }

    private String buildOnce(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        JavaSource code = new JavaSource();
        ClassBuilder lastBuilder = buildNodeList(nextInterface, list, code);
        this.interfaceName = lastBuilder.interfaceName;
        return code.toString();
    }

    private ClassBuilder buildNodeList(String nextInterface, List<Node> list, JavaSource code) {
        ClassBuilder lastBuilder = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            final Node node = list.get(i);
            ClassBuilder builder = new ClassBuilder(this);
            final String actualNextInterface =
                Optional.ofNullable(lastBuilder).map(z -> z.interfaceName).orElse(nextInterface);
            code.write(builder.build(node, actualNextInterface));
            if (i == 0) {
                return builder;
            }
            lastBuilder = builder;
        }
        throw new GeciException("Internal error");
    }
}
