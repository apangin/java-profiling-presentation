package one.classpath;

import one.classfile.ClassFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassPathScanner {
    private final List<ClassFileLoader> loaders = new ArrayList<>();
    private final Map<String, ClassFileLoader> classMap = new LinkedHashMap<>();
    private boolean bootLoader;

    public List<ClassFileLoader> getLoaders() {
        return loaders;
    }

    public ClassFileLoader getLoaderFor(String className) {
        return classMap.get(className);
    }

    public Stream<ClassFile> stream() {
        return classMap.entrySet().stream()
                .map(entry -> {
                    try {
                        return entry.getValue().load(entry.getKey());
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    public void scanBootClassPath() throws IOException {
        bootLoader = true;
        try {
            if (System.getProperty("java.version").startsWith("1.")) {
                scanClassPath(System.getProperty("sun.boot.class.path"));
            } else {
                scanSystemModules();
            }
        } finally {
            bootLoader = false;
        }
    }

    public void scanSystemModules() throws IOException {
        try (DirectoryStream<Path> moduleStream = Files.newDirectoryStream(Paths.get(URI.create("jrt:/modules")))) {
            for (Path module : moduleStream) {
                scanTree(module);
            }
        } catch (FileSystemNotFoundException e) {
            // Modules are not supported
        }
    }

    public void scanSystemClassPath() throws IOException {
        scanClassPath(System.getProperty("jdk.module.upgrade.path"));
        scanClassPath(System.getProperty("jdk.module.path"));
        scanClassPath(System.getProperty("java.class.path"));
    }

    public void scanClassPath(String classPath) throws IOException {
        if (classPath == null) {
            return;
        }

        for (StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator); st.hasMoreElements(); ) {
            String path = st.nextToken().trim();
            if (path.endsWith("*")) {
                scanJarDirectory(path.substring(0, path.length() - 1));
            } else if (path.endsWith("*.jar")) {
                scanJarDirectory(path.substring(0, path.length() - 5));
            } else if (path.endsWith(".jar") || path.endsWith(".zip")) {
                scanJar(path);
            } else {
                scanTree(Paths.get(path));
            }
        }
    }

    public void scanJarDirectory(String jarDir) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(jarDir))) {
            for (Path file : dirStream) {
                if (file.toString().endsWith(".jar") && !Files.isDirectory(file)) {
                    scanJar(file.toString());
                }
            }
        } catch (NoSuchFileException e) {
            // Skip invalid classpath entry
        }
    }

    public void scanJar(String jar) throws IOException {
        ZipFile zip;
        try {
            zip = new ZipFile(jar);
        } catch (FileNotFoundException e) {
            return;
        }

        try {
            JarLoader loader = new JarLoader(zip, bootLoader);
            loaders.add(loader);

            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName();
                    className = className.substring(0, className.length() - 6);
                    registerClass(className, loader);
                }
            }
        } catch (Throwable e) {
            zip.close();
            throw e;
        }
    }

    public void scanTree(Path base) throws IOException {
        PathLoader loader = new PathLoader(base, bootLoader);
        loaders.add(loader);

        try {
            Files.walkFileTree(base, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".class")) {
                        String className = base.relativize(file).toString();
                        className = className.replace('\\', '/');
                        className = className.substring(0, className.length() - 6);
                        registerClass(className, loader);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (NoSuchFileException e) {
            // Skip invalid classpath entry
        }
    }

    private void registerClass(String className, ClassFileLoader loader) {
        loader.registerClass(className);
        classMap.putIfAbsent(className, loader);
    }
}
