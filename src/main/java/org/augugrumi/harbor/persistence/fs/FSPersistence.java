package org.augugrumi.harbor.persistence.fs;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;

import java.io.*;

public class FSPersistence implements Persistence {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(FSPersistence.class);

    private final File home;

    public FSPersistence(String folderName) {
        home = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + folderName);
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RuntimeException("Impossible to create " + folderName + " home. Check your writing " +
                        "permissions.");
            }
        }
    }

    private boolean writeDown(File fileName, String content) {
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(content.getBytes());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Result save(Query q) {

        File toSave = new File(home.getAbsolutePath() + File.separator + q.getId());
        if (toSave.exists()) {
            if (toSave.delete()) {
                return new Result<Void>(writeDown(toSave, q.getContent()));
            } else {
                LOG.warn("Impossible to save file " + q.getId() + ": impossible to delete old entry.");
            }
        }
        try {
            toSave.createNewFile();
            return new Result<Void>(writeDown(toSave, q.getContent()));
        } catch (IOException e) {
            LOG.warn("Impossible to create a new file");
            e.printStackTrace();
            return new Result<Void>(false);
        }
    }

    @Override
    public Result get(Query q) {

        File toRead = new File(home.getAbsolutePath() + File.separator + q.getId());
        try (FileInputStream inputStream = new FileInputStream(toRead)) {
            StringBuilder res = new StringBuilder();
            int i;
            while ((i = inputStream.read()) != -1) {
                res.append((char) i);
            }
            return new Result<String>(true, res.toString());
        } catch (FileNotFoundException e) {
            LOG.warn("Impossible to file the found!");
            e.printStackTrace();
            return new Result<Void>(false);
        } catch (IOException e) {
            LOG.warn("Error while reading the file");
            e.printStackTrace();
            return new Result<Void>(false);
        }
    }

    @Override
    public Result pop(Query q) {

        Result get = get(q);
        File toDelete = new File(home.getAbsolutePath() + File.separator + q.getId());

        if (get.isSuccessful()) {
            Result delete = delete(q);
            if (delete.isSuccessful()) {
                return get;
            } else {
                return new Result(false, get.getContent());
            }
        }

        return get;
    }

    @Override
    public Result<Void> delete(Query q) {

        File toDelete = new File(home.getAbsolutePath() + File.separator + q.getId());

        if (toDelete.delete()) {
            return new Result<Void>(true);
        } else {
            return new Result<Void>(false);
        }
    }

    @Override
    public Result<Void> exists(Query q) {

        File toCheck = new File(home.getAbsolutePath() + File.separator + q.getId());
        return new Result<Void>(toCheck.exists());
    }
}
