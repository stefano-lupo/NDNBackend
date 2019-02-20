package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

public interface SequenceNumberedName {
    Name getListenName();
    Name getFullName();

    Interest buildInterest();
    long getLatestSequenceNumberSeen();
    void setNextSequenceNumber(long nextSequenceNumber);
}
