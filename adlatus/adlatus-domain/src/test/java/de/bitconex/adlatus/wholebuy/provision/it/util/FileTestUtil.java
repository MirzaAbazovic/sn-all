package de.bitconex.adlatus.wholebuy.provision.it.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileTestUtil {

    public static String readResourceContent(String path) throws IOException {
        File responsePayloadFile = new File(
                Objects.requireNonNull(FileTestUtil.class.getClassLoader().getResource(path)).getFile());

        return Files.readString(Path.of(responsePayloadFile.getPath()), StandardCharsets.UTF_8);
    }
}
