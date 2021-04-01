package jag.js5;

import jag.game.client;
import jag.statics.Statics31;
import jag.statics.Statics4;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BufferedFile {

    public static BufferedFile[] indexes;

    public static BufferedFile dataFile;
    public static BufferedFile indexFile;
    public static BufferedFile random;

    public static int indexCount;

    static {
        BufferedFile.random = null;
        BufferedFile.dataFile = null;
        BufferedFile.indexFile = null;
    }

    public final DiskFile file;
    public long aLong1897;
    public int anInt1895;
    public long aLong1896;
    public long aLong1894;
    public long caret;
    public long aLong1898;
    public int anInt1902;
    public byte[] aByteArray1893;
    public byte[] aByteArray1899;
    public long aLong1901;

    public BufferedFile(DiskFile file, int var2, int var3) throws IOException {
        this.file = file;
        aLong1898 = -1L;
        aLong1894 = -1L;
        anInt1902 = 0;
        aLong1897 = aLong1901 = file.length();
        aByteArray1899 = new byte[var2];
        aByteArray1893 = new byte[var3];
        caret = 0L;
    }

    public static void releaseAll() {
        try {
            dataFile.close();

            for (int i = 0; i < indexCount; ++i) {
                indexes[i].close();
            }

            indexFile.close();
            random.close();
        } catch (Exception ignored) {
        }

    }

    public static File openRw(String file) {
        if (!Statics4.aBoolean830) {
            throw new RuntimeException("");
        }
        File var1 = Statics4.rwFileCache.get(file);
        if (var1 != null) {
            return var1;
        }
        File var2 = new File(Statics4.aFile831, file);
        RandomAccessFile var3 = null;

        try {
            File var4 = new File(var2.getParent());
            if (!var4.exists()) {
                throw new RuntimeException("");
            }
            var3 = new RandomAccessFile(var2, "rw");
            int var5 = var3.read();
            var3.seek(0L);
            var3.write(var5);
            var3.seek(0L);
            var3.close();
            Statics4.rwFileCache.put(file, var2);
            return var2;
        } catch (Exception var8) {
            try {
                if (var3 != null) {
                    var3.close();
                }
            } catch (Exception ignored) {
            }

            throw new RuntimeException();
        }
    }

    public static void createRandom(byte[] var0, int var1) {
        if (client.random == null) {
            client.random = new byte[24];
        }

        Statics31.method1140(var0, var1, client.random, 0, 24);
    }

    public void read(byte[] var1, int var2, int var3) throws IOException {
        try {
            if (var3 + var2 > var1.length) {
                throw new ArrayIndexOutOfBoundsException(var3 + var2 - var1.length);
            }

            if (aLong1894 != -1L && caret >= aLong1894 && (long) var3 + caret <= (long) anInt1902 + aLong1894) {
                System.arraycopy(aByteArray1893, (int) (caret - aLong1894), var1, var2, var3);
                caret += var3;
                return;
            }

            long var4 = caret;
            int var7 = var3;
            int var8;
            if (caret >= aLong1898 && caret < aLong1898 + (long) anInt1895) {
                var8 = (int) ((long) anInt1895 - (caret - aLong1898));
                if (var8 > var3) {
                    var8 = var3;
                }

                System.arraycopy(aByteArray1899, (int) (caret - aLong1898), var1, var2, var8);
                caret += var8;
                var2 += var8;
                var3 -= var8;
            }

            if (var3 > aByteArray1899.length) {
                file.seek(caret);

                for (aLong1896 = caret; var3 > 0; var3 -= var8) {
                    var8 = file.read(var1, var2, var3);
                    if (var8 == -1) {
                        break;
                    }

                    aLong1896 += var8;
                    caret += var8;
                    var2 += var8;
                }
            } else if (var3 > 0) {
                method1405();
                var8 = Math.min(var3, anInt1895);

                System.arraycopy(aByteArray1899, 0, var1, var2, var8);
                var2 += var8;
                var3 -= var8;
                caret += var8;
            }

            if (aLong1894 != -1L) {
                if (aLong1894 > caret && var3 > 0) {
                    var8 = var2 + (int) (aLong1894 - caret);
                    if (var8 > var3 + var2) {
                        var8 = var3 + var2;
                    }

                    while (var2 < var8) {
                        var1[var2++] = 0;
                        --var3;
                        ++caret;
                    }
                }

                long var9 = -1L;
                long var11 = -1L;
                if (aLong1894 >= var4 && aLong1894 < (long) var7 + var4) {
                    var9 = aLong1894;
                } else if (var4 >= aLong1894 && var4 < aLong1894 + (long) anInt1902) {
                    var9 = var4;
                }

                if (aLong1894 + (long) anInt1902 > var4 && (long) anInt1902 + aLong1894 <= var4 + (long) var7) {
                    var11 = (long) anInt1902 + aLong1894;
                } else if (var4 + (long) var7 > aLong1894 && var4 + (long) var7 <= aLong1894 + (long) anInt1902) {
                    var11 = (long) var7 + var4;
                }

                if (var9 > -1L && var11 > var9) {
                    int var13 = (int) (var11 - var9);
                    System.arraycopy(aByteArray1893, (int) (var9 - aLong1894), var1, (int) (var9 - var4) + var2, var13);
                    if (var11 > caret) {
                        var3 = (int) ((long) var3 - (var11 - caret));
                        caret = var11;
                    }
                }
            }
        } catch (IOException var15) {
            aLong1896 = -1L;
            throw var15;
        }

        if (var3 > 0) {
            throw new EOFException();
        }
    }

    void method1411() throws IOException {
        if (aLong1894 != -1L) {
            if (aLong1896 != aLong1894) {
                file.seek(aLong1894);
                aLong1896 = aLong1894;
            }

            file.write(aByteArray1893, 0, anInt1902);
            aLong1896 += anInt1902;
            if (aLong1896 > aLong1901) {
                aLong1901 = aLong1896;
            }

            long var1 = -1L;
            long var3 = -1L;
            if (aLong1894 >= aLong1898 && aLong1894 < aLong1898 + (long) anInt1895) {
                var1 = aLong1894;
            } else if (aLong1898 >= aLong1894 && aLong1898 < aLong1894 + (long) anInt1902) {
                var1 = aLong1898;
            }

            if ((long) anInt1902 + aLong1894 > aLong1898 && (long) anInt1902 + aLong1894 <= (long) anInt1895 + aLong1898) {
                var3 = (long) anInt1902 + aLong1894;
            } else if ((long) anInt1895 + aLong1898 > aLong1894 && aLong1898 + (long) anInt1895 <= aLong1894 + (long) anInt1902) {
                var3 = (long) anInt1895 + aLong1898;
            }

            if (var1 > -1L && var3 > var1) {
                int var5 = (int) (var3 - var1);
                System.arraycopy(aByteArray1893, (int) (var1 - aLong1894), aByteArray1899, (int) (var1 - aLong1898), var5);
            }

            aLong1894 = -1L;
            anInt1902 = 0;
        }

    }

    public long method1409() {
        return aLong1897;
    }

    public void seek(long caret) throws IOException {
        if (caret < 0L) {
            throw new IOException("");
        }
        this.caret = caret;
    }

    public void write(byte[] var1, int var2, int var3) throws IOException {
        try {
            if (caret + (long) var3 > aLong1897) {
                aLong1897 = (long) var3 + caret;
            }

            if (aLong1894 != -1L && (caret < aLong1894 || caret > (long) anInt1902 + aLong1894)) {
                method1411();
            }

            if (aLong1894 != -1L && (long) var3 + caret > aLong1894 + (long) aByteArray1893.length) {
                int var4 = (int) ((long) aByteArray1893.length - (caret - aLong1894));
                System.arraycopy(var1, var2, aByteArray1893, (int) (caret - aLong1894), var4);
                caret += var4;
                var2 += var4;
                var3 -= var4;
                anInt1902 = aByteArray1893.length;
                method1411();
            }

            if (var3 <= aByteArray1893.length) {
                if (var3 > 0) {
                    if (aLong1894 == -1L) {
                        aLong1894 = caret;
                    }

                    System.arraycopy(var1, var2, aByteArray1893, (int) (caret - aLong1894), var3);
                    caret += var3;
                    if (caret - aLong1894 > (long) anInt1902) {
                        anInt1902 = (int) (caret - aLong1894);
                    }

                }
            } else {
                if (aLong1896 != caret) {
                    file.seek(caret);
                    aLong1896 = caret;
                }

                file.write(var1, var2, var3);
                aLong1896 += var3;
                if (aLong1896 > aLong1901) {
                    aLong1901 = aLong1896;
                }

                long var5 = -1L;
                long var7 = -1L;
                if (caret >= aLong1898 && caret < (long) anInt1895 + aLong1898) {
                    var5 = caret;
                } else if (aLong1898 >= caret && aLong1898 < caret + (long) var3) {
                    var5 = aLong1898;
                }

                if ((long) var3 + caret > aLong1898 && (long) var3 + caret <= aLong1898 + (long) anInt1895) {
                    var7 = caret + (long) var3;
                } else if (aLong1898 + (long) anInt1895 > caret && aLong1898 + (long) anInt1895 <= caret + (long) var3) {
                    var7 = (long) anInt1895 + aLong1898;
                }

                if (var5 > -1L && var7 > var5) {
                    int var9 = (int) (var7 - var5);
                    System.arraycopy(var1, (int) (var5 + (long) var2 - caret), aByteArray1899, (int) (var5 - aLong1898), var9);
                }

                caret += var3;
            }
        } catch (IOException var11) {
            aLong1896 = -1L;
            throw var11;
        }
    }

    void method1405() throws IOException {
        anInt1895 = 0;
        if (caret != aLong1896) {
            file.seek(caret);
            aLong1896 = caret;
        }

        int var2;
        for (aLong1898 = caret; anInt1895 < aByteArray1899.length; anInt1895 += var2) {
            int var1 = aByteArray1899.length - anInt1895;
            if (var1 > 200000000) {
                var1 = 200000000;
            }

            var2 = file.read(aByteArray1899, anInt1895, var1);
            if (var2 == -1) {
                break;
            }

            aLong1896 += var2;
        }

    }

    public void close() throws IOException {
        method1411();
        file.close();
    }

    public void read(byte[] var1) throws IOException {
        read(var1, 0, var1.length);
    }
}