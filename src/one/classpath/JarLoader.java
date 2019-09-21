package one.classpath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class JarLoader extends ClassFileLoader {
    private final ZipFile zip;

    JarLoader(ZipFile zip, boolean bootLoader) {
        super(bootLoader);
        this.zip = zip;
    }

    @Override
    byte[] loadResource(String resourceName) throws IOException {
        ZipEntry entry = zip.getEntry(resourceName);
        if (entry == null) {
            throw new FileNotFoundException(resourceName + " not found in " + zip.getName());
        }

        byte[] buf = new byte[(int) entry.getSize()];
        try (InputStream in = zip.getInputStream(entry)) {
            for (int offset = 0; offset < buf.length; ) {
                int bytes = in.read(buf, offset, buf.length - offset);
                if (bytes < 0) {
                    throw new IOException("Unexpected end of stream while reading " + resourceName);
                }
                offset += bytes;
            }
        }
        return buf;
    }

    @Override
    public void close() {
        try {
            zip.close();
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public String toString() {
        return "jar:" + zip.getName();
    }
}
