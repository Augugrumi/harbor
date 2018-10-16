package org.augugrumi.harbor.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static File createTmpFile(String prefix, String suffix, String content) throws IOException {
        File tmpToFill = File.createTempFile(prefix, suffix);
        FileOutputStream fos = new FileOutputStream(tmpToFill);
        fos.write(content.getBytes());
        fos.close();
        return tmpToFill;
    }
}
