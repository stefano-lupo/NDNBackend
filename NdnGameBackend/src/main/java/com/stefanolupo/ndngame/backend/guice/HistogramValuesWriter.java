package com.stefanolupo.ndngame.backend.guice;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class HistogramValuesWriter {

    private static final String HISTOGRAM_VALUES_FILE_NAME = "histogram_values.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final File valuesFile;
    private final MetricRegistry metricRegistry;

    @Inject
    public HistogramValuesWriter(@BackendMetrics MetricRegistry metricRegistry,
                                 @Named(BackendModule.METRICS_DIR) File metricsDir) {
        this.metricRegistry = metricRegistry;

        valuesFile = new File(metricsDir, HISTOGRAM_VALUES_FILE_NAME);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::writeValues, 10, 10, TimeUnit.SECONDS);
    }

    private void writeValues() {
         Map<String, List<Long>> map = metricRegistry.getHistograms().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> fromArray(e.getValue().getSnapshot().getValues())
                ));
         try {
             OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(valuesFile, map);
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
    }

    private List<Long> fromArray(long[] arr) {
        return Arrays.stream(arr)
                .boxed()
                .collect(Collectors.toList());
    }
}
