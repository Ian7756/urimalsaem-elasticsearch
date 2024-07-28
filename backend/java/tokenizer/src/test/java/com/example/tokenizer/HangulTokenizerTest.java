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
        StringReader reader = new StringReader("망아지");
        HangulTokenizer tokenizer = new HangulTokenizer();
        tokenizer.setReader(reader);
        tokenizer.reset();

        CharTermAttribute charTermAttr = tokenizer.getAttribute(CharTermAttribute.class);

        // Check the first token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("h"));

        // Check the second token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("e"));

        // Check the third token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("l"));

        // Check the fourth token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("l"));

        // Check the fifth token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("o"));

        // Check the sixth token (space character)
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is(" "));

        // Check the seventh token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("w"));

        // Check the eighth token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("o"));

        // Check the ninth token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("r"));

        // Check the tenth token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("l"));

        // Check the eleventh token
        assertTrue(tokenizer.incrementToken());
        assertThat(charTermAttr.toString(), is("d"));

        // Ensure no more tokens are present
        assertFalse(tokenizer.incrementToken());

        tokenizer.end();
        tokenizer.close();
    }
}