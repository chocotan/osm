package io.loli.util.osm.source;

import javaslang.Tuple2;
import javaslang.Tuple3;

import java.io.File;
import java.util.List;

public interface Storage {
    public String getName();

    public Tuple3<Boolean, String, List<String>> list(String bucket, String prefix, String marker, Integer size);

    public void downloadToFile(String bucket, String key, File file);

    public void upload(String bucket, String key, File file);

    boolean exist(String bucket, String key);

    void delete(String bucket, String key);
}
