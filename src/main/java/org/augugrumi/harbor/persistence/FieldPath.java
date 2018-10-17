package org.augugrumi.harbor.persistence;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FieldPath implements Iterable<String> {

    private static final String DELIMITER = ".";

    private final List<String> paths;

    public FieldPath(String path) {
        paths = Arrays.asList(path.split(DELIMITER));
    }

    public FieldPath(List<String> paths) {
        this.paths = paths;
    }

    public int size() {
        return paths.size();
    }

    public String get(int i) {
        return paths.get(i);
    }

    @Override
    public Iterator<String> iterator() {
        return paths.iterator();
    }
}
