package one.classpath;

import one.classfile.ClassFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ClassFileLoader {
    private final boolean bootLoader;
    private final List<String> classes;

    ClassFileLoader(boolean bootLoader) {
        this.bootLoader = bootLoader;
        this.classes = new ArrayList<>();
    }

    public boolean isBootLoader() {
        return bootLoader;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void registerClass(String className) {
        classes.add(className);
    }

    public ClassFile load(String className) throws IOException {
        return new ClassFile(loadResource(className + ".class"));
    }

    public void close() {
        // To be overridden
    }

    abstract byte[] loadResource(String resourceName) throws IOException;
}
