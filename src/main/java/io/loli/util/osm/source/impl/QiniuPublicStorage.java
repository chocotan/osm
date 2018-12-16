package io.loli.util.osm.source.impl;

import com.aliyun.oss.model.OSSObjectSummary;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QiniuPublicStorage implements Storage {
    private BucketManager client;
    private UploadManager uploadManager;
    private StorageProperties properties;

    @Override
    public Tuple2<String, List<String>> list(String bucket, String prefix, String marker, Integer size) {

        FileListing objectListing = null;
        try {
            objectListing = client.listFiles(bucket, "", marker, size, null);
            if (!objectListing.isEOF()) {
                return null;
            }
            return Tuple.of(objectListing.marker, Arrays.stream(objectListing.items)
                    .map(i -> i.key).collect(Collectors.toList()));
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }

    public QiniuPublicStorage(StorageProperties properties) {
        this.properties = properties;
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        Auth auth = Auth.create(properties.getKey(), properties.getSecret());
        client = new BucketManager(auth, cfg);
    }

    @Override
    public String getName() {
        return "七牛公有";
    }

    @Override
    public void downloadToFile(String bucket, String key, File file) {
        try {
            String fileName = key;
            String domainOfBucket = properties.getRegion();
            String encodedFileName = null;
            encodedFileName = URLEncoder.encode(fileName, "utf-8");
            String finalUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
            FileUtils.copyURLToFile(new URL(finalUrl), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void upload(String bucket, String key, File file) {
//        client.putObject(bucket, key, file);
    }

    @Override
    public boolean exist(String bucket, String key) {
//        return client. (bucket, key);
        return true;
    }

    public void delete(String bucket, String key) {
        try {
            client.delete(bucket, key);
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }
}
