package io.loli.util.osm.source.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.loli.util.osm.source.Storage;
import io.loli.util.osm.source.StorageProperties;
import javaslang.Tuple;
import javaslang.Tuple3;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class JdOssStorage implements Storage {
    @Override
    public String getName() {
        return "京东OSS";
    }

    private AmazonS3 client;

    public JdOssStorage(StorageProperties properties) {
        final String accessKey = properties.getKey();
        final String secretKey = properties.getSecret();
        final String endpoint = properties.getEndpoint();
        System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
        ClientConfiguration config = new ClientConfiguration();

        AwsClientBuilder.EndpointConfiguration endpointConfig =
                new AwsClientBuilder.EndpointConfiguration(endpoint, properties.getRegion());

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        client = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfig)
                .withClientConfiguration(config)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .withPathStyleAccessEnabled(true)
                .build();
    }


    @Override
    public Tuple3<Boolean, String, List<String>> list(String bucket, String prefix, String marker, Integer size) {
        ListObjectsRequest request = new ListObjectsRequest().withBucketName(bucket)
                .withMarker(marker)
                .withMaxKeys(size);
        ObjectListing objectListing = client.listObjects(request);
        return Tuple.of(objectListing.isTruncated(), objectListing.getNextMarker(), objectListing.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey).collect(Collectors.toList()));
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
