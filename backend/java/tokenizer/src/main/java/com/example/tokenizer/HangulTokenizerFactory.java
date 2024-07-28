package com.example.tokenizer;

import com.example.tokenizer.HangulTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class HangulTokenizerFactory extends AbstractTokenizerFactory {

    public HangulTokenizerFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, settings, name);
    }

    @Override
    public Tokenizer create() {
        return new HangulTokenizer();
    }
}
