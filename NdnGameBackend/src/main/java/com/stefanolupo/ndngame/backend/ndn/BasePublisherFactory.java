package com.stefanolupo.ndngame.backend.ndn;

import com.stefanolupo.ndngame.backend.publisher.BasePublisher;
import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.function.Function;

public interface BasePublisherFactory {
    BasePublisher create(Name syncName, Function<Interest, SequenceNumberedName> interestTFunction);
}
