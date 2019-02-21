package com.stefanolupo.ndngame.backend.ndn;

import com.stefanolupo.ndngame.backend.publisher.BasePublisher;
import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.Interest;

import java.util.function.Function;

public interface BasePublisherFactory {
    BasePublisher create(SequenceNumberedName syncName, Function<Interest, SequenceNumberedName> interestTFunction);
}
