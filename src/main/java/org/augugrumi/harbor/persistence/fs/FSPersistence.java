package org.augugrumi.harbor.persistence.fs;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
            LOG.warn("Impossible to found the file " + q.getId() + "!");
            return new Result<Integer>(false, -1);
        } catch (IOException e) {
            LOG.warn("Error while reading the file " + q.getId() + "!");
            return new Result<Integer>(false, -2);
        }
    }

    @Override
    public List<Result<String>> get() {

        final File[] elements = home.listFiles();
        final List<Result<String>> res = new ArrayList<>();
        if (elements != null) {
            for (File f : elements) {
                if (f.isFile()) {
                    res.add(new Result<>(true, f.getName()));
                }
            }
        }

        return res;
    }

    @Override
    public Result pop(Query q) {

        Result get = get(q);
        File toDelete = new File(home.getAbsolutePath() + File.separator + q.getId());

        if (get.isSuccessful()) {
            Result<Boolean> delete = delete(q);
            if (delete.isSuccessful() && delete.getContent()) {
                return get;
            } else {
                return new Result(false, get.getContent());
            }
        }

        return get;
    }

    @Override
    public Result<Boolean> delete(Query q) {

        File toDelete = new File(home.getAbsolutePath() + File.separator + q.getId());

        return new Result<Boolean>(true, toDelete.delete());
    }

    @Override
    public Result<Boolean> exists(Query q) {

        File toCheck = new File(home.getAbsolutePath() + File.separator + q.getId());
        return new Result<Boolean>(true, toCheck.exists());
    }

    @Override
    public Result<List<Boolean>> exists(List<Query> queryList) {
        ArrayList<Boolean> allQueryRes = new ArrayList<>();
        for (final Query q : queryList) {
            Result<Boolean> qRes = exists(q);

            if (qRes.isSuccessful()) {
                allQueryRes.add(qRes.getContent());
            } else {
                return new Result<List<Boolean>>(false);
            }
        }
        return new Result<List<Boolean>>(true, allQueryRes);
    }
}
