package demo10;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This test demonstrates a native memory leak caused by
 * unclosed InputStream.
 * Async-profiler's ability to profile native functions
 * helps to track the memory leak down to the origin
 * in Java code, i.e. getResourceAsStream().
 *
 * Run async-profiler with `-e malloc` and `-e mprotect`.
 */
@Controller
public class GetImage {

    @RequestMapping("/getImage")
    public byte[] getImage(@RequestParam("id") String id) throws IOException  {
        InputStream in = getClass().getResourceAsStream("/img/" + id + ".png");
        if (in == null) {
            throw new ResourceNotFoundException();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyStream(in, out);
        return out.toByteArray();
    }

    private void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        for (int bytes; (bytes = in.read(buf)) >= 0; ) {
            out.write(buf, 0, bytes);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class ResourceNotFoundException extends RuntimeException {
    }

    public static void main(String[] args) throws Exception {
        GetImage controller = new GetImage();
        while (true) {
            controller.getImage("1");
        }
    }
}
