package io.loli.util.osm.source;

import io.loli.util.osm.source.impl.AliOssStorage;
import io.loli.util.osm.source.impl.BaiduBosStorage;
import io.loli.util.osm.source.impl.TencentCosStorage;

public class StorageFactory {
    public static Storage getStorage(StorageProperties properties) {
        switch (properties.getType()) {
            case "阿里OSS":
                return new AliOssStorage(properties);
            case "腾讯COS":
                return new TencentCosStorage(properties);
            case "百度BOS":
                return new BaiduBosStorage(properties);
        }
        return null;
    }
}
