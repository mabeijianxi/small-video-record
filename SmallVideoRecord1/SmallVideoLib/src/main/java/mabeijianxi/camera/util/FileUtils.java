package mabeijianxi.camera.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {

    /**
     * 拼接路径
     * concatPath("/mnt/sdcard", "/DCIM/Camera")  	=>		/mnt/sdcard/DCIM/Camera
     * concatPath("/mnt/sdcard", "DCIM/Camera")  	=>		/mnt/sdcard/DCIM/Camera
     * concatPath("/mnt/sdcard/", "/DCIM/Camera")  =>		/mnt/sdcard/DCIM/Camera
     */
    public static String concatPath(String... paths) {
        StringBuilder result = new StringBuilder();
        if (paths != null) {
            for (String path : paths) {
                if (path != null && path.length() > 0) {
                    int len = result.length();
                    boolean suffixSeparator = len > 0 && result.charAt(len - 1) == File.separatorChar;//后缀是否是'/'
                    boolean prefixSeparator = path.charAt(0) == File.separatorChar;//前缀是否是'/'
                    if (suffixSeparator && prefixSeparator) {
                        result.append(path.substring(1));
                    } else if (!suffixSeparator && !prefixSeparator) {//补前缀
                        result.append(File.separatorChar);
                        result.append(path);
                    } else {
                        result.append(path);
                    }
                }
            }
        }
        return result.toString();
    }

    /**
     * 计算文件的md5值
     */
    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e("FileUtils", "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e("FileUtils", "Exception while getting FileInputStream", e);
            return null;
        }

        //DigestInputStream

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("FileUtils", "Exception on closing MD5 input stream", e);
            }
        }
    }

    /**
     * 计算文件的md5值
     */
    public static String calculateMD5(File updateFile, int offset, int partSize) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e("FileUtils", "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e("FileUtils", "Exception while getting FileInputStream", e);
            return null;
        }

        //DigestInputStream
        final int buffSize = 8192;//单块大小
        byte[] buffer = new byte[buffSize];
        int read;
        try {
            if (offset > 0) {
                is.skip(offset);
            }
            int byteCount = Math.min(buffSize, partSize), byteLen = 0;
            while ((read = is.read(buffer, 0, byteCount)) > 0 && byteLen < partSize) {
                digest.update(buffer, 0, read);
                byteLen += read;
                //检测最后一块，避免多读数据
                if (byteLen + buffSize > partSize) {
                    byteCount = partSize - byteLen;
                }
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("FileUtils", "Exception on closing MD5 input stream", e);
            }
        }
    }

    /**
     * 检测文件是否可用
     */
    public static boolean checkFile(File f) {
        if (f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0))) {
            return true;
        }
        return false;
    }

    /**
     * 检测文件是否可用
     */
    public static boolean checkFile(String path) {
        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);
            if (f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0)))
                return true;
        }
        return false;
    }

    /**
     * 获取sdcard路径
     */
    public static String getExternalStorageDirectory() {
        String path = Environment.getExternalStorageDirectory().getPath();
        if (DeviceUtils.isZte()) {
            //			if (!Environment.getExternalStoragePublicDirectory(
            //					Environment.DIRECTORY_DCIM).exists()) {
            path = path.replace("/sdcard", "/sdcard-ext");
            //			}
        }
        return path;
    }

    public static long getFileSize(String fn) {
        File f = null;
        long size = 0;

        try {
            f = new File(fn);
            size = f.length();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            f = null;
        }
        return size < 0 ? null : size;
    }

    public static long getFileSize(File fn) {
        return fn == null ? 0 : fn.length();
    }

    public static String getFileType(String fn, String defaultType) {
        FileNameMap fNameMap = URLConnection.getFileNameMap();
        String type = fNameMap.getContentTypeFor(fn);
        return type == null ? defaultType : type;
    }

    public static String getFileType(String fn) {
        return getFileType(fn, "application/octet-stream");
    }

    public static String getFileExtension(String filename) {
        String extension = "";
        if (filename != null) {
            int dotPos = filename.lastIndexOf(".");
            if (dotPos >= 0 && dotPos < filename.length() - 1) {
                extension = filename.substring(dotPos + 1);
            }
        }
        return extension.toLowerCase();
    }

    public static boolean deleteFile(File f) {
        if (f != null && f.exists() && !f.isDirectory()) {
            return f.delete();
        }
        return false;
    }

    public static void deleteDir(File f) {
        if (f != null && f.exists() && f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (file.isDirectory())
                    deleteDir(file);
                file.delete();
            }
            f.delete();
        }
    }

    public static void deleteCacheFile(String f) {
        if (f != null && f.length() > 0) {
            File files = new File(f);
            if (files.exists() && files.isDirectory()) {
                for (File file : files.listFiles()) {
                    if (!file.isDirectory() && (file.getName().contains(".ts") || file.getName().contains("temp"))) {
                        file.delete();
                    }

                }
            }
        }
    }
    public static void deleteCacheFile2TS(String f) {
        if (f != null && f.length() > 0) {
            File files = new File(f);
            if (files.exists() && files.isDirectory()) {
                for (File file : files.listFiles()) {
                    if (!file.isDirectory() && (file.getName().contains(".ts"))) {
                        file.delete();
                    }

                }
            }
        }
    }
    public static void deleteDir(String f) {
        if (f != null && f.length() > 0) {
            deleteDir(new File(f));
        }
    }

    public static boolean deleteFile(String f) {
        if (f != null && f.length() > 0) {
            return deleteFile(new File(f));
        }
        return false;
    }

    /**
     * read file
     *
     * @param filePath
     * @param charsetName The name of a supported {@link java.nio.charset.Charset
     *                    </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static String readFile(File file, String charsetName) {
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return fileContent.toString();
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
        return fileContent.toString();
    }

    public static String readFile(String filePath, String charsetName) {
        return readFile(new File(filePath), charsetName);
    }

    public static String readFile(File file) {
        return readFile(file, "utf-8");
    }

    /**
     * 文件拷贝
     *
     * @param from
     * @param to
     * @return
     */
    public static boolean fileCopy(String from, String to) {
        boolean result = false;

        int size = 1 * 1024;

        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);
            byte[] buffer = new byte[size];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
        return result;
    }
}
