package io.loli.util.osm.source;

public class StorageProperties {
    private String type;
    private String key;
    private String secret;
    private String bucket;
    private String region;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public static final class StoragePropertiesBuilder {
        private String type;
        private String key;
        private String secret;
        private String bucket;
        private String region;

        private StoragePropertiesBuilder() {
        }

        public static StoragePropertiesBuilder a() {
            return new StoragePropertiesBuilder();
        }

        public StoragePropertiesBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public StoragePropertiesBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public StoragePropertiesBuilder withSecret(String secret) {
            this.secret = secret;
            return this;
        }

        public StoragePropertiesBuilder withBucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public StoragePropertiesBuilder withRegion(String region) {
            this.region = region;
            return this;
        }

        public StorageProperties build() {
            StorageProperties storageProperties = new StorageProperties();
            storageProperties.setType(type);
            storageProperties.setKey(key);
            storageProperties.setSecret(secret);
            storageProperties.setBucket(bucket);
            storageProperties.setRegion(region);
            return storageProperties;
        }
    }
}
