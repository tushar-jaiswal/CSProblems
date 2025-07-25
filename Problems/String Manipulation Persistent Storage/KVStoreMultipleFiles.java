//  Author: Tushar Jaiswal
//  Creation Date: 2025-07-25

//  Implement a KVStore class that supports storing and retrieving key-value pairs, as well as persisting data to the filesystem and restoring it. The class should include the following methods:
//  Things to note:
//      Both key and value are strings and may contain any characters, including newlines.
//      You must not use Python’s built-in serialization libraries (such as json).
//      The main focus of the problem is implementing your own serialization/deserialization methods.

//  2nd question How to write multiple files when a file cannot exceed one KB.

import java.io.*;
import java.util.*;

public class KVStore {
    private static final String FILENAME_PREFIX = "kvstore";
    private static final int MAX_FILE_SIZE = 1024; // 1KB
    private Map<String, String> store;

    public KVStore() {
        store = new HashMap<>();
    }

    public String getValue(String key) {
        if (!store.containsKey(key)) {
            throw new NoSuchElementException("Key not found: " + key);
        }
        return store.get(key);
    }

    public void setValue(String key, String val) {
        store.put(key, val);
    }

    public void clear() {
        store.clear();
    }

    public void persistToDisk() throws IOException {
        String encoding = encode();
        
        // Delete existing chunk files
        File dir = new File(".");
        File[] oldFiles = dir.listFiles((d, name) -> name.startsWith(FILENAME_PREFIX + "_"));
        if (oldFiles != null) {
            for (File f : oldFiles) {
                f.delete();
            }
        }

        // Write chunks
        for (int i = 0; i < encoding.length(); i += MAX_FILE_SIZE) {
            String chunk = encoding.substring(i, Math.min(i + MAX_FILE_SIZE, encoding.length()));
            String filename = FILENAME_PREFIX + "_" + (i / MAX_FILE_SIZE);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(chunk);
            }
        }
    }

    private String encode() {
        StringBuilder encoding = new StringBuilder();
        for (Map.Entry<String, String> entry : store.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            encoding.append(key.length()).append(".").append(key);
            encoding.append(val.length()).append(".").append(val);
        }
        return encoding.toString();
    }

    public void restoreFromDisk() throws IOException {
        List<String> chunks = new ArrayList<>();

        // Match files like kvstore_0, kvstore_1, ...
        Pattern pattern = Pattern.compile(FILENAME_PREFIX + "_(\\d+)");
        File[] files = new File(".").listFiles((dir, name) -> pattern.matcher(name).matches());

        if (files == null || files.length == 0) return;

        // Sort files based on the numeric suffix
        Arrays.sort(files, Comparator.comparingInt(file -> {
            Matcher m = pattern.matcher(file.getName());
            if (m.matches()) {
                return Integer.parseInt(m.group(1));
            }
            return Integer.MAX_VALUE;
        }));

        for (File file : files) {
            chunks.add(new String(Files.readAllBytes(file.toPath())));
        }

        String encoding = String.join("", chunks);
        decode(encoding);
    }

    private void decode(String encoding) {
        int i = 0;
        while (i < encoding.length()) {
            int delimIndex = encoding.indexOf(".", i);
            int keyLen = Integer.parseInt(encoding.substring(i, delimIndex));
            int keyStart = delimIndex + 1;
            int keyEnd = keyStart + keyLen;
            String key = encoding.substring(keyStart, keyEnd);

            i = keyEnd;
            delimIndex = encoding.indexOf(".", i);
            int valLen = Integer.parseInt(encoding.substring(i, delimIndex));
            int valStart = delimIndex + 1;
            int valEnd = valStart + valLen;
            String val = encoding.substring(valStart, valEnd);

            store.put(key, val);
            i = valEnd;
        }
    }

    public static void main(String[] args) throws IOException {
        KVStore kvstore = new KVStore();

        // Set and Get
        kvstore.setValue("a", "1");
        assert kvstore.getValue("a").equals("1");

        // Exception check (simulate KeyError)
        try {
            kvstore.getValue("b");
            throw new AssertionError("Expected exception not thrown for key 'b'");
        } catch (NoSuchElementException e) {
            // Expected
        }

        kvstore.setValue("bas", "123");
        kvstore.persistToDisk();
        kvstore.clear();
        kvstore.restoreFromDisk();

        assert kvstore.getValue("a").equals("1");
        assert kvstore.getValue("bas").equals("123");
        
        // Force multiple file chunks
        for (int i = 0; i < 100; i++) {
            kvstore.setValue("key" + i, "v".repeat(20));
        }

        kvstore.persistToDisk();
        kvstore.clear();
        kvstore.restoreFromDisk();

        for (int i = 0; i < 100; i++) {
            assert kvstore.getValue("key" + i).equals("v".repeat(20));
        }

        System.out.println("All tests passed");
    }
}
