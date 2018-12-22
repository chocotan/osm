package io.loli.util.osm.source.impl;

import com.obs.services.ObsClient;
import com.obs.services.model.*;
import io.loli.util.osm.source.Storage;
import io.loli.util.osm.source.StorageProperties;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class HuaweiObsStorage implements Storage {
    private ObsClient client;

    public HuaweiObsStorage(StorageProperties properties) {
        client = new ObsClient(properties.getKey(), properties.getSecret(), properties.getEndpoint());
    }

    @Override
    public String getName() {
        return "华为OBS";
    }

    @Override
    public Tuple3<Boolean, String, List<String>> list(String bucket, String prefix, String marker, Integer size) {
        ListObjectsRequest request = new ListObjectsRequest(bucket);
        request.setMarker(marker);
        request.setMaxKeys(size);
        ObjectListing objectListing = client.listObjects(request);

        return Tuple.of(objectListing.isTruncated(), objectListing.getNextMarker(), objectListing.getObjects().stream()
                .map(ObsObject::getObjectKey).collect(Collectors.toList()));
    }

    @Override
    public void downloadToFile(String bucket, String key, File file) {

        try {
            client.downloadFile(new DownloadFileRequest(bucket, key, file.getPath()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void upload(String bucket, String key, File file) {
        client.putObject(bucket, key, file);
    }

    @Override
    public boolean exist(String bucket, String key) {
        try {
            ObsObject object = client.getObject(bucket, key);
            return object != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void delete(String bucket, String key) {
        client.deleteObject(bucket, key);
    }

}
