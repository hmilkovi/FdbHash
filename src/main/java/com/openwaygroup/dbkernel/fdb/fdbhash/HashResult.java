package com.openwaygroup.dbkernel.fdb.fdbhash;

import java.util.zip.CRC32;
import java.text.MessageFormat;

public class HashResult implements RangeResult {

  private final CRC32 crc32 = new CRC32();
  
  byte[] keyFrom;
  byte[] keyTo;
  long cnt;
  long keyHash;
  long valueHash;
  
  public void init(byte[] keyFrom, byte[] keyTo) {
    this.keyFrom = keyFrom;
    this.keyTo = keyTo;
    cnt = 0;
    keyHash = 0;
    valueHash = 0;
  }

  public synchronized void processKv(final byte[] key, final byte[] value) {
    cnt ++;
    keyHash ^= bytesHash(key);
    valueHash ^= bytesHash(value);
  }
  
  private long bytesHash(final byte[] bytes) {
    crc32.reset();
    crc32.update(bytes);
    return crc32.getValue();
  }

  public synchronized void mergeFrom(RangeResult anotherRes)  {
    final HashResult a = (HashResult) anotherRes;
    
    cnt += a.cnt;
    keyHash ^= a.keyHash;
    valueHash ^= a.valueHash;
  }

  public String toString() {
    return RangeResult.toString(keyFrom, keyTo, getResStr());
  }
  
  public String getResStr() {
    return MessageFormat.format(
      "{0, number, #} {1, number, #} {2, number, #}",
      cnt, keyHash, valueHash
    );
  }
 
}
