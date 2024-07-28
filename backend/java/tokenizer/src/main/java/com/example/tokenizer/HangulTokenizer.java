package com.example.tokenizer;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HangulTokenizer extends Tokenizer {

    protected final char[] CHOSUNG_ARR = {
            0x3131, 0x3132, 0x3134, 0x3137, 0x3138,     // ㄱ, ㄲ, ㄴ, ㄷ, ㄸ
            0x3139, 0x3141, 0x3142, 0x3143, 0x3145,     // ㄹ, ㅁ, ㅂ, ㅃ, ㅅ
            0x3146, 0x3147, 0x3148, 0x3149, 0x314A,     // ㅆ, ㅇ, ㅈ, ㅉ, ㅊ
            0x314B, 0x314C, 0x314D, 0x314E              // ㅋ, ㅌ, ㅍ, ㅎ
    };

    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);
    private final List<Token> tokens = new ArrayList<>();
    private int tokenIndex = 0;

    @Override
    final public boolean incrementToken() throws IOException {
        clearAttributes();

        if (tokenIndex < tokens.size()) {
            Token token = tokens.get(tokenIndex++);
            termAttr.append(token.term);
            offsetAttr.setOffset(0, token.endOffset);
            return true;
        }

        tokens.clear();
        tokenIndex = 0;

        int character;
        StringBuilder buffer = new StringBuilder();

        while ((character = input.read()) != -1) {
            if (Character.isWhitespace(character)) {
                if (!buffer.isEmpty()) {
                    decomposeHangul(buffer.toString());
                    buffer.setLength(0);
                }
            } else {
                buffer.append((char) character);
            }
        }

        if (!buffer.isEmpty()) {
            decomposeHangul(buffer.toString());
        }

        if (tokenIndex < tokens.size()) {
            return incrementToken();
        } else {
            return false;
        }
    }

    private void decomposeHangul(String input) {
        int offset = 0;
        StringBuilder buffer = new StringBuilder();
        StringBuilder choStr = new StringBuilder();

        for (char ch : input.toCharArray()) {
            if (ch >= 0xAC00 && ch <= 0xD7A3) {  // 한글 음절인지 확인
                char base = (char) (ch - 0xAC00);
                int choIndex = base / (21 * 28);
                int jungIndex = (base % (21 * 28)) / 28;
                int jongIndex = base % 28;

                String cho = String.valueOf(CHOSUNG_ARR[choIndex]);  // 초성
                choStr.append(cho);

                String jung = String.valueOf((char) (0xAC00 + (choIndex * 21 * 28) + (jungIndex * 28)));  // 중성
                String jong = String.valueOf((char) (0xAC00 + (choIndex * 21 * 28) + (jungIndex * 28) + jongIndex));  // 종성


                tokens.add(new Token(buffer + cho, offset, offset + 1));
                tokens.add(new Token(buffer + jung, offset, offset + 1));

                if (jongIndex != 0) {
                    tokens.add(new Token(buffer + jong, offset, offset + 1));
                }

                // 첫글자 제외
                if (choStr.length() > 1) {
                    tokens.add(new Token(choStr.toString(), offset, offset + 1));
                }
            } else {
                choStr.append(ch);
                tokens.add(new Token(String.valueOf(ch), offset, offset + 1));
            }
            buffer.append(ch);
            offset++;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokens.clear();
        tokenIndex = 0;
    }

    @Override
    public void end() throws IOException {
        super.end();
        int finalOffset = correctOffset(tokenIndex);
        offsetAttr.setOffset(finalOffset, finalOffset);
    }

    private static class Token {
        final String term;
        final int endOffset;

        Token(String term, int startOffset, int endOffset) {
            this.term = term;
            this.endOffset = endOffset;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "term='" + term + '\'' +
                    ", endOffset=" + endOffset +
                    '}';
        }
    }
}
