package com.example.tokenizer;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HangulTokenizer extends Tokenizer {

    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);
    private final StringBuilder buffer = new StringBuilder();
    private int finalOffset;

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        buffer.setLength(0);

        int startOffset = -1;
        int endOffset = -1;

        while (true) {
            final int character = input.read();
            if (character == -1) {
                if (buffer.length() > 0) {
                    endOffset = finalOffset;
                    break;
                } else {
                    finalOffset = correctOffset(finalOffset);
                    return false;
                }
            }

            finalOffset++;
            if (Character.isWhitespace(character)) {
                if (buffer.length() > 0) {
                    endOffset = finalOffset - 1;
                    break;
                } else {
                    continue;
                }
            }

            if (startOffset == -1) {
                startOffset = finalOffset - 1;
            }

            buffer.append((char) character);
        }

        String token = buffer.toString();
        List<String> tokens = decomposeHangul(token);

        for (String tok : tokens) {
            termAttr.setEmpty().append(tok);
            offsetAttr.setOffset(correctOffset(startOffset), correctOffset(endOffset));
            return true;
        }

        return false;
    }

    private List<String> decomposeHangul(String input) {
        List<String> tokens = new ArrayList<>();
        List<String> choList = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch >= 0xAC00 && ch <= 0xD7A3) {
                int base = ch - 0xAC00;
                int choIndex = base / (21 * 28);
                int jungIndex = (base % (21 * 28)) / 28;
                int jongIndex = base % 28;

                char cho = (char) (0x1100 + choIndex);
                char jung = (char) (0x1161 + jungIndex);
                char jong = jongIndex != 0 ? (char) (0x11A7 + jongIndex) : 0;


                // 초성, 중성, 종성 분리
                tokens.add(current.toString() + cho);  // 초성
                choList.add(String.valueOf(cho));
                tokens.add(current.toString() + cho + jung);  // 초성 + 중성

                if (jong != 0) {
                    tokens.add(current.toString() + cho + jung + jong);  // 초성 + 중성 + 종성
                }

                // 전체 음절 추가
                current.append(ch);
            } else {
                if (!String.valueOf(input.charAt(i)).trim().isEmpty()) {
                    choList.add(String.valueOf(input.charAt(i)));
                }
                current.append(ch);
                tokens.add(current.toString());
            }
        }

        if (choList.size() > 1) {
            tokens.addAll(choList.subList(1, choList.size()));
        }

        return tokens;
    }

    private String createToken(String... parts) {
        StringBuilder token = new StringBuilder();
        for (String part : parts) {
            token.append(part);
        }
        return token.toString();
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        finalOffset = 0;
    }

    @Override
    public void end() throws IOException {
        super.end();
        offsetAttr.setOffset(finalOffset, finalOffset);
    }
}
