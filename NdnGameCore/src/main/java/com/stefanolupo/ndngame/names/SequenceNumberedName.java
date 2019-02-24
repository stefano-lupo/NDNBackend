package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

public interface SequenceNumberedName {
    Name getFullName();
    Interest buildInterest();
    long getLatestSequenceNumberSeen();
    void setNextSequenceNumber(long nextSequenceNumber);
}
