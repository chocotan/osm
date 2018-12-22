# 云存储迁移工具
---

## 功能
1. 支持阿里OSS、腾讯COS、百度BOS、京东OSS、华为OBS、七牛、又拍的互相迁移
2. 批量删除bucket文件

## 打包
```bash
mvn clean assembly:single
```

## 截图
![image.png](http://b1.loli.io/images/e0gox.png)

## 
1. 界面的`导出`按钮暂时没啥用
2. 暂不支持失败重推
3. 本程序是先将文件下载到本地再上传，会消耗流量
4. 请根据自己带宽合理设置线程数
5. 代码不漂亮，甚至还有中文变量名，请勿吐槽
6. 功能如有问题，请在issue中写明自己的操作流程，或者你直接改代码

## 其他
1. BAT、华为、京东的sdk很像，代码复制粘贴就完事了，我怀疑他们是不是互相抄袭的
2. 又拍和七牛的都特立独行，尤其是七牛，居然还区分公开和私有，更加离谱的是，不同的区域要调用不同的方法，都不能直接传个参数进去


## Region和endpoint

> 注意所有的endpoint必须以`http://`开头，不能以`/`结尾


### 阿里OSS
阿里OSS不需要填写region，只需要填写endpoint，点击 https://help.aliyun.com/document_detail/31837.html

### 腾讯COS
腾讯直接填入region，不需要endpoint： https://cloud.tencent.com/document/product/436/6224

### 百度BOS
百度和阿里一样，不须要region，使用endpoint： https://cloud.baidu.com/doc/BOS/Java-SDK.html

### 华为OBS
华为也是不需要region，使用endpoint： https://support.huaweicloud.com/devg-obs/zh-cn_topic_0105713153.html 

### 京东OSS
京东两者都需要，使用s3 endpoint：https://docs.jdcloud.com/cn/object-storage-service/regions-and-endpoints

### 七牛
七牛需要填写region和endpoint等，https://developer.qiniu.com/kodo/sdk/1239/java#4

region可以不填写，将自动识别

### 又拍云
又拍云无需填写region和endpoint