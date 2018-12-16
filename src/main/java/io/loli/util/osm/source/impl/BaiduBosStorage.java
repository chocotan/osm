package io.loli.util.osm.source.impl;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.GetObjectRequest;
import com.baidubce.services.bos.model.ListObjectsRequest;
import com.baidubce.services.bos.model.ListObjectsResponse;
import io.loli.util.osm.source.Storage;
import io.loli.util.osm.source.StorageProperties;
import javaslang.Tuple;
import javaslang.Tuple2;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class BaiduBosStorage implements Storage {
    private BosClient client;

    public BaiduBosStorage(StorageProperties properties) {
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(properties.getKey(), properties.getSecret()));
        config.setEndpoint(properties.getRegion());
        client = new BosClient(config);
    }

    @Override
    public String getName() {
        return "百度BOS";
    }

    @Override
    public Tuple2<String, List<String>> list(String bucket, String prefix, String marker, Integer size) {
        ListObjectsRequest request = new ListObjectsRequest(bucket).withMarker(marker)
                .withMaxKeys(size);
        ListObjectsResponse objectListing = client.listObjects(request);
        if (!objectListing.isTruncated()) {
            return null;
        }
        return Tuple.of(objectListing.getNextMarker(), objectListing.getContents().stream()
                .map(BosObjectSummary::getKey).collect(Collectors.toList()));
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
