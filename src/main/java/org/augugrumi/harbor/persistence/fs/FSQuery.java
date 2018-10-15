package org.augugrumi.harbor.persistence.fs;

import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FSQuery implements Query {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(FSQuery.class);

    private final File file;

    public FSQuery(File fs) {
        if (fs.exists()) {
            file = fs;
        } else {
            throw new RuntimeException("File must exist");
        }
    }

    @Override
    public String getId() {
        return file.getName();
    }

    @Override
    public String getContent() {

        try (FileInputStream inputStream = new FileInputStream(file)) {

            StringBuilder res = new StringBuilder();

            int i;
            while ((i = inputStream.read()) != -1) {
                res.append((char) i);
            }

            return res.toString();

        } catch (FileNotFoundException e) {
            LOG.warn("Error while reading " + file.getName() + ". File not found");
            e.printStackTrace();
        } catch (IOException e) {
            LOG.warn("Error while reading " + file.getName() + ". IOException");
            e.printStackTrace();
        }
        return "";
    }
}
