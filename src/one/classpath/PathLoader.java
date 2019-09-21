package one.classpath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class PathLoader extends ClassFileLoader {
    private final Path base;

    PathLoader(Path base, boolean bootLoader) {
        super(bootLoader);
        this.base = base;
    }

    @Override
    byte[] loadResource(String resourceName) throws IOException {
        return Files.readAllBytes(base.resolve(resourceName));
    }

    @Override
    public String toString() {
        return "path:" + base;
    }
}
