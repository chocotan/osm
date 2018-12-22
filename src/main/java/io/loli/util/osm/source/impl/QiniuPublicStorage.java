package io.loli.util.osm.source.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import io.loli.util.osm.source.Storage;
import io.loli.util.osm.source.StorageProperties;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QiniuPublicStorage implements Storage {
    protected BucketManager client;
    protected UploadManager uploadManager;
    protected StorageProperties properties;
    protected Auth auth;

    @Override
    public Tuple3<Boolean, String, List<String>> list(String bucket, String prefix, String marker, Integer size) {

        FileListing objectListing = null;
        try {
            objectListing = client.listFiles(bucket, "", marker, size, null);
            return Tuple.of(objectListing.isEOF(), objectListing.marker, Arrays.stream(objectListing.items)
                    .map(i -> i.key).collect(Collectors.toList()));
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }

    public QiniuPublicStorage(StorageProperties properties) {
        this.properties = properties;
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(getZone(properties.getRegion()));
        auth = Auth.create(properties.getKey(), properties.getSecret());
        client = new BucketManager(auth, cfg);
        uploadManager = new UploadManager(cfg);
    }

    @Override
    public String getName() {
        return "七牛公有";
    }

    @Override
    public void downloadToFile(String bucket, String key, File file) {
        try {
            String domainOfBucket = properties.getEndpoint();
            String encodedFileName;
            encodedFileName = URLEncoder.encode(key, "utf-8");
            String finalUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
            FileUtils.copyURLToFile(new URL(finalUrl), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void upload(String bucket, String key, File file) {
        String upToken = auth.uploadToken(bucket);
        try {
            uploadManager.put(file.getPath(), key, upToken);
        } catch (QiniuException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public boolean exist(String bucket, String key) {
        FileListing objectListing = null;
        try {
            objectListing = client.listFiles(bucket, key, null, 1, null);
            return objectListing.items != null && objectListing.items.length > 0;
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String bucket, String key) {
        try {
            client.delete(bucket, key);
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }


    private Zone getZone(String region) {
        try {
            return (Zone) Zone.class.getMethod(region).invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return Zone.autoZone();
        }
    }
}
