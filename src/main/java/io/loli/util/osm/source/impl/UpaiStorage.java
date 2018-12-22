package io.loli.util.osm.source.impl;

import com.UpYun;
import com.upyun.UpException;
import io.loli.util.osm.source.Storage;
import io.loli.util.osm.source.StorageProperties;
import javaslang.Tuple;
import javaslang.Tuple3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpaiStorage implements Storage {
    private UpYun client;

    public UpaiStorage(StorageProperties properties) {
        client = new UpYun(properties.getBucket(), properties.getKey(), properties.getSecret());
    }

    @Override
    public String getName() {
        return "又拍云";
    }

    @Override
    public Tuple3<Boolean, String, List<String>> list(String bucket, String prefix, String marker, Integer size) {
        try {
            List<UpYun.FolderItem> folderItems = client.readDir(prefix);
            return Tuple.of(true, null, folderItems.stream().map(i -> i.name).collect(Collectors.toList()));
        } catch (IOException | UpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadToFile(String bucket, String key, File file) {
        try {
            client.writeFile(key, file);
        } catch (IOException | UpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void upload(String bucket, String key, File file) {
        try {
            client.writeFile("/" + key, file, true);
        } catch (IOException | UpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exist(String bucket, String key) {
        Map<String, String> fileInfo = null;
        try {
            fileInfo = client.getFileInfo("/" + key);
        } catch (IOException | UpException e) {
            if (e instanceof FileNotFoundException) {
                return false;
            }
            throw new RuntimeException(e);
        }
        return fileInfo != null;
    }

    @Override
    public void delete(String bucket, String key) {
        try {
            client.deleteFile("/" + key);
        } catch (IOException | UpException e) {
            throw new RuntimeException(e);
        }
    }
}
