package org.devtimize.urm.scanners;

import org.devtimize.urm.DomainClassFinder;
import org.devtimize.urm.domain.Edge;
import org.devtimize.urm.domain.EdgeType;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class FieldScanner extends AbstractScanner {

    private static final String NAME_FOR_INNERCLASS = null;
    private static final String innerClassFieldReferenceInBytecode = "this$0";

    private final Logger logger = LoggerFactory.getLogger(FieldScanner.class);

    public FieldScanner(final List<Class<?>> classes) {
        super(classes);
        logger.info("Field scanner to scan classes:" + classes);
    }

    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Class<?> clazz : classes) {
            edges.addAll(extractFieldEdges(clazz));
        }
        return EdgeOperations.mergeBiDirectionals(edges);
    }

    private List<Edge> extractFieldEdges(Class<?> clazz) {
        List<Edge> fieldEdges = new ArrayList<>();
        try {
            InputStream is = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace(".", "/") + ".class");
            ClassReader reader = new ClassReader(is);
            reader.accept(new ClassVisitor(Opcodes.ASM4) {
                @Override
                public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                    try {
                        Optional<Edge> fieldEdge = createFieldEdge(clazz, clazz.getDeclaredField(name));
                        if (fieldEdge.isPresent()) {
                            if (EdgeOperations.relationAlreadyExists(fieldEdges, fieldEdge.get())) {
                                Optional<Edge> relation = EdgeOperations.getMatchingRelation(fieldEdges, fieldEdge.get());
                                if (relation.isPresent()) {
                                    fieldEdges.remove(relation.get());
                                    fieldEdges.add(new Edge(relation.get().source, relation.get().target, EdgeType.ONE_TO_MANY, relation.get().direction));
                                }
                            } else{
                                fieldEdges.add(fieldEdge.get());
                            }
                        }
                    } catch (NoSuchFieldException e) {
                        // should never happen
                    } catch (NoClassDefFoundError e) {
                        logger.warn("Skipped field " + name + " in class " + clazz.getName() + " because it's type class is not available. Field description: " + desc);
                    }
                    return super.visitField(access, name, desc, signature, value);
                }

                @Override
                public void visitInnerClass(String name, String outerName, String innerName, int access) {
                    if (innerName == null || outerName == null
                            || name.startsWith("java/")) {
                        // abort if anonymous or standard java class (latter needed because of java.util.MethodHandles)
                        return;
                    }
                    Class<?> outerClass = ReflectionUtils.forName(outerName.replaceAll("/", "."), DomainClassFinder.classLoaders);
                    Class<?> innerClass = ReflectionUtils.forName(name.replaceAll("/", "."), DomainClassFinder.classLoaders);

                    if(!isDomainClass(outerClass) || !isDomainClass(innerClass)) {
                        return;
                    }

                    if (innerClass.equals(outerClass) || clazz.equals(outerClass)) {
                        // To ensure we only add one Relation for each couple, the outerClass relations are thrown aboard
                        return;
                    }

                    Edge innerClassEdge;
                    if ((innerClass.getModifiers() & Modifier.STATIC) > 0) {
                        innerClassEdge = EdgeOperations.createEdge(innerClass, outerClass, EdgeType.STATIC_INNER_CLASS, NAME_FOR_INNERCLASS);
                    } else {
                        innerClassEdge = EdgeOperations.createEdge(innerClass, outerClass, EdgeType.INNER_CLASS, NAME_FOR_INNERCLASS);
                    }

                    if (innerClassEdge != null) {
                        if (!EdgeOperations.relationAlreadyExists(fieldEdges, innerClassEdge)) {
                            fieldEdges.add(innerClassEdge);
                        }
                    }

                    super.visitInnerClass(name, outerName, innerName, access);
                }
            }, ClassReader.SKIP_CODE);
        } catch (IOException e) {
            logger.warn("Failed to read bytecode for class " + clazz.getName(), e);
        }
        return fieldEdges;
    }

    private Optional<Edge> createFieldEdge(Class<?> clazz, Field field) {
        if (field.isEnumConstant()) {
            return empty(); // to prevent self-referencing we ignore all enum constants
        }
        if (isDomainClass(field.getType())) {
            if (!innerClassFieldReferenceInBytecode.equals(field.getName())) {
                return of(EdgeOperations.createEdge(clazz, (Class) field.getType(), EdgeType.ONE_TO_ONE, field.getName()));
            }
        }
        if (isCollection(field)) {
            List<Class<?>> classesInCollection = getDomainClassFromCollection(field);
            for(Class classInCollection : classesInCollection) {
                return of(EdgeOperations.createEdge(clazz, classInCollection, EdgeType.ONE_TO_MANY, field.getName()));
            }
        }
        // TODO Extract Type Arguments too...
        return empty();
    }

    private List<Class<?>> getDomainClassFromCollection(final Field field) {
        List<Class<?>> domainClasses = new ArrayList<>();
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            for (Type t : pt.getActualTypeArguments()) {
                if(t instanceof ParameterizedType) {
                    t = ((ParameterizedType) t).getRawType();
                }
                if (isDomainClass(t.getTypeName())) {
                    domainClasses.add((Class) t);
                }
            }
        }
        return domainClasses;
    }

    private boolean isCollection(final Field field) {
        return Collection.class.isAssignableFrom(field.getType()) ||
                Map.class.isAssignableFrom(field.getType());
    }
}
