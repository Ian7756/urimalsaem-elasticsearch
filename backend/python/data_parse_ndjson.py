import json
import os

folder_path = "./dictionary"
ids = set()
words = []

for filename in os.listdir(folder_path):
    if filename.endswith(".json"):  # 파일이 .json 확장자로 끝나는지 확인
        file_path = os.path.join(folder_path, filename)
        with open(file_path, 'r') as file:
            data = json.load(file)

        for item in data.get('channel', {}).get('item', []):
            if item.get('target_code') in ids:
                print(item['target_code'])
            else:
                ids.add(item.get('target_code', ''))
                word = {
                    'id': item.get('target_code', ''),
                    'word': item.get('wordinfo', {}).get('word', ''),
                    'definition': item.get('senseinfo', {}).get('definition', ''),
                    'no': item.get('senseinfo', {}).get('sense_no', ''),
                    'part': item.get('senseinfo', {}).get('pos', ''),
                }
                words.append({'index': {'_index': 'dictionary-v2', '_id': item['target_code']}})
                words.append(word)

with open('./words01.ndjson', 'w', encoding='utf-8') as file:
    for obj in words:
        file.write(json.dumps(obj, ensure_ascii=False) + '\n')

print("데이터가 성공적으로 저장되었습니다.")
