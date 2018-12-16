package io.loli.util.osm.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.loli.util.osm.source.Storage;
import io.loli.util.osm.source.StorageFactory;
import io.loli.util.osm.source.StorageProperties;
import javaslang.Tuple2;
import org.apache.commons.collections4.QueueUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OsmUi extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JComboBox type1;
    private JTextField region1;
    private JCheckBox 是否覆盖CheckBox;
    private JButton 开始Button;
    private JButton 暂停Button;
    private JButton 停止Button;
    private JButton 导出错误Button;
    private JTextField key1;
    private JTextField secret1;
    private JTextField bucket1;
    private JTextField region2;
    private JTextField key2;
    private JTextField secret2;
    private JTextField bucket2;
    private JComboBox type2;
    private JTextArea textArea1;
    private JLabel statusBarLabel;
    private JButton 批量删除Button;
    private JButton 批量删除Button1;
    private JTextField threadNumberfield;
    private Task task;

    public OsmUi() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(450, 600));
        setPreferredSize(new Dimension(450, 600));
        setResizable(false);
        add(panel1);
        setVisible(true);
        this.repaint();
        暂停Button.setEnabled(false);
        停止Button.setEnabled(false);
        开始Button.addActionListener(e -> {
            if (startPrepare()) return;
            task.setFromProperties(fromProperties());
            task.setToProperties(toProperties());
            task.start();
        });

        暂停Button.addActionListener(e -> {
            if ("暂停".equals(暂停Button.getText())) {
                暂停Button.setText("继续");
            } else if ("继续".equals(暂停Button.getText())) {
                暂停Button.setText("暂停");
            }
            task.pauseOrResume();
        });
        停止Button.addActionListener(e -> {
            开始Button.setEnabled(true);
            批量删除Button.setEnabled(true);
            批量删除Button1.setEnabled(true);
            暂停Button.setEnabled(false);
            停止Button.setEnabled(false);
            导出错误Button.setEnabled(true);
            task.stop();
        });

        批量删除Button.addActionListener(e -> {
            if (startPrepare()) return;
            task.setFromProperties(fromProperties());
            task.setToProperties(fromProperties());
            task.delete1();
        });
        批量删除Button1.addActionListener(e -> {
            if (startPrepare()) return;
            task.setFromProperties(toProperties());
            task.setToProperties(toProperties());
            task.delete2();
        });
        loadConfig();
    }

    private boolean startPrepare() {
        if (!checkValue()) {
            return true;
        }
        saveConfig();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", threadNumberfield.getText());
        开始Button.setEnabled(false);
        暂停Button.setEnabled(true);
        停止Button.setEnabled(true);
        导出错误Button.setEnabled(true);
        批量删除Button.setEnabled(false);
        批量删除Button1.setEnabled(false);
        textArea1.setText("");
        task = new Task();
        return false;
    }

    private StorageProperties fromProperties() {
        return StorageProperties.StoragePropertiesBuilder.a()
                .withType(type1.getSelectedItem().toString())
                .withBucket(bucket1.getText())
                .withKey(key1.getText())
                .withSecret(secret1.getText())
                .withRegion(region1.getText())
                .build();
    }

    private StorageProperties toProperties() {
        return StorageProperties.StoragePropertiesBuilder.a()
                .withType(type2.getSelectedItem().toString())
                .withBucket(bucket2.getText())
                .withKey(key2.getText())
                .withSecret(secret2.getText())
                .withRegion(region2.getText())
                .build();
    }

    private void saveConfig() {
        try {
            String type1Str = type1.getSelectedItem().toString();
            String type2Str = type2.getSelectedItem().toString();
            String key1Str = key1.getText();
            String key2Str = key2.getText();
            String bucket1Str = bucket1.getText();
            String bucket2Str = bucket2.getText();
            String secret1Str = secret1.getText();
            String secret2Str = secret2.getText();
            String region1Str = region1.getText();
            String region2Str = region2.getText();
            JSONObject cfg = new JSONObject();
            cfg.put("type1", type1Str);
            cfg.put("type2", type2Str);
            cfg.put("key1", key1Str);
            cfg.put("key2", key2Str);
            cfg.put("secret1", secret1Str);
            cfg.put("secret2", secret2Str);
            cfg.put("bucket1", bucket1Str);
            cfg.put("bucket2", bucket2Str);
            cfg.put("region1", region1Str);
            cfg.put("region2", region2Str);
            String json = cfg.toString();
            String cfgFilePath = System.getProperty("java.io.tmpdir") + File.separator + "osmcfg.json";
            FileUtils.writeStringToFile(new File(cfgFilePath), json, "UTF-8");
        } catch (Exception ignored) {
        }
    }

    private void loadConfig() {
        try {
            String cfgFilePath = System.getProperty("java.io.tmpdir") + File.separator + "osmcfg.json";
            String s = FileUtils.readFileToString(new File(cfgFilePath), "UTF-8");
            JSONObject cfg = JSON.parseObject(s);
            String type1Str = cfg.getString("type1");
            String type2Str = cfg.getString("type2");
            String key1Str = cfg.getString("key1");
            String key2Str = cfg.getString("key2");
            String bucket1Str = cfg.getString("bucket1");
            String bucket2Str = cfg.getString("bucket2");
            String secret1Str = cfg.getString("secret1");
            String secret2Str = cfg.getString("secret2");
            String region1Str = cfg.getString("region1");
            String region2Str = cfg.getString("region2");
            type1.setSelectedItem(type1Str);
            type2.setSelectedItem(type2Str);
            key1.setText(key1Str);
            key2.setText(key2Str);
            region1.setText(region1Str);
            region2.setText(region2Str);
            secret1.setText(secret1Str);
            secret2.setText(secret2Str);
            bucket1.setText(bucket1Str);
            bucket2.setText(bucket2Str);
            repaint();
            revalidate();
        } catch (Exception ignored) {
        }
    }

    private boolean checkValue() {
        if (StringUtils.isBlank(region1.getText()) ||
                StringUtils.isBlank(region2.getText()) ||
                StringUtils.isBlank(key1.getText()) ||
                StringUtils.isBlank(key2.getText()) ||
                StringUtils.isBlank(secret1.getText()) ||
                StringUtils.isBlank(secret2.getText()) ||
                StringUtils.isBlank(bucket1.getText()) ||
                StringUtils.isBlank(bucket2.getText())) {
            JOptionPane.showMessageDialog(this, "参数不能为空",
                    "检查参数",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private class Task {

        private AtomicInteger successCount = new AtomicInteger();
        private AtomicInteger failedCount = new AtomicInteger();
        private final Queue<String> log = QueueUtils.synchronizedQueue(new CircularFifoQueue<>(1000));
        private Map<String, String> failedReason = new HashMap<>();
        private StorageProperties fromProperties;
        private StorageProperties toProperties;
        private Storage from;
        private Storage to;
        private ExecutorService exe = Executors.newFixedThreadPool(2);
        private ExecutorService lockPool = Executors.newSingleThreadExecutor();
        private ScheduledExecutorService schedule = Executors.newScheduledThreadPool(2);
        private final Object pauseLock = new Object();
        private AtomicBoolean pauseStatus = new AtomicBoolean(false);
        private AtomicBoolean stopStatus = new AtomicBoolean(false);

        private void start() {
            listAndAction(key -> {
                try {
                    downloadAndUpload(key, 是否覆盖CheckBox.isSelected());
                } catch (IOException ignored) {
                }
            });
        }

        private void listAndAction(Consumer<String> action) {
            from = StorageFactory.getStorage(fromProperties);
            to = StorageFactory.getStorage(toProperties);
            schedule.scheduleAtFixedRate(() -> {
                statusBarLabel.setText(successCount.get() + "/" + failedCount.get());
                synchronized (log) {
                    textArea1.setText(log.stream().sorted(Collections.reverseOrder()).collect(Collectors.joining("\r\n")));
                }
                textArea1.repaint();
                textArea1.revalidate();
            }, 1, 1, TimeUnit.SECONDS);
            exe.execute(new Runnable() {
                @Override
                public void run() {
                    String lastMarker = null;
                    for (; !stopStatus.get(); ) {
                        try {
                            // 检查暂停状态，如果已暂停，则等待唤醒
                            if (pauseStatus.get()) {
                                waitForNotify();
                            }
                            Tuple2<String, List<String>> list = from.list(fromProperties.getBucket(), "", lastMarker, 1000);
                            if (list == null || list._1 == null || list._2 == null) {
                                // 处理结束
                                log.add("处理结束，共有" +
                                        successCount +
                                        "个成功，共有" +
                                        failedCount +
                                        "个失败");
                                break;
                            }
                            // 如果都上传完了，用返回的marker
                            List<String> fileKeys = list._2;
                            fileKeys.parallelStream().forEach(key -> {
                                // 如果已停止，就不执行了

                                // 检查暂停状态，如果已暂停，则等待唤醒
                                if (pauseStatus.get()) {
                                    waitForNotify();
                                }
                                // 检查停止状态，如果已停止，则不处理
                                if (stopStatus.get()) {
                                    return;
                                }
                                try {
                                    action.accept(key);
                                } catch (Exception e) {
                                    failedCount.incrementAndGet();
                                    String message = e.getClass().getName() + "^^" + e.getMessage();
                                    addLog(key, message);
                                    failedReason.put(key, message);
                                }
                            });
                            lastMarker = list._1;
                        } catch (Exception e) {
                            addLog("发生错误", e.getClass().getName() + "^^" + e.getMessage());
                            if (successCount.get() == 0) {
                                JOptionPane.showMessageDialog(textArea1, "第一次查询就报错了，请检查参数",
                                        "检查参数",
                                        JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        }
                    }
                }
            });
        }

        private void addLog(String key, String message) {
            String e = key + "^^" + message;
            synchronized (log) {
                log.add(e);
            }
        }

        private void downloadAndUpload(String key, boolean override) throws IOException {
            File file = File.createTempFile("mig", "f");
            try {
                if (override) {
                    from.downloadToFile(fromProperties.getBucket(), key, file);
                    to.upload(toProperties.getBucket(), key, file);
                    addLog(key, "处理成功");
                } else {
                    if (!to.exist(toProperties.getBucket(), key)) {
                        from.downloadToFile(fromProperties.getBucket(), key, file);
                        to.upload(toProperties.getBucket(), key, file);
                        addLog(key, "处理成功");
                    } else {
                        addLog(key, "已存在");
                    }
                }
                successCount.incrementAndGet();

            } finally {
                file.delete();
            }
        }

        private void stop() {
            stopStatus.set(true);
            exe.shutdownNow();
            schedule.shutdownNow();
        }

        public void repushFailed() {
            // 处理失败的
            Map<String, String> failed = new HashMap<>();
            exe.execute(() -> failedReason.forEach((key, v) -> {
                // 检查暂停状态，如果已暂停，则等待唤醒
                if (pauseStatus.get()) {
                    waitForNotify();
                }
                try {
                    downloadAndUpload(key, 是否覆盖CheckBox.isSelected());
                    failedCount.decrementAndGet();
                } catch (Exception e) {
                    failed.put("key", e.getClass().getName() + "^^" + e.getMessage());
                }
            }));
            failedReason = failed;
        }

        private void waitForNotify() {
            synchronized (pauseLock) {
                try {
                    pauseLock.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }

        private void pauseOrResume() {
            if (pauseStatus.get()) {
                // 如果已暂停，则唤醒
                pauseStatus.set(false);
                synchronized (pauseLock) {
                    pauseLock.notifyAll();
                }
            } else {
                // 如果未暂停，则暂停
                pauseStatus.set(true);
            }
        }

        private void setFromProperties(StorageProperties fromProperties) {
            this.fromProperties = fromProperties;
        }

        private void setToProperties(StorageProperties toProperties) {
            this.toProperties = toProperties;
        }

        private void delete1() {
            listAndAction(key -> {
                from.delete(fromProperties.getBucket(), key);
                addLog(key, "删除成功");
            });
        }

        private void delete2() {
            listAndAction(key -> {
                to.delete(toProperties.getBucket(), key);
                successCount.incrementAndGet();
                addLog(key, "删除成功");
            });
        }
    }
}
