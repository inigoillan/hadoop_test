package com.aol.logprocessor.hadoop;

import com.aol.logprocessor.builders.impl.CountersBuilder;
import com.aol.logprocessor.configuration.Config;
import com.aol.logprocessor.hadoop.datatypes.AggregationKey;
import com.aol.logprocessor.hadoop.datatypes.CountAggregation;
import com.aol.logprocessor.hadoop.datatypes.KeyValueFactory;
import com.aol.logprocessor.hadoop.datatypes.MergeException;
import com.aol.logprocessor.printer.CSVPrinter;
import com.aol.logprocessor.printer.Printer;
import com.google.common.collect.Iterables;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reducer for the Log Processing MapReduce program
 *
 * @author <a href="mailto:inigo.illan@gmail.com">Inigo Illan</a>
 * @since 1.0
 */
public class ProcessorReducer extends Reducer<AggregationKey, CountAggregation, NullWritable, Text> {
    private final static Logger LOG = Logger.getLogger(ProcessorReducer.class);
    private Printer printer;
    private final Text text = new Text();
    private CountersBuilder<CountAggregation> countersBuilder;

    @Override
    protected void setup(Context context) throws InterruptedException, IOException {
        String configFilename = context.getConfiguration().get("com.aol.logprocessor.hadoop.config.file");

        try (InputStream configFile = new FileInputStream(configFilename)) {
            Config config = new Config(configFile);

            printer = new CSVPrinter(config);

            KeyValueFactory factory = new KeyValueFactory();
            countersBuilder = new CountersBuilder(config, factory);
        }

    }

    @Override
    protected void reduce(AggregationKey key, Iterable<CountAggregation> values, Context context) throws InterruptedException, IOException {
        CountAggregation countAggregation = countersBuilder.buildZeroCounters();

        try {
            CountAggregation mergedCounters = countAggregation.merge(values);

            String printed = printer.print(key, mergedCounters);

            text.set(printed);

            context.write(NullWritable.get(), text);

        } catch (MergeException e) {
            LOG.error(String.format("Merge exception for key %s", key));
            LOG.error(String.format("With values %s", Iterables.toString(values)));
        }


    }
}
