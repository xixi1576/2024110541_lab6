package com.example.a2024110541lab6;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 对象序列化存储工具。
 *
 * <p>写文件使用 {@link Context#openFileOutput}，读文件使用 {@link Context#openFileInput}，
 * 对象通过 {@link ObjectOutputStream} / {@link ObjectInputStream} 以二进制形式保存到
 * 应用私有目录 {@code /data/data/包名/files/} 下，对象必须实现 {@link Serializable} 接口。</p>
 */
public final class ObjectStore {

    private static final String TAG = "ObjectStore";

    private ObjectStore() {
    }

    /**
     * 把可序列化对象写入应用私有文件。
     */
    public static void write(Context context, String fileName, Serializable data) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE))) {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "写入序列化文件失败：" + fileName, e);
        }
    }

    /**
     * 从应用私有文件读回对象，文件不存在或读取失败时返回 {@code null}。
     */
    public static Object read(Context context, String fileName) {
        if (!context.getFileStreamPath(fileName).exists()) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(context.openFileInput(fileName))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "读取序列化文件失败：" + fileName, e);
            return null;
        }
    }
}
