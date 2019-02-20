package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Name;

public interface HasNameWithSequenceNumber {
    Name getNameWithSequenceNumber();
    Name getNameWithoutSequenceNumber();
//    long getNewSequenceNumber();
    long getSequenceNumber();
//    void setSequenceNumber(long sequenceNumber);
}
