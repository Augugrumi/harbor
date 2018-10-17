package org.augugrumi.harbor.persistence.fs;

import org.augugrumi.harbor.persistence.FieldPath;
import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.exception.DbException;
import org.augugrumi.harbor.persistence.query.SimpleQuery;
import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private boolean writeDown(File fileName, String content) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(content.getBytes());
            return true;
        }
    }

    @Override
    public Result<Void> save(Query q) throws DbException {

        File toSave = new File(home.getAbsolutePath() + File.separator + q.getID());
        try {
            if (toSave.exists()) {
                if (toSave.delete()) {
                    return new Result<Void>(writeDown(toSave, q.getContent()));
                } else {
                    LOG.warn("Impossible to save file " + q.getID() + ": impossible to delete old entry.");
                }
            }
            toSave.createNewFile();
            return new Result<Void>(writeDown(toSave, q.getContent()));
        } catch (IOException e) {
            throw new DbException("Impossible to create a new file");
        }
    }

    @Override
    public Result<JSONObject> get(Query q) throws DbException {

        File toRead = new File(home.getAbsolutePath() + File.separator + q.getID());
        try (FileInputStream inputStream = new FileInputStream(toRead)) {
            StringBuilder res = new StringBuilder();
            int i;
            while ((i = inputStream.read()) != -1) {
                res.append((char) i);
            }
            return new Result<JSONObject>(true, new JSONObject(res.toString()));
        } catch (FileNotFoundException e) {
            throw new DbException("Impossible to found the file " + q.getID());
        } catch (IOException e) {
            throw new DbException("Error while reading the file " + q.getID());
        }
    }

    @Override
    public Result<List<JSONObject>> get() {
        final File[] elements = home.listFiles();
        final List<JSONObject> res = new ArrayList<>();
        if (elements != null) {
            for (File f : elements) {
                if (f.isFile()) {
                    res.add(new JSONObject().put(Fields.ID, f.getName()));
                }
            }
        }
        return new Result<>(true, res);
    }

    @Override
    public Result<Boolean> update(Query q) throws DbException {

        synchronized (this) {
            Result<Boolean> deleteRes = delete(q);
            if (deleteRes.isSuccessful()) {
                if (deleteRes.getContent()) {
                    Result<Void> saveRes = save(q);
                    if (saveRes.isSuccessful()) {
                        return new Result<>(true, true);
                    } else {
                        throw new DbException("Impossible to save data in the DB. The previous data have been lost");
                    }
                } else {
                    return new Result<>(false);
                }
            } else {
                throw new DbException("Impossible to delete data from the DB");
            }
        }
    }

    @Override
    public Result<Boolean> update(SimpleQuery q, Map<FieldPath, Object> toUpdate) throws DbException {

        synchronized (this) {
            Result<JSONObject> dataToUpdate = get(q);
            if (dataToUpdate.isSuccessful()) {
                JSONObject payload = dataToUpdate.getContent();
                // For every key in the map
                for (final Map.Entry<FieldPath, Object> entry : toUpdate.entrySet()) {
                    // Iterate over every step in the JSON path
                    JSONObject path = payload;
                    for (int i = 0; i < entry.getKey().size() - 2; i++) {
                        final String key = entry.getKey().get(i);
                        path = path.getJSONObject(key);
                    }
                    // Now path has the element we need to update
                    final String lastKey = entry.getKey().get(entry.getKey().size() - 1);
                    path.put(lastKey, entry.getValue().toString());
                }
                return new Result<Boolean>(true, true);
            } else {
                return new Result<Boolean>(false, false);
            }
        }
    }

    @Override
    public Result<JSONObject> pop(Query q) {

        Result<JSONObject> get = get(q);
        File toDelete = new File(home.getAbsolutePath() + File.separator + q.getID());

        if (get.isSuccessful()) {
            Result<Boolean> delete = delete(q);
            if (delete.isSuccessful() && delete.getContent()) {
                return get;
            } else {
                return new Result<JSONObject>(false, get.getContent());
            }
        }

        return get;
    }

    @Override
    public Result<Boolean> delete(Query q) {

        File toDelete = new File(home.getAbsolutePath() + File.separator + q.getID());

        return new Result<Boolean>(true, toDelete.delete());
    }

    @Override
    public Result<Boolean> exists(Query q) {

        File toCheck = new File(home.getAbsolutePath() + File.separator + q.getID());
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
