package com.example.tokenizer;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.test.ESTestCase;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;

public class HangulTokenizerTest extends ESTestCase {
    @Test
    public void testTokenizer() throws IOException {
        StringReader reader = new StringReader("가");
        HangulTokenizer tokenizer = new HangulTokenizer();
        tokenizer.setReader(reader);
        tokenizer.reset();

        CharTermAttribute charTermAttr = tokenizer.getAttribute(CharTermAttribute.class);

        // Check the first token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("ㄱ"));

        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("가"));

        // Ensure no more tokens are present
        assertFalse(tokenizer.incrementToken());

        tokenizer.end();
        tokenizer.close();
    }
}