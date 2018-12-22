package io.loli.util.osm.source.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import io.loli.util.osm.source.Storage;
import io.loli.util.osm.source.StorageProperties;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

public class AliOssStorage implements Storage {
    private OSSClient client;

    public AliOssStorage(StorageProperties properties) {
        client = new OSSClient(properties.getEndpoint(), properties.getKey(), properties.getSecret());
    }

    @Override
    public String getName() {
        return "阿里OSS";
    }

    @Override
    public Tuple3<Boolean, String, List<String>> list(String bucket, String prefix, String marker, Integer size) {
        ListObjectsRequest request = new ListObjectsRequest(bucket).withMarker(marker)
                .withMaxKeys(size);
        ObjectListing objectListing = client.listObjects(request);
        return Tuple.of(objectListing.isTruncated(), objectListing.getNextMarker(), objectListing.getObjectSummaries().stream()
                .map(OSSObjectSummary::getKey).collect(Collectors.toList()));
    }

    @Override
    public void downloadToFile(String bucket, String key, File file) {
        client.getObject(new GetObjectRequest(bucket, key), file);
    }

    @Override
    public void upload(String bucket, String key, File file) {
        client.putObject(bucket, key, file);
    }

    @Override
    public boolean exist(String bucket, String key) {
        return client.doesObjectExist(bucket, key);
    }

    public void delete(String bucket, String key) {
        client.deleteObject(bucket, key);
    }

}
