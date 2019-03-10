package com.stefanolupo.ndngame.backend.publisher;

import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.Face;
import net.named_data.jndn.util.Blob;

public class DataSend {

    private final Face face;
    private final SequenceNumberedName name;
    private final Blob blob;

    public DataSend(Face face, SequenceNumberedName name, Blob blob) {
        this.face = face;
        this.name = name;
        this.blob = blob;
    }

    public Face getFace() {
        return face;
    }

    public SequenceNumberedName getName() {
        return name;
    }

    public Blob getBlob() {
        return blob;
    }
}
