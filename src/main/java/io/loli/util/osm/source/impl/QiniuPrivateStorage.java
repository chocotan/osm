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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QiniuPrivateStorage extends QiniuPublicStorage {

    public QiniuPrivateStorage(StorageProperties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "七牛私有";
    }

    @Override
    public void downloadToFile(String bucket, String key, File file) {
       try {
            String domainOfBucket = properties.getEndpoint();
            String encodedFileName = URLEncoder.encode(key, "utf-8");
            String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
            String finalUrl = auth.privateDownloadUrl(publicUrl);
            FileUtils.copyURLToFile(new URL(finalUrl), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
