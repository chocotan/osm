package io.loli.util.osm.source.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.region.Region;
import io.loli.util.osm.source.Storage;
import io.loli.util.osm.source.StorageProperties;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class TencentCosStorage implements Storage {
    private COSClient client;

    public TencentCosStorage(StorageProperties properties) {
        COSCredentials cred = new BasicCOSCredentials(properties.getKey(), properties.getSecret());
        ClientConfig clientConfig = new ClientConfig(new Region(properties.getRegion()));
        client = new COSClient(cred, clientConfig);
    }

    @Override
    public String getName() {
        return "腾讯COS";
    }

    @Override
    public Tuple3<Boolean, String, List<String>> list(String bucket, String prefix, String marker, Integer size) {
        ListObjectsRequest request = new ListObjectsRequest().withBucketName(bucket)
                .withMarker(marker)
                .withMaxKeys(size);
        ObjectListing objectListing = client.listObjects(request);
        return Tuple.of(objectListing.isTruncated(), objectListing.getNextMarker(), objectListing.getObjectSummaries().stream()
                .map(COSObjectSummary::getKey).collect(Collectors.toList()));
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

    @Override
    public void delete(String bucket, String key) {
        client.deleteObject(bucket, key);
    }
}
