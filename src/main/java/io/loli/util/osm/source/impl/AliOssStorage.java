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

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AliOssStorage implements Storage {
    private OSSClient client;

    public AliOssStorage(StorageProperties properties) {
        client = new OSSClient(properties.getRegion(), properties.getKey(), properties.getSecret());
    }

    @Override
    public String getName() {
        return "阿里OSS";
    }

    @Override
    public Tuple2<String, List<String>> list(String bucket, String prefix, String marker, Integer size) {
        ListObjectsRequest request = new ListObjectsRequest(bucket).withMarker(marker)
                .withMaxKeys(size);
        ObjectListing objectListing = client.listObjects(request);
        if (!objectListing.isTruncated()) {
            return null;
        }
        return Tuple.of(objectListing.getNextMarker(), objectListing.getObjectSummaries().stream()
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
        return client.doesObjectExist(bucket,key);
    }

    public void delete(String bucket, String key){
        client.deleteObject(bucket, key);
    }
}
