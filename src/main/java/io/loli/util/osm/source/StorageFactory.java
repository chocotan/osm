package io.loli.util.osm.source;

import io.loli.util.osm.source.impl.*;

public class StorageFactory {
    public static Storage getStorage(StorageProperties properties) {
        switch (properties.getType()) {
            case "阿里OSS":
                return new AliOssStorage(properties);
            case "腾讯COS":
                return new TencentCosStorage(properties);
            case "百度BOS":
                return new BaiduBosStorage(properties);
            case "华为OBS":
                return new HuaweiObsStorage(properties);
            case "京东OSS":
                return new JdOssStorage(properties);
            case "七牛公有":
                return new QiniuPublicStorage(properties);
            case "七牛私有":
                return new QiniuPrivateStorage(properties);
            case "又拍云":
                return new UpaiStorage(properties);
        }
        return null;
    }
}
