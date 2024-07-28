package com.example.tokenizer;

import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.index.analysis.TokenizerFactory;

import java.util.Map;
import java.util.HashMap;

public class HangulTokenizerPlugin extends Plugin implements AnalysisPlugin {

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> extraTokenizers = new HashMap<>();
        extraTokenizers.put("hangul_tokenizer", HangulTokenizerFactory::new);
        return extraTokenizers;
    }
}
